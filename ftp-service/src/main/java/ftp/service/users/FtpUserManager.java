package ftp.service.users;

import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class FtpUserManager implements UserManager {

    private static final AtomicReference<FtpUserManager> ROOT_FS = new AtomicReference<>();

    private final FtpUserRepository ftpUserRepository;

    private final File rootFileSystem;

    @Autowired
    public FtpUserManager(FtpUserRepository ftpUserRepository,
                  @Value("${ftp.root:${HOME}/Desktop/root}") File rootFileSystem) {
        this.ftpUserRepository = ftpUserRepository;
        this.rootFileSystem = rootFileSystem;
        ROOT_FS.compareAndSet(null, this);
    }

    /*
     * TODO: Hopefully this and the other method of almost the same name won't be required
     * TODO: once we decouple our {@link FtpUserManager} from a normal file-system.
     */
    public static File getHomeDirectory(String username) {
        FtpUserManager ftpUserManager = ROOT_FS.get();
        return ftpUserManager.getHomeDirectoryFor(username);
    }

    public File getHomeDirectoryFor(String username) {
        return this.ftpUserRepository.findByUsername(username)
                .map(u -> new File(this.rootFileSystem, u.getWorkspace()))
                .orElseThrow(() -> new IllegalArgumentException(username + " doesn't exist"));
    }

    @Override
    public User getUserByName(String s) throws FtpException {
        return this.ftpUserRepository.findByUsername(s).orElse(null);
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        List<String> collect = this.ftpUserRepository.findAll()
                .stream()
                .map(FtpUser::getName)
                .collect(Collectors.toList());
        return collect.toArray(new String[collect.size()]);
    }

    @Override
    public void delete(String s) throws FtpException {
        this.ftpUserRepository.findByUsername(s).ifPresent(this.ftpUserRepository::delete);
    }

    @Override
    public void save(User user) throws FtpException {
        Class<FtpUser> ftpUserClass = FtpUser.class;
        Assert.isInstanceOf(ftpUserClass, user);
        FtpUser ftpUser = ftpUserClass.cast(user);
        this.ftpUserRepository.save(ftpUser);
    }

    @Override
    public boolean doesExist(String s) throws FtpException {
        return this.ftpUserRepository.findByUsername(s).isPresent();
    }

    private void assertAboutAuthentication(boolean condition, String msg) throws AuthenticationFailedException {
        if (!condition) throw new AuthenticationFailedException(msg);
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        assertAboutAuthentication(authentication != null, "the authentication must not be null");
        Class<UsernamePasswordAuthentication> authenticationClass = UsernamePasswordAuthentication.class;
        assertAboutAuthentication(authenticationClass.isAssignableFrom(authentication.getClass()),
                "you must login using a username and password");
        UsernamePasswordAuthentication auth = authenticationClass.cast(authentication);
        assertAboutAuthentication(StringUtils.hasText(auth.getUsername()), "you must provide a username");
        assertAboutAuthentication(StringUtils.hasText(auth.getPassword()), "you must provide a password");
        return this.ftpUserRepository.findByUsername(auth.getUsername())
                .filter(u -> u.getPassword().equals(auth.getPassword()))
                .orElseThrow(() -> new AuthenticationFailedException(
                        "the provided username and password is invalid"));
    }

    @Override
    public String getAdminName() throws FtpException {
        return "admin";
    }

    @Override
    public boolean isAdmin(String s) throws FtpException {
        return this.ftpUserRepository.findByUsername(s)
                .map(FtpUser::isAdmin)
                .orElse(false);
    }
}

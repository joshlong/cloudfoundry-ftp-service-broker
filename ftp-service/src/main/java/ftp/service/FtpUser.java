package ftp.service;

import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "FTP_USER")
public class FtpUser implements User {

    @Transient
    private List<Authority> adminAuthorities = Collections.singletonList(new WritePermission());

    @Transient
    private List<Authority> anonAuthorities = Arrays.asList(
            new ConcurrentLoginPermission(20, 2),
            new TransferRatePermission(4800, 4800));

    @Id
    private String username;
    private String workspace; // users in the same workspace will be able to share the same file system
    private boolean admin;
    private String password;
    private int maxIdleTime = 0; // no limit
    private boolean enabled;

    FtpUser() {
    }

    public FtpUser(String ws, String username, String password, boolean admin) {
        this(ws, username, password, admin, 0, true);
    }

    public FtpUser(String workspace, String username, String password, boolean admin,
                   int maxIdleTime, boolean enabled) {
        this.username = username;
        this.workspace = workspace;
        this.admin = admin;
        this.password = password;
        this.maxIdleTime = maxIdleTime;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FtpUser{");
        sb.append("adminAuthorities=").append(adminAuthorities);
        sb.append(", anonAuthorities=").append(anonAuthorities);
        sb.append(", username='").append(username).append('\'');
        sb.append(", workspace='").append(workspace).append('\'');
        sb.append(", admin=").append(admin);
        sb.append(", password='").append(password).append('\'');
        sb.append(", maxIdleTime=").append(maxIdleTime);
        sb.append(", enabled=").append(enabled);
        sb.append('}');
        return sb.toString();
    }

    public String getWorkspace() {
        return workspace;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public List<Authority> getAuthorities() {
        List<Authority> authorities = new ArrayList<>();
        authorities.addAll(this.anonAuthorities);
        if (this.admin) {
            authorities.addAll(this.adminAuthorities);
        }
        return authorities;
    }

    @Override
    public List<Authority> getAuthorities(Class<? extends Authority> aClass) {
        return this.getAuthorities().stream()
                .filter(a -> a.getClass().equals(aClass))
                .collect(Collectors.toList());
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        return this.getAuthorities()
                .stream()
                .filter(a -> a.canAuthorize(authorizationRequest))
                .map(a -> a.authorize(authorizationRequest))
                .filter(a -> a != null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public String getHomeDirectory() {
        String homeDir = FtpUserManager.getHomeDirectory(this.username)
                .getAbsolutePath();
        LogFactory.getLog(getClass()).info("home-directory: " + homeDir);
        return homeDir;
    }

}

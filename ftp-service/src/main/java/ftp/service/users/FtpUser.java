package ftp.service.users;

import ftp.service.nodes.FtpServerNode;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.data.domain.Modifiable;

import java.util.*;
import java.util.stream.Collectors;


public class FtpUser implements User, Modifiable<String> {


    private transient boolean persisted;

    private List<Authority> adminAuthorities = Collections.singletonList(new WritePermission());

    private List<Authority> anonAuthorities = Arrays.asList(
            new ConcurrentLoginPermission(20, 2),
            new TransferRatePermission(4800, 4800));

    private String username;
    private String workspace;
    private boolean admin;
    private String password;
    private int maxIdleTime = 0;
    private boolean enabled;


    private final FtpServerNode ftpServerNode;

    public FtpServerNode getFtpServerNode() {
        return ftpServerNode;
    }

    public FtpUser(String workspace, String username, String password, boolean admin,
            int maxIdleTime, boolean enabled, FtpServerNode node, boolean persisted) {
        this(workspace, username, password, admin, maxIdleTime, enabled, node);
        this.setPersisted(persisted);
    }

     public FtpUser(String workspace, String username, String password, boolean admin,
                   int maxIdleTime, boolean enabled, FtpServerNode node) {
        this.username = username;
        this.workspace = workspace;
        this.ftpServerNode = node;
        this.admin = admin;
        this.password = password;
        this.maxIdleTime = maxIdleTime;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        FtpUser ftpUser = FtpUser.class.cast(o);
        return Objects.equals(admin, ftpUser.admin) &&
                Objects.equals(maxIdleTime, ftpUser.maxIdleTime) &&
                Objects.equals(enabled, ftpUser.enabled) &&
                Objects.equals(username, ftpUser.username) &&
                Objects.equals(workspace, ftpUser.workspace) &&
                Objects.equals(password, ftpUser.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, workspace, admin, password, maxIdleTime, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FtpUser{");
        sb.append("username='").append(username).append('\'');
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
        return FtpUserManager.getHomeDirectory(this.username)
                .getAbsolutePath();
    }

    @Override
    public String getId() {
        return this.username;
    }

    @Override
    public boolean isNew() {
        return !persisted;
    }

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    @Override
    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }
}

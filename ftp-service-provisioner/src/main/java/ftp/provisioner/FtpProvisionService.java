package ftp.provisioner;

import ftp.service.FtpUser;
import ftp.service.FtpUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.User;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Transactional
public class FtpProvisionService {

    private final FtpUserManager ftpUserManager;

    private Log log = LogFactory.getLog(getClass());

    public FtpProvisionService(FtpUserManager ftpUserManager) {
        this.ftpUserManager = ftpUserManager;
    }

    public FtpProvision provisionFtpAccount(
            String ws, String usr, String password) throws Exception {


    /*
    * keep a list of all the assigned users and the node's they're assigned to.
    * the node will be derived by taking the max count of users per node (N)
    * and doing total number of users - T - and doing T % N. Thus, if we ensure that
    * there may be only 10 users per a single server, then the 12th user will get assigned to ftp2.lattice.
    *
    * this approach won't work when existing allotments have been destroyed. so before we create a new allotment
    * (and, as a side-effect, a new FTP service VM), we'll scan the table of 'dead' allotments and reuse those, first.
    *
    * TODO: the Apache Mina FTP service provides the ability to enforce quotas for uploads/downloads.
    * TODO This will be invaluable in sizing capacity.
    *
    */
        FtpUser user = new FtpUser(ws, usr, password, true);
        ftpUserManager.save(user);
//        String ftpUri = buildFtpConnectionString(host, port, user);

        //    log.info("registering: workspace: " + ws + ", " + "user: " + usr + ", ftp URI: " + ftpUri);

        return null;
    }

    private static String allocate(long id, int maxPerNode) {
        return null;
    }

    public static void main(String args[]) {

        System.out.println(allocate(32, 10));
    }

    public String buildFtpConnectionString(String host, int port, User user) {
        return String.format("ftp://%s:%s@%s:%s", user.getName(),
                user.getPassword(),
                host,
                port);
    }
}

class FtpProvision {

    private final User ftpUser;
    private String host;
    private URI uri;

    public User getFtpUser() {
        return ftpUser;
    }

    public URI getUri() {
        return uri;
    }

    public FtpProvision(User ftpUser, String host) {
        this.ftpUser = ftpUser;
        this.host = host;
    }

}
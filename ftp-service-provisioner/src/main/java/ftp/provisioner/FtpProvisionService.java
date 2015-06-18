package ftp.provisioner;

import ftp.service.FtpUser;
import ftp.service.FtpUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
public class FtpProvisionService {

    private final FtpUserManager ftpUserManager;
    private final JdbcTemplate jdbcTemplate;
    private int maxPerNode = 10;
    private Log log = LogFactory.getLog(getClass());

    public FtpProvisionService(JdbcTemplate jdbcTemplate,
                               FtpUserManager ftpUserManager, int maxPerNode) {

        this.maxPerNode = maxPerNode;
        this.ftpUserManager = ftpUserManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static String allocate(long id, int maxPerNode) {
        return null;
    }

    public static void main(String args[]) {

        System.out.println(allocate(32, 10));
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


        // lets get all the ftp server nodes
        List<FtpServerNode> ftpServerNodeList = jdbcTemplate.query("select * from FTP_SERVER",
                (resultSet, i) -> new FtpServerNode(resultSet.getLong("ID"), resultSet.getInt("PORT"), resultSet.getString("IP_ADDRESS")));

        Map<Long, FtpServerNode> idsToNode = new ConcurrentHashMap<>();
        Map<FtpServerNode, Integer> allocationMap = new ConcurrentHashMap<>();

        ftpServerNodeList.forEach(ftpServerNode -> idsToNode.putIfAbsent(ftpServerNode.getId(), ftpServerNode));

        jdbcTemplate.query("select svr_id, c from (select ftp_server_id as svr_id," +
                        " count(fu.ftp_server_id) as c from ftp_user fu group by " +
                        "fu.ftp_server_id ) where c <  ?",
                (ResultSet resultSet) -> allocationMap.put(idsToNode.get(resultSet.getLong("svr_id")), resultSet.getInt("c")), this.maxPerNode);


//        String ftpUri = buildFtpConnectionString(host, port, user);

        //    log.info("registering: workspace: " + ws + ", " + "user: " + usr + ", ftp URI: " + ftpUri);

        return null;
    }

    public String buildFtpConnectionString(String host, int port, User user) {
        return String.format("ftp://%s:%s@%s:%s", user.getName(),
                user.getPassword(),
                host,
                port);
    }

    private static class FtpServerNode {
        private final long id;
        private final int port;
        private final String ipAddress;

        public FtpServerNode(long id, int port, String ipAddress) {
            this.id = id;
            this.port = port;
            this.ipAddress = ipAddress;
        }

        @Override
        public boolean equals(Object o) {
            FtpServerNode that = FtpServerNode.class.cast(o);
            return Objects.equals(id, that.id) &&
                    Objects.equals(port, that.port) &&
                    Objects.equals(ipAddress, that.ipAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, port, ipAddress);
        }

        public long getId() {
            return id;
        }

        public int getPort() {
            return port;
        }

        public String getIpAddress() {
            return ipAddress;
        }
    }
}

class FtpProvision {

    private final User ftpUser;
    private String host;
    private URI uri;

    public FtpProvision(User ftpUser, String host) {
        this.ftpUser = ftpUser;
        this.host = host;
    }

    public User getFtpUser() {
        return ftpUser;
    }

    public URI getUri() {
        return uri;
    }

}
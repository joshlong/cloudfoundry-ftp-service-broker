package ftp.service.provisions;

import ftp.service.nodes.FtpServerNode;
import ftp.service.nodes.FtpServerNodeRepository;
import ftp.service.users.FtpUser;
import ftp.service.users.FtpUserRepository;
import org.apache.ftpserver.ftplet.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;


// TODO tease this logic apart into 2 sets of calls:
// TODO  _create_ a new user w/ REST client
// TODO  _fetch_ available nodes (and stand one up using Lattice if not available)
// TODO _assign_ instance to the
@Service
@Transactional
public class FtpProvisionService {

    private final FtpServerNodeRepository ftpServerNodeRepository;
    private final FtpUserRepository ftpUserRepository;
    private final int maxPerNode;

    @Autowired
    public FtpProvisionService(FtpServerNodeRepository ftpServerNodeRepository,
                               FtpUserRepository ftpUserRepository,
                               @Value("${ftp.max-per-node:10}") int maxPerNode) {
        this.ftpServerNodeRepository = ftpServerNodeRepository;
        this.ftpUserRepository = ftpUserRepository;
        this.maxPerNode = maxPerNode;
    }

    public FtpProvision provisionFtpAccount(
            String ws, String usr, String password) throws Exception {

        Comparator<Map.Entry<FtpServerNode, Integer>> entryComparator =
                (entry1, entry2) -> Integer.compare(entry1.getValue(), entry2.getValue());

        Map<FtpServerNode, Integer> allocationMap = this.ftpServerNodeRepository.findFtpServerNodesWithCapacity(this.maxPerNode);

        return allocationMap
                .entrySet()
                .stream()
                .sorted(entryComparator)
                .findFirst()
                .map(Map.Entry::getKey)
                .map(node -> {
                    FtpUser ftpUser = ftpUserRepository.save(new FtpUser(ws, usr, password, true, 0, true, node));
                    String uri = buildFtpConnectionString(node.getIpAddress(), node.getPort(), ftpUser);
                    return new FtpProvision(ftpUser, node, URI.create(uri));
                })
                .orElse(null);
    }

    private String buildFtpConnectionString(String host, int port, User user) {
        return String.format("ftp://%s:%s@%s:%s", user.getName(),
                user.getPassword(),
                host,
                port);
    }

}

package ftp.service.nodes;

import com.nurkiewicz.jdbcrepository.JdbcRepository;
import com.nurkiewicz.jdbcrepository.RowUnmapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FtpServerNodeRepository extends JdbcRepository<FtpServerNode, Long> {

    private static final String FTP_USER = "FTP_SERVER";
    private static final String FTP_USER_ID = "ID";

    private Log log = LogFactory.getLog(getClass());

    private static final RowMapper<FtpServerNode> ROW_MAPPER = (rs, i) ->
            new FtpServerNode(rs.getLong("ID"), rs.getInt("PORT"), rs.getString("IP_ADDRESS"));

    private static final RowUnmapper<FtpServerNode> ROW_UNMAPPER =
            fu -> {
                Map<String, Object> maps = new HashMap<>();
                maps.put("ID", fu.getId());
                maps.put("IP_ADDRESS", fu.getIpAddress());
                maps.put("PORT", fu.getPort());
                return maps;
            };

    public Map<FtpServerNode, Integer> findFtpServerNodesWithCapacity(int maxCapacity) {

        JdbcOperations jdbcTemplate = getJdbcOperations();
        List<FtpServerNode> ftpServerNodeList = jdbcTemplate.query("select * from FTP_SERVER",
                (resultSet, i) -> new FtpServerNode(resultSet.getLong("ID"), resultSet.getInt("PORT"), resultSet.getString("IP_ADDRESS")));

        Map<FtpServerNode, Integer> allocationMap = new ConcurrentHashMap<>();
        Map<Long, FtpServerNode> idsToNode = new ConcurrentHashMap<>();
        ftpServerNodeList.forEach(n -> idsToNode.put(n.getId(), n));
        Assert.isTrue(idsToNode.size() > 0, "there should be at least one FtpServerNode to assign!");
        RowCallbackHandler rowCallbackHandler = resultSet -> {
            long svrId = resultSet.getLong("svr_id");
            FtpServerNode key = idsToNode.get(svrId);
            int count = resultSet.getInt("c");
            allocationMap.put(key, count);
            log.info(key + " x " + count);
        };
        jdbcTemplate.query(
                "select * from (select fs.id as svr_id, (select count(fu.username) from ftp_user as fu where fu.ftp_server_id = fs.id) as c from ftp_server as fs) where c < ?",
                rowCallbackHandler, maxCapacity);

        return allocationMap;
    }

    public FtpServerNodeRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, FTP_USER, FTP_USER_ID);
    }

    @Override
    protected <S extends FtpServerNode> S postCreate(S entity, Number generatedId) {
        entity.setId(generatedId.longValue());
        return entity;
    }

}

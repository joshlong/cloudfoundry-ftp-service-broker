package ftp.service.users;

import com.nurkiewicz.jdbcrepository.JdbcRepository;
import ftp.service.nodes.FtpServerNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FtpUserRepository extends JdbcRepository<FtpUser, String> {

    private static final String FTP_USER = "FTP_USER";
    private static final String FTP_USER_ID = "USERNAME";

    private FtpServerNodeRepository repository;

    private static class FtpUserRowMapper implements RowMapper<FtpUser> {

        private final FtpServerNodeRepository ftpServerNodeRepository;

        @Override
        public FtpUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FtpUser(
                    rs.getString("WORKSPACE"),
                    rs.getString("USERNAME"),
                    rs.getString("PASSWORD"),
                    rs.getBoolean("ADMIN"),
                    rs.getInt("MAX_IDLE_TIME"),
                    rs.getBoolean("ENABLED"),
                    ftpServerNodeRepository.findOne(rs.getLong("FTP_SERVER_ID")));
        }
        public FtpUserRowMapper(FtpServerNodeRepository repository) {
            this.ftpServerNodeRepository = repository;
        }

    }

    @Autowired
    public FtpUserRepository(FtpServerNodeRepository repository, JdbcTemplate jdbcTemplate) {
        super(new FtpUserRowMapper(repository),
                (FtpUser fu) -> {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("WORKSPACE", fu.getWorkspace());
                    maps.put("USERNAME", fu.getName());
                    maps.put("PASSWORD", fu.getPassword());
                    maps.put("ADMIN", fu.isAdmin());
                    maps.put("MAX_IDLE_TIME", fu.getMaxIdleTime());
                    maps.put("ENABLED", fu.isEnabled());
                    maps.put("FTP_SERVER_ID", fu.getFtpServerNode().getId());
                    return maps;
                }, FTP_USER, FTP_USER_ID);

        this.setJdbcOperations(jdbcTemplate);
        this.repository = repository;
    }

    @Override
    protected <S extends FtpUser> S postCreate(S entity, Number generatedId) {
        entity.setPersisted(true);
        return entity;
    }

    @Override
    protected <S extends FtpUser> S postUpdate(S entity) {
        entity.setPersisted(true);
        return entity;
    }

    public Optional<FtpUser> findByUsername(String u) {
        List<FtpUser> users = this.getJdbcOperations().query(
                "select * from FTP_USER where USERNAME = ?",
                new FtpUserRowMapper(this.repository), u);
        return Optional.ofNullable(users.size() == 0 ? null : users.iterator().next());
    }
}


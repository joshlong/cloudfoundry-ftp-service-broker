package ftp.service;

import com.nurkiewicz.jdbcrepository.JdbcRepository;
import com.nurkiewicz.jdbcrepository.RowUnmapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO we're moving everything to JDBC so that in the ProvisionService we can use multiple resources in the same TX
// TODO does the fact that we're trying to use multiple resources in the same TX indicate a code-smell?
// TODO let's get t his working then see about teasing out to better microservice-style things

public class FtpUserRepository extends JdbcRepository<FtpUser, String> {

    private static final String FTP_USER = "FTP_USER";


    private static final String FTP_USER_ID = "USERNAME";

    private static final RowMapper<FtpUser> ROW_MAPPER = (rs, i) ->
            new FtpUser(
                    rs.getString("WORKSPACE"),
                    rs.getString("USERNAME"),
                    rs.getString("PASSWORD"),
                    rs.getBoolean("ADMIN"),
                    rs.getInt("MAX_IDLE_TIME"),
                    rs.getBoolean("ENABLED"));

    private static final RowUnmapper<FtpUser> ROW_UNMAPPER =
            fu -> {
                Map<String, Object> maps = new HashMap<>();
                maps.put("WORKSPACE", fu.getWorkspace());
                maps.put("USERNAME", fu.getName());
                maps.put("PASSWORD", fu.getPassword());
                maps.put("ADMIN", fu.isAdmin());
                maps.put("MAX_IDLE_TIME", fu.getMaxIdleTime());
                maps.put("ENABLED", fu.isEnabled());
                return maps;
            };

    public FtpUserRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, FTP_USER, FTP_USER_ID);
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
                "select * from FTP_USER where USERNAME = ?", ROW_MAPPER, u);
        return Optional.ofNullable(users.size() == 0 ? null : users.iterator().next());
    }
}


package ftp.broker;

import com.nurkiewicz.jdbcrepository.JdbcRepository;
import com.nurkiewicz.jdbcrepository.RowUnmapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PersistableServiceInstanceBindingRepository
        extends JdbcRepository<PersistableServiceInstanceBinding, String> {

    private static final String SERVICE_INSTANCE_BINDING_ID_NAME = "SERVICE_INSTANCE_BINDING_ID";

    private static final String SERVICE_INSTANCE_TABLE_NAME = "SERVICE_INSTANCE_BINDING";

    private static final RowUnmapper<PersistableServiceInstanceBinding> ROW_UNMAPPER =
            sib -> {
                Map<String, Object> map = new HashMap<>();
                map.put(SERVICE_INSTANCE_BINDING_ID_NAME, sib.getId());
                map.put("SERVICE_INSTANCE_ID", sib.getServiceInstanceId());
                map.put("URI", String.class.cast(sib.getCredentials().getOrDefault("uri", null)));
                map.put("SYSLOG_DRAIN_URL", sib.getSyslogDrainUrl());
                map.put("APP_GUID", sib.getAppGuid());
                return map;
            };

    private static final RowMapper<PersistableServiceInstanceBinding> ROW_MAPPER =
            (rs, i) -> new PersistableServiceInstanceBinding(
                    rs.getString(SERVICE_INSTANCE_BINDING_ID_NAME),
                    rs.getString("SERVICE_INSTANCE_ID"),
                    rs.getString("URI"),
                    rs.getString("SYSLOG_DRAIN_URL"),
                    rs.getString("APP_GUID"));

    public PersistableServiceInstanceBindingRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SERVICE_INSTANCE_TABLE_NAME, SERVICE_INSTANCE_BINDING_ID_NAME);
    }

    @Override
    protected <S extends PersistableServiceInstanceBinding> S postCreate(S entity, Number generatedId) {
        entity.setPersisted(true);
        return entity;
    }

    @Override
    protected <S extends PersistableServiceInstanceBinding> S postUpdate(S entity) {
        entity.setPersisted(true);
        return entity;
    }
}

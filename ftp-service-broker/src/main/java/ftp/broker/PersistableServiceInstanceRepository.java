package ftp.broker;

import com.google.common.collect.ImmutableMap;
import com.nurkiewicz.jdbcrepository.JdbcRepository;
import com.nurkiewicz.jdbcrepository.RowUnmapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PersistableServiceInstanceRepository extends JdbcRepository<PersistableServiceInstance, String> {

    private static final String SERVICE_INSTANCE_TABLE_NAME = "SERVICE_INSTANCE";

    private static final String SERVICE_INSTANCE_ID_NAME = "SERVICE_INSTANCE_ID";

    private static final RowMapper<PersistableServiceInstance> ROW_MAPPER =
            (rs, i) -> new PersistableServiceInstance(
                    rs.getString("SERVICE_DEFINITION_ID"),
                    rs.getString("PLAN_ID"),
                    rs.getString("ORGANIZATION_NAME"),
                    rs.getString("SPACE_GUID"),
                    rs.getString(SERVICE_INSTANCE_ID_NAME), true);

    private static final RowUnmapper<PersistableServiceInstance> ROW_UNMAPPER =
            si -> ImmutableMap.of(
                    SERVICE_INSTANCE_ID_NAME, si.getServiceInstanceId(),
                    "PLAN_ID", si.getPlanId(),
                    "SERVICE_DEFINITION_ID", si.getServiceDefinitionId(),
                    "ORGANIZATION_NAME", si.getOrganizationGuid(),
                    "SPACE_GUID", si.getSpaceGuid()
            );

    public PersistableServiceInstanceRepository() {
        super(ROW_MAPPER, ROW_UNMAPPER, SERVICE_INSTANCE_TABLE_NAME, SERVICE_INSTANCE_ID_NAME);
    }

    @Override
    protected <S extends PersistableServiceInstance> S postCreate(S entity, Number generatedId) {
        entity.setPersisted(true);
        return entity;
    }

    @Override
    protected <S extends PersistableServiceInstance> S postUpdate(S entity) {
        entity.setPersisted(true);
        return entity;
    }
}


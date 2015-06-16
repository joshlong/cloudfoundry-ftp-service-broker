package ftp.broker;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.data.domain.Modifiable;

public class PersistableServiceInstance extends ServiceInstance
        implements Modifiable<String> {

    private transient boolean persisted;

    public PersistableServiceInstance(String serviceDefinitionId, String planId,
                                      String organizationGuid, String spaceGuid, String serviceInstanceId) {
        super(new CreateServiceInstanceRequest(serviceDefinitionId, planId, organizationGuid, spaceGuid)
                .withServiceInstanceId(serviceInstanceId));

    }

    public PersistableServiceInstance(String serviceDefinitionId, String planId,
                                      String organizationGuid, String spaceGuid, String serviceInstanceId, boolean persisted) {
        super(new CreateServiceInstanceRequest(serviceDefinitionId, planId, organizationGuid, spaceGuid)
                .withServiceInstanceId(serviceInstanceId));
        this.persisted = persisted;
    }

    public PersistableServiceInstance() {
        super(new UpdateServiceInstanceRequest("noOpPlanId"));
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String getId() {
        return this.getServiceInstanceId();
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

package ftp.broker;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.springframework.data.domain.Modifiable;

import java.util.Collections;


public class PersistableServiceInstanceBinding extends ServiceInstanceBinding
        implements Modifiable<String> {

    private transient boolean persisted;

    public PersistableServiceInstanceBinding(String id, String serviceInstanceId,
                                             String credentials, String syslogDrainUrl, String appGuid) {
        super(id, serviceInstanceId, Collections.singletonMap("uri", credentials), syslogDrainUrl, appGuid);
    }

    public PersistableServiceInstanceBinding(String id, String serviceInstanceId,
                                             String credentials, String syslogDrainUrl, String appGuid, boolean persisted) {
        super(id, serviceInstanceId, Collections.singletonMap("uri", credentials), syslogDrainUrl, appGuid);
        this.persisted = persisted;
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

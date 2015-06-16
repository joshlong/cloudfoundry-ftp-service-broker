package ftp.broker;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.DeleteServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.UpdateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class FtpServiceInstanceService implements ServiceInstanceService {

    private final PersistableServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    public FtpServiceInstanceService(PersistableServiceInstanceRepository repository) {
        this.serviceInstanceRepository = repository;
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request)
            throws ServiceBrokerException {
        PersistableServiceInstance instance = this.serviceInstanceRepository
                .findOne(request.getServiceInstanceId());
        if (null != instance)
            this.serviceInstanceRepository.delete(instance);
        return instance;
    }

    @Override
    public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request)
            throws ServiceInstanceExistsException, ServiceBrokerException {

        PersistableServiceInstance psi = new PersistableServiceInstance(
                request.getServiceDefinitionId(),
                request.getPlanId(), request.getOrganizationGuid(),
                request.getSpaceGuid(), request.getServiceInstanceId());

        return this.serviceInstanceRepository.save(psi);

    }

    @Override
    public ServiceInstance getServiceInstance(String s) {
        return this.serviceInstanceRepository.findOne(s);
    }

    @Override
    public ServiceInstance updateServiceInstance(UpdateServiceInstanceRequest request)
            throws ServiceInstanceUpdateNotSupportedException,
            ServiceBrokerException,
            ServiceInstanceDoesNotExistException {

        PersistableServiceInstance one = this.serviceInstanceRepository.findOne(
                request.getServiceInstanceId());

        PersistableServiceInstance two = new PersistableServiceInstance(
                one.getServiceDefinitionId(),
                request.getPlanId(),
                one.getOrganizationGuid(),
                one.getSpaceGuid(),
                request.getServiceInstanceId(), true);

        return this.serviceInstanceRepository.save(two);
    }
}

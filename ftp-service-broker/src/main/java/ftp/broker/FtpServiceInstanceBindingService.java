package ftp.broker;

import ftp.provisioner.FtpServerProvisionerClient;
import ftp.provisioner.FtpServerProvisionerRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
class FtpServiceInstanceBindingService implements ServiceInstanceBindingService {

    private final PersistableServiceInstanceBindingRepository bindingRepository;
    private Log log = LogFactory.getLog(getClass());
    private FtpServerProvisionerClient client;

    @Autowired
    public FtpServiceInstanceBindingService(
            FtpServerProvisionerClient client,
            PersistableServiceInstanceBindingRepository repository) {
        this.bindingRepository = repository;
        this.client = client;
    }

    @Override
    public ServiceInstanceBinding createServiceInstanceBinding(
            CreateServiceInstanceBindingRequest request)
            throws ServiceInstanceBindingExistsException,
            ServiceBrokerException {

        String bindingId = request.getBindingId(); // user
        String instanceId = request.getServiceInstanceId(); // workspace

        log.info(String.format("attempting to provision an FTP for workspace " +
                "(serviceInstanceId) %s and user (bindingId) %s", instanceId, bindingId));

        FtpServerProvisionerRequest r = new FtpServerProvisionerRequest(bindingId, instanceId);

        String response = this.client.provisionFtpServer(r);

        log.info(String.format("received FTP service provision response %s", response));

        PersistableServiceInstanceBinding psb = new PersistableServiceInstanceBinding(
                request.getBindingId(),
                request.getServiceInstanceId(),
                response,
                null, request.getAppGuid());

        return this.bindingRepository.save(psb);
    }

    @Override
    public ServiceInstanceBinding deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request)
            throws ServiceBrokerException {

        return Optional.ofNullable(this.bindingRepository.findOne(request.getBindingId()))
                .map(psbi -> {
                    this.bindingRepository.delete(psbi);
                    return psbi;
                })
                .orElse(null);

    }
}

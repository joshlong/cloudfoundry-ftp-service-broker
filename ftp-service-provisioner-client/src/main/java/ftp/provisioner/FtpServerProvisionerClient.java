package ftp.provisioner;


import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface FtpServerProvisionerClient {

    @Gateway(requestChannel = FtpServerProvisionerClientAutoConfiguration.PROVISION_REQUESTS_CHANNEL_NAME)
    String provisionFtpServer(FtpServerProvisionerRequest request);
}

package ftp.provisioner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftp.service.FtpUserManagerConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@EnableTransactionManagement
@Import(FtpUserManagerConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final ObjectMapper mapper = new ObjectMapper();

    private Log log = LogFactory.getLog(getClass());

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.amqp.requests}")
    private String ftpRequests;

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    IntegrationFlow amqpReplyFlow(ConnectionFactory rabbitConnectionFactory,
                                  FtpProvisionService ftpProvisionService) {
        return IntegrationFlows.from(Amqp.inboundGateway(rabbitConnectionFactory, this.ftpRequests)
                .messageConverter(new Jackson2JsonMessageConverter()))
                .transform(String.class, new GenericTransformer<String, String>() {
                    @Override
                    public String transform(String source) {
                        try {
                            Map<String, String> map = toMap(source);
                            String ws = map.get("workspace");
                            String usr = map.get("user");
                            String password = UUID.randomUUID().toString();


                            //FtpProvisionService.FtpProvision ftpProvision = ftpProvisionService.createFtpUser(ws, usr, password) ;

                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).get();
    }


    private Map<String, String> toMap(String src) {
        try {
            TypeReference<HashMap<String, String>> valueTypeRef =
                    new TypeReference<HashMap<String, String>>() { };
            return mapper.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

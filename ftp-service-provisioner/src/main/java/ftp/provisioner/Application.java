package ftp.provisioner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftp.service.*;
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
import org.springframework.integration.dsl.EnricherSpec;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
//@EnableTransactionManagement
//@Import(FtpUserManagerConfiguration.class)
public class Application {

    private final ObjectMapper mapper = new ObjectMapper();


    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.amqp.requests}")
    private String ftpRequests;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    public static class ProvisionRequest {
        private String workspace, user, password;

        public ProvisionRequest(String workspace, String user,
                                String password) {
            this.workspace = workspace;
            this.user = user;
            this.password = password;
        }

        public String getWorkspace() {
            return workspace;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    @Bean
    IntegrationFlow amqpReplyFlow(ConnectionFactory rabbitConnectionFactory) {
        return IntegrationFlows.from(Amqp.inboundGateway(rabbitConnectionFactory, this.ftpRequests)
                .messageConverter(new Jackson2JsonMessageConverter()))
                .transform(String.class, new GenericTransformer<String, ProvisionRequest>() {
                    @Override
                    public ProvisionRequest transform(String source) {
                        Map<String, String> map = toMap(source);
                        String ws = map.get("workspace");
                        String usr = map.get("user");
                        String password = UUID.randomUUID().toString();
                        return new ProvisionRequest(ws, usr, password);
                    }
                })
                .enrichHeaders(new Consumer<HeaderEnricherSpec>() {
                    @Override
                    public void accept(HeaderEnricherSpec headerEnricherSpec) {

                        // from here we'll call the FTP REST API and do an allocation
                    }
                })

                .get();
    }

    private Map<String, String> toMap(String src) {
        try {
            TypeReference<HashMap<String, String>> valueTypeRef = new TypeReference<HashMap<String, String>>() {
            };
            return mapper.readValue(src, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

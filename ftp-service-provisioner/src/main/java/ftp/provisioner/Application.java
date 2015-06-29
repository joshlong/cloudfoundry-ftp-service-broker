package ftp.provisioner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ftp.api.FtpUser;
import ftp.api.FtpUserManagerConfiguration;
import ftp.api.FtpUserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.transformer.GenericTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The provisioner receives requests and translates them into API invocations against the FTP API.
 *
 * The FTP API is also used by instances of the FTP server.
 *
 */
@SpringBootApplication
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
    CommandLineRunner provisionerCLR(FtpUserRepository repository) {
        return args -> {

            log.info("FTP provisioner started at " + System.currentTimeMillis());

            repository.findAll().forEach(u -> this.log.info(
                    buildFtpConnectionString(host, port, u)));
        };
    }

    @Bean
    IntegrationFlow amqpReplyFlow(ConnectionFactory rabbitConnectionFactory,
                                  UserManager ftpUserManager) {
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
                            FtpUser user = new FtpUser(ws, usr, password, true);
                            ftpUserManager.save(user);
                            String ftpUri = buildFtpConnectionString(host, port, user);
                            log.info("registering: workspace: " + ws + ", " + "user: " + usr + ", ftp URI: " + ftpUri);
                            return ftpUri;
                        } catch (FtpException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).get();
    }

    private String buildFtpConnectionString(String host, int port, User user) {
        return String.format("ftp://%s:%s@%s:%s", user.getName(),
                user.getPassword(),
                host,
                port);
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

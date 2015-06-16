package ftp.installer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

@SpringBootApplication
public class Application {

    private Log log = LogFactory.getLog(getClass());

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner runner(AnnotationConfigApplicationContext configApplicationContext, JdbcTemplate template) {
        return args -> {
            template.query("select USERNAME from FTP_USER",
                    (RowCallbackHandler) resultSet -> log.info(String.format("ftp_user row: %s", resultSet.getString("USERNAME"))));
            configApplicationContext.destroy();
        };
    }

    @Bean
    InitializingBean prepareQueues(@Value("${ftp.amqp.requests}") String ftpRequests,
                                   AmqpAdmin amqpAdmin) {
        return () -> {
            Queue queue = new Queue(ftpRequests, true);
            DirectExchange exchange = new DirectExchange(ftpRequests);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(
                    ftpRequests);
            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareExchange(exchange);
            amqpAdmin.declareBinding(binding);
        };
    }

}

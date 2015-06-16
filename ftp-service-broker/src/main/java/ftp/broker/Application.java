package ftp.broker;

import org.cloudfoundry.community.servicebroker.model.BrokerApiVersion;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    Catalog catalog() {
        List<Plan> plans = Collections.singletonList(new Plan("ftp-free", "ftp-free", "'Everybody gets an FTP!'",
                Collections.singletonMap("plan-metadata", "plan-metadata-description"), true));
        List<ServiceDefinition> serviceDefinitions = Collections.singletonList(
                new ServiceDefinition("ftp", "ftp", "Apache Mina FTP", true, true, plans,
                        Collections.singletonList("ftp"),
                        Collections.emptyMap(),
                        Collections.emptyList(),
                        null));
        return new Catalog(serviceDefinitions);
    }

    @Bean
    BrokerApiVersion brokerApiVersion() {
        return new BrokerApiVersion();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}


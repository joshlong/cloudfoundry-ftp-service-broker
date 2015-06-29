package ftp.provisioner;

import ftp.provisioner.FtpServerProvisionerClient;
import ftp.provisioner.FtpServerProvisionerClientAutoConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FtpServerProvisionerClientAutoConfigurationTest {

    private static AtomicReference<RabbitTemplate> RT_AR = new AtomicReference<>();

    private ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {

        RT_AR.set(buildRabbitTemplate());

        this.applicationContext = SpringApplication.run(
                FtpServerProvisionerClientAutoConfigurationConfig.class);
    }

    private RabbitTemplate buildRabbitTemplate() {
        return mock(RabbitTemplate.class);
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(this.applicationContext.getBean("provision-ftp", MessageChannel.class));
        assertNotNull(this.applicationContext.getBean(FtpServerProvisionerClientAutoConfiguration.class));
        assertNotNull(this.applicationContext.getBean(FtpServerProvisionerClient.class));
    }

    @Configuration
    @EnableAutoConfiguration
    public static class FtpServerProvisionerClientAutoConfigurationConfig {

        @Bean
        ConnectionFactory connectionFactory() {
            ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
            when(connectionFactory.createConnection())
                    .thenReturn(mock(Connection.class));
            return connectionFactory;
        }

        @Bean
        AmqpAdmin amqpAdmin() {
            return mock(AmqpAdmin.class);
        }

        @Bean
        RabbitTemplate rabbitTemplate() {
            return RT_AR.get();
        }
    }
}
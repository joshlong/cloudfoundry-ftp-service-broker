package ftp.provisioner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;

@Configuration
@IntegrationComponentScan
public class FtpServerProvisionerClientAutoConfiguration {

    public static final String PROVISION_REQUESTS_CHANNEL_NAME = "provision-ftp";

    @Value("${ftp.amqp.requests}")
    private String ftpRequests;

    @Bean
    InitializingBean ampqInitializer(AmqpAdmin admin) {
        return () -> this.prepareQueues(admin);
    }

    @Bean(name = PROVISION_REQUESTS_CHANNEL_NAME)
    @ConditionalOnMissingBean(value = MessageChannel.class, name = PROVISION_REQUESTS_CHANNEL_NAME)
    MessageChannel provisionFtpRequestsChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    IntegrationFlow provisionFtpRequestsFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows.from(PROVISION_REQUESTS_CHANNEL_NAME)
                .transform(FtpServerProvisionerRequest.class,
                        new GenericTransformer<FtpServerProvisionerRequest, String>() {
                            @Override
                            public String transform(FtpServerProvisionerRequest source) {
                                try {
                                    ObjectMapper om = new ObjectMapper();
                                    return om.writer().writeValueAsString(source);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                .handle(Amqp.outboundGateway(amqpTemplate).routingKey(this.ftpRequests))
                .get();
    }

    protected void prepareQueues(AmqpAdmin amqpAdmin) {
        Queue queue = new Queue(this.ftpRequests, true);
        DirectExchange exchange = new DirectExchange(this.ftpRequests);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(this.ftpRequests);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareBinding(binding);
    }
}
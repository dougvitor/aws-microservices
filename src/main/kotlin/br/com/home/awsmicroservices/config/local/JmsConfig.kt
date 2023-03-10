package br.com.home.awsmicroservices.config.local

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.support.destination.DynamicDestinationResolver
import javax.jms.Session

@Configuration
@EnableJms
@Profile("local")
class JmsConfig{
    private lateinit var sqsConnectionFactory: SQSConnectionFactory

    @Bean
    fun jmsListenerContainerFactory(): DefaultJmsListenerContainerFactory {
        sqsConnectionFactory = SQSConnectionFactory(
            ProviderConfiguration(),
            AmazonSQSClient.builder()
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:4566",
                        Regions.US_EAST_1.getName()
                    )
                ).withCredentials(DefaultAWSCredentialsProviderChain()).build()
        )

        return DefaultJmsListenerContainerFactory().apply {
            this.setConnectionFactory(sqsConnectionFactory)
            this.setDestinationResolver(DynamicDestinationResolver())
            this.setConcurrency("2")
            this.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
        }
    }

}



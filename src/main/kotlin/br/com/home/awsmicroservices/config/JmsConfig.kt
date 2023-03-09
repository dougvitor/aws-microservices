package br.com.home.awsmicroservices.config

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.support.destination.DynamicDestinationResolver
import javax.jms.Session

@Configuration
@EnableJms
@Profile("!local")
class JmsConfig(
    @Value("\${aws.region}")
    private val awsRegion: String
) {
    private lateinit var sqsConnectionFactory: SQSConnectionFactory

    @Bean
    fun jmsListenerContainerFactory(): DefaultJmsListenerContainerFactory {
        sqsConnectionFactory = SQSConnectionFactory(
            ProviderConfiguration(),
            AmazonSQSClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(DefaultAWSCredentialsProviderChain())
                .build()
        )

        return  DefaultJmsListenerContainerFactory().apply {
            this.setConnectionFactory(sqsConnectionFactory)
            this.setDestinationResolver(DynamicDestinationResolver())
            this.setConcurrency("2")
            this.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
        }
    }

}



package br.com.home.awsmicroservices.config.local

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.model.Topic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class SnsCreate {

    private val logger: Logger = LoggerFactory.getLogger(SnsCreate::class.java)

    private var productEventsTopic: String? = null

    private var snsClient: AmazonSNS = AmazonSNSClient.builder()
        .withEndpointConfiguration(
            AwsClientBuilder.EndpointConfiguration(
                "http://localhost:4566",
                Regions.US_EAST_1.getName()
            )
        )
        .withCredentials(DefaultAWSCredentialsProviderChain())
        .build()

    init {
        val createTopicRequest = CreateTopicRequest("product-events")
        productEventsTopic = snsClient.createTopic(createTopicRequest).topicArn

        logger.info("SNS topic ARN: {}", productEventsTopic)
    }

    @Bean
    fun snsClient(): AmazonSNS = snsClient

    @Bean(name = ["productEventsTopic"])
    fun snsProductEventsTopic(): Topic = Topic().withTopicArn(productEventsTopic)

}
package br.com.home.awsmicroservices.config.local

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.Topic
import com.amazonaws.services.sns.util.Topics
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.CreateQueueRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class SqsCreateSubscribe(
    private val snsClient: AmazonSNS,
    @Qualifier("productEventsTopic") private val productEventsTopic: Topic
) {

    private var sqsClient = AmazonSQSClient.builder()
        .withEndpointConfiguration(
            AwsClientBuilder.EndpointConfiguration(
                "http://localhost:4566",
                Regions.US_EAST_1.getName()
            )
        ).withCredentials(DefaultAWSCredentialsProviderChain())
        .build()

    init {
        val productEventsQueryUrl = sqsClient.createQueue(
            CreateQueueRequest("product-events")
        ).queueUrl

        Topics.subscribeQueue(snsClient, sqsClient, productEventsTopic.topicArn, productEventsQueryUrl)
    }

}
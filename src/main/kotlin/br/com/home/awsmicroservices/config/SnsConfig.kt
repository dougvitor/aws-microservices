package br.com.home.awsmicroservices.config

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.Topic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
data class SnsConfig(
    @Value("\${aws.region}")
    private val awsRegion: String,

    @Value("\${aws.sns.topic.product.events.arn}")
    private val productEventsTopic: String
) {
    @Bean
    fun snsClient(): AmazonSNS = AmazonSNSClientBuilder.standard()
        .withRegion(awsRegion)
        .withCredentials(DefaultAWSCredentialsProviderChain())
        .build()


    @Bean(name = ["productEventsTopic"])
    fun snsProductEventsTopic(): Topic = Topic().withTopicArn(productEventsTopic)

}
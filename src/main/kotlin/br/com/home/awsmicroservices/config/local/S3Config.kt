package br.com.home.awsmicroservices.config.local

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.BucketNotificationConfiguration
import com.amazonaws.services.s3.model.S3Event
import com.amazonaws.services.s3.model.TopicConfiguration
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.util.Topics
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.CreateQueueRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class S3Config(
    @Value("\${aws.s3.bucket.invoice.name}")
    private val bucketName: String,

    @Value("\${aws.sqs.queue.invoice.events.name}")
    private val sqsInvoiceEventsName: String
) {

    private lateinit var amazonS3: AmazonS3


    init {
        createAmazonS3()

        createBucket()

        val snsClient = getAmazonSNS()

        val s3InvoiceEventsTopicArn = createTopicAndGetArn(snsClient)

        val sqsClient = getAmazonSQS()

        createQueueAndSubscribeInTopic(snsClient, s3InvoiceEventsTopicArn, sqsClient)

        configureBucket(s3InvoiceEventsTopicArn)
    }

    private fun createAmazonS3() {
        val credentials: AWSCredentials = BasicAWSCredentials("test", "test")

        amazonS3 = AmazonS3Client.builder()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    "http://localhost:4566",
                    Regions.US_EAST_1.getName()
                )
            )
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .enablePathStyleAccess()
            .build()
    }

    private fun createBucket() = amazonS3.createBucket(bucketName)

    private fun getAmazonSNS(): AmazonSNS =
        AmazonSNSClient.builder()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    "http://localhost:4566",
                    Regions.US_EAST_1.getName()
                )
            )
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .build()

    private fun createTopicAndGetArn(snsClient: AmazonSNS): String =
        CreateTopicRequest(sqsInvoiceEventsName).run {
            snsClient.createTopic(this).topicArn
        }

    private fun getAmazonSQS(): AmazonSQS =
        AmazonSQSClient.builder()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    "http://localhost:4566",
                    Regions.US_EAST_1.getName()
                )
            )
            .withCredentials(DefaultAWSCredentialsProviderChain())
            .build()

    private fun createQueueAndSubscribeInTopic(
        snsClient: AmazonSNS,
        s3InvoiceEventsTopicArn: String,
        sqsClient: AmazonSQS
    ) = sqsClient.createQueue(
        CreateQueueRequest(sqsInvoiceEventsName)
    ).queueUrl.run {
        Topics.subscribeQueue(snsClient, sqsClient, s3InvoiceEventsTopicArn, this)
    }

    private fun configureBucket(s3InvoiceEventsTopicArn: String) =
        TopicConfiguration().apply {
            topicARN = s3InvoiceEventsTopicArn
            addEvent(S3Event.ObjectCreatedByPut)
        }.also {
            amazonS3.setBucketNotificationConfiguration(
                bucketName,
                BucketNotificationConfiguration().addConfiguration(
                    "putInvoiceObject",
                    it
                )
            )
        }

    @Bean
    fun amazonS3(): AmazonS3 = this.amazonS3

}


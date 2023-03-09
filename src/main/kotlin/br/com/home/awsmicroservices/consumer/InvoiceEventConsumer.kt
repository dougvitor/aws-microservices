package br.com.home.awsmicroservices.consumer

import br.com.home.awsmicroservices.model.Invoice
import br.com.home.awsmicroservices.model.SnsMessage
import br.com.home.awsmicroservices.repository.InvoiceRepository
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.event.S3EventNotification
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.jms.TextMessage

@Service
class InvoiceEventConsumer(
    private val amazonS3: AmazonS3,
    private val objectMapper: ObjectMapper,
    private val invoiceRepository: InvoiceRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(InvoiceEventConsumer::class.java)

    @JmsListener(destination = "\${aws.sqs.queue.invoice.events.name}")
    fun receiveProductEvent(textMessage: TextMessage) {

        val snsMessage = objectMapper.readValue(textMessage.text, SnsMessage::class.java)

        val s3EventNotification = objectMapper.readValue(snsMessage.message, S3EventNotification::class.java)

        processInvoiceNotification(s3EventNotification)

    }

    private fun processInvoiceNotification(s3EventNotification: S3EventNotification) =
        s3EventNotification.records.forEach { record ->

            val s3Entity = record.s3
            val bucketName = s3Entity.bucket.name
            val objectKey = s3Entity.`object`.key

            val invoiceFile = downloadObject(bucketName, objectKey)

            val invoice = objectMapper.readValue(invoiceFile, Invoice::class.java)

            logger.info("Invoice received: {}", invoice.invoiceNumber)

            invoiceRepository.save(invoice)

            amazonS3.deleteObject(bucketName, objectKey)

        }

    private fun downloadObject(bucketName: String, objectKey: String) =
        amazonS3.getObject(bucketName, objectKey).run {
            val stringBuilder = StringBuilder()
            BufferedReader(InputStreamReader(this.objectContent)).readLines().forEach { content ->
                stringBuilder.append(content)
            }
            stringBuilder.toString()
        }
}
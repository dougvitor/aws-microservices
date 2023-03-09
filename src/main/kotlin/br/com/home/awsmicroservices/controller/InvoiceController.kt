package br.com.home.awsmicroservices.controller

import br.com.home.awsmicroservices.model.UrlResponse
import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(
    @Value("\${aws.s3.bucket.invoice.name}")
    private val bucketName: String,
    private val amazonS3: AmazonS3
) {

    @GetMapping
    fun createInvoiceUrl(): ResponseEntity<UrlResponse> {
        val expirationTime = Instant.now().plus(Duration.ofMinutes(5))
        val processId = UUID.randomUUID().toString()

        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, processId)
            .withMethod(HttpMethod.PUT)
            .withExpiration(Date.from(expirationTime))

        return ResponseEntity.ok(
            UrlResponse(
                amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString(),
                expirationTime.epochSecond
            )
        )
    }
}
package br.com.home.awsmicroservices.controller

import br.com.home.awsmicroservices.model.Invoice
import br.com.home.awsmicroservices.model.UrlResponse
import br.com.home.awsmicroservices.repository.InvoiceRepository
import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api/invoices")
class InvoiceController(
    @Value("\${aws.s3.bucket.invoice.name}")
    private val bucketName: String,
    private val amazonS3: AmazonS3,
    private val invoceRepository: InvoiceRepository
) {

    @PostMapping
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

    @GetMapping
    fun findAll(): Iterable<Invoice> = invoceRepository.findAll()

    @GetMapping("/customerName")
    fun findByCustomerName(@RequestParam customerName: String) = invoceRepository.findAllByCustomerName(customerName)
}
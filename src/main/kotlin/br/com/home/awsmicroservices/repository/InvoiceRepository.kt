package br.com.home.awsmicroservices.repository

import br.com.home.awsmicroservices.model.Invoice
import br.com.home.awsmicroservices.model.Product
import org.springframework.data.repository.CrudRepository
import java.util.*

interface InvoiceRepository : CrudRepository<Invoice, Long> {
    fun findByInvoiceNumber(invoiceNumber: String): Invoice?

    fun findAllByCustomerName(customerName: String): List<Invoice>
}
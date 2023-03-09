package br.com.home.awsmicroservices.model

import java.math.BigDecimal
import javax.persistence.*

@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["invoiceNumber"])])
@Entity
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(length = 32, nullable = false)
    val invoiceNumber: String,

    @Column(length = 32, nullable = false)
    val customerName: String,

    val totalValue: BigDecimal,

    val productId: Long,

    val quantity: Long
)

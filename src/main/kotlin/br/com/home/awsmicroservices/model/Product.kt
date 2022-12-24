package br.com.home.awsmicroservices.model

import java.math.BigDecimal
import javax.persistence.*

@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["code"])])
@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(length = 32, nullable = false)
    val name: String? = "",

    @Column(length = 24, nullable = false)
    val model: String? = "",

    @Column(length = 8, nullable = false)
    val code: String? = "",

    val price: BigDecimal = BigDecimal.ZERO,

    @Column(length = 12)
    val color: String? = null

)
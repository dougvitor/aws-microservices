package br.com.home.awsmicroservices.model

data class ProductEvent(
    val productId: Long,
    val code: String,
    val username: String
)

package br.com.home.awsmicroservices.model

data class ProductEvent(
    private val productId: Long,
    private val code: String,
    private val username: String
)

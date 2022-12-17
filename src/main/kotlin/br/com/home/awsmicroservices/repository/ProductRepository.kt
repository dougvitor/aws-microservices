package br.com.home.awsmicroservices.repository

import br.com.home.awsmicroservices.model.Product
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProductRepository : CrudRepository<Product, Long> {
    fun findByCode(code: String): Optional<Product>
}
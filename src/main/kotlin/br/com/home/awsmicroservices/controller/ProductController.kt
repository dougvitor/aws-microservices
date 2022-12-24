package br.com.home.awsmicroservices.controller

import br.com.home.awsmicroservices.model.Product
import br.com.home.awsmicroservices.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController @Autowired constructor(private val repository: ProductRepository) {

    @GetMapping
    fun findAll(): Iterable<Product> = repository.findAll()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Product> =
        repository.findById(id).map { value: Product ->
            ResponseEntity(
                value,
                HttpStatus.OK
            )
        }.orElseGet { ResponseEntity(HttpStatus.NO_CONTENT) }

    @PostMapping
    fun save(@RequestBody product: Product): ResponseEntity<Product> =
        ResponseEntity(repository.save(product), HttpStatus.CREATED)

    @PutMapping(path = ["/{id}"])
    fun update(@RequestBody product: Product, @PathVariable("id") id: Long): ResponseEntity<Product> =
        if (repository.existsById(id)) {
            product.id = id
            ResponseEntity(repository.save(product), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }

    @DeleteMapping(path = ["/{id}"])
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Product> =
        repository.findById(id).map { product: Product ->
            repository.delete(product)
            ResponseEntity(product, HttpStatus.OK)
        }.orElseGet { ResponseEntity(HttpStatus.NOT_FOUND) }

    @GetMapping("/bycode")
    fun findByCode(@RequestParam code: String): ResponseEntity<Product> =
        repository.findByCode(code).map { product: Product ->
            ResponseEntity(product, HttpStatus.OK)
        }.orElseGet { ResponseEntity(HttpStatus.NO_CONTENT) }
}
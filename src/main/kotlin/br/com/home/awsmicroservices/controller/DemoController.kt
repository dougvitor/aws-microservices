package br.com.home.awsmicroservices.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/demo")
class DemoController {

    val log: Logger = LoggerFactory.getLogger(DemoController::class.java)

    @GetMapping("/{name}")
    fun execute(@PathVariable name: String): ResponseEntity<*> =
        ResponseEntity.ok("Name: $name")
            .also { log.info("Demo controller - name: {}", name) }

}
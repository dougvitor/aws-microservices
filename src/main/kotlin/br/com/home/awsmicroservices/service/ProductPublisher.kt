package br.com.home.awsmicroservices.service

import br.com.home.awsmicroservices.enums.EventType
import br.com.home.awsmicroservices.model.Envelope
import br.com.home.awsmicroservices.model.Product
import br.com.home.awsmicroservices.model.ProductEvent
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.Topic
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ProductPublisher @Autowired constructor(
    private val snsClient: AmazonSNS,
    @Qualifier("productEventsTopic") private val productEventsTopic: Topic,
    private val mapper: ObjectMapper
) {
    private val logger: Logger = LoggerFactory.getLogger(ProductPublisher::class.java)

    fun publishProductEvent(
        product: Product,
        eventType: EventType,
        username: String
    ){
        val productEvent = ProductEvent(
            productId = product.id!!,
            code = product.code!!,
            username = username
        )

        val envelope = Envelope(eventType = eventType, data = mapper.writeValueAsString(productEvent))
        val writeEnvelopeAsString = mapper.writeValueAsString(envelope)

        logger.info(writeEnvelopeAsString)
        snsClient.publish(productEventsTopic.topicArn, writeEnvelopeAsString)
    }


}
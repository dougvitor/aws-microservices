package br.com.home.awsmicroservices.model

import br.com.home.awsmicroservices.enums.EventType

data class Envelope(
    val eventType: EventType,
    val data: String
)

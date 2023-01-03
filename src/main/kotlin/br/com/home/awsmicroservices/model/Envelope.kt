package br.com.home.awsmicroservices.model

import br.com.home.awsmicroservices.enums.EventType

data class Envelope(
    private val eventType: EventType,
    private val data: String
)

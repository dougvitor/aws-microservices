package br.com.home.awsmicroservices.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SnsMessage(
    @JsonProperty("Message")
    val message: String,
    @JsonProperty("Type")
    val type: String,
    @JsonProperty("TopicArn")
    val topicArn: String,
    @JsonProperty("Timestamp")
    val timestamp: String,
    @JsonProperty("MessageId")
    val messageId: String
)

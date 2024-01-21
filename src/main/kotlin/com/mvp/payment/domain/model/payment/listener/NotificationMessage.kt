package com.mvp.payment.domain.model.payment.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class NotificationMessage(
        val Type: String = "",
        val MessageId: String = "",
        val TopicArn: String = "",
        val Message: String = "",
        val Timestamp: String = "",
        val SignatureVersion: String = "",
        val Signature: String = "",
        val SigningCertURL: String = "",
        val UnsubscribeURL: String = ""
) {
        private val objectMapper: ObjectMapper by lazy { jacksonObjectMapper() }
        val orderNotification: OrderNotification? get() = objectMapper.readValue(Message)
}


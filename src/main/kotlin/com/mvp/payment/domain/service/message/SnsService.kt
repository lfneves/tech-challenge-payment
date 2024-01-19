package com.mvp.payment.domain.service.message

import com.mvp.payment.domain.configuration.AwsSnsConfig
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest

@Service
class SnsService(
    private val snsClient: SnsClient,
    private val awsSnsConfig: AwsSnsConfig
) {

    fun sendMessage(message: String) {
        val publishRequest = PublishRequest.builder()
            .topicArn(awsSnsConfig.topicArn)
            .message(message)
            .build()

        val publishResponse = snsClient.publish(publishRequest)
        println("Message sent. Message ID: ${publishResponse.messageId()}")
    }
}
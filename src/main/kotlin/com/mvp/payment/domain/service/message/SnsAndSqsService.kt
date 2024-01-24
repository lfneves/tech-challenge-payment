package com.mvp.payment.domain.service.message

import com.mvp.payment.domain.configuration.AwsConfig
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Service
class SnsAndSqsService(
    private val sqsClient: SqsClient,
    private val awsConfig: AwsConfig
) {

    fun sendQueueStatusMessage(message: String) {
        val sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(awsConfig.statusQueue)
            .messageBody(message)
            .delaySeconds(3)
            .build()

        sqsClient.sendMessage(sendMessageRequest)
    }
}
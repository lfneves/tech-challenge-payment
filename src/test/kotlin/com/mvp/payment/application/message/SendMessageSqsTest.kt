package com.mvp.payment.application.message

import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class SendMessageSqsTest {

    val PAYMENT_QUEUE: String = System.getenv().getOrDefault("PAYMENT_QUEUE", "")
    val STATUS_QUEUE: String = System.getenv().getOrDefault("STATUS_QUEUE", "")

    @Test
    fun `Send menssage to aws sqs test`() {
        val sqsClient = SqsClient.create()
        val queueUrl = PAYMENT_QUEUE

        val messageBody = "This is a test message"
        val sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(messageBody)
            .build()

        try {
            val sendMessageResponse = sqsClient.sendMessage(sendMessageRequest)
            println("Message sent with ID: ${sendMessageResponse.messageId()}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            sqsClient.close()
        }
    }

    @Test
    fun `PAYMENT_QUEUE - Consume menssage to aws sqs test`() {
        val sqsClient = SqsClient.create()
        val queueUrl = PAYMENT_QUEUE

        // Receive messages
        val receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(1) // You can adjust this value
            .waitTimeSeconds(10)    // Long polling
            .build()

        val messages = sqsClient.receiveMessage(receiveMessageRequest).messages()

        for (message in messages) {
            println("Received message: ${message.body()}")

            // Delete the message
            val deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build()

            sqsClient.deleteMessage(deleteMessageRequest)
            println("Deleted message with ID: ${message.messageId()}")
        }

        sqsClient.close()
    }

    @Test
    fun `STATUS_QUEUE - Consume Message to sqs Topic`() {
        val sqsClient = SqsClient.create()
        val queueUrl = STATUS_QUEUE

        val receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10) // adjust this value as needed
            .waitTimeSeconds(20)    // long polling
            .build()

        val messages = sqsClient.receiveMessage(receiveMessageRequest).messages()
        for (message in messages) {
            println("Message received: ${message.body()}")
            // Process the message here

            // Delete the message after processing to prevent it from being read again
            sqsClient.deleteMessage { builder ->
                builder.queueUrl(queueUrl).receiptHandle(message.receiptHandle())
            }
        }

        sqsClient.close()
    }
}
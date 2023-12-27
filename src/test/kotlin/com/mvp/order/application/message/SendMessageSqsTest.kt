package com.mvp.order.application.message

import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class SendMessageSqsTest {

    @Test
    fun `Send Message to SNS Topic`() {
        val snsClient = SnsClient.create()
        val topicArn = "arn:aws:sns:us-east-1:566907801160:ORDER_TOPIC"
        val message = "Send Message to SNS Topic Test"

        val publishRequest = PublishRequest.builder()
            .topicArn(topicArn)
            .message(message)
            .build()

        try {
            val publishResponse = snsClient.publish(publishRequest)
            println("Message sent. Message ID: ${publishResponse.messageId()}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            snsClient.close()
        }
    }

    @Test
    fun `Send menssage to aws sqs test`() {
        val sqsClient = SqsClient.create()
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/566907801160/PAYMENT_QUEUE"

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
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/566907801160/PAYMENT_QUEUE"

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
        val queueUrl = "https://sqs.us-east-1.amazonaws.com/566907801160/STATUS_QUEUE"

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
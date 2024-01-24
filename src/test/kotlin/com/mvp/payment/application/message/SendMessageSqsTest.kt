package com.mvp.payment.application.message

import com.mvp.payment.domain.configuration.AwsConfig
import com.mvp.payment.domain.service.message.SnsAndSqsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@ActiveProfiles("test")
class SendMessageSqsTest {

    private lateinit var awsConfig: AwsConfig
    private lateinit var sqsClient: SqsClient
    private lateinit var sqsService: SnsAndSqsService

    @BeforeEach
    fun setUp() {
        awsConfig = mockk()
        sqsClient = mockk(relaxed = true)
        sqsService = SnsAndSqsService(sqsClient, awsConfig)

        every { awsConfig.statusQueue } returns "url"
    }

    @Test
    fun `sendQueueStatusMessage sends message to SQS with correct parameters`() {
        val message = "Test Message"

        sqsService.sendQueueStatusMessage(message)

        val expectedRequest = SendMessageRequest.builder()
            .queueUrl("url")
            .messageBody(message)
            .delaySeconds(3)
            .build()

        verify { sqsClient.sendMessage(expectedRequest) }
    }
}
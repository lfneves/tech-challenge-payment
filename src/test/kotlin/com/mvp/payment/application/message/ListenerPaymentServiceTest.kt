package com.mvp.payment.application.message

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mvp.payment.domain.model.payment.listener.NotificationMessage
import com.mvp.payment.domain.service.listener.ListenerPaymentService
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class ListenerPaymentServiceTest {

    @Autowired private lateinit var orderRepository: OrderRepository
    @Autowired private lateinit var listenerPaymentService: ListenerPaymentService

    private val orderRepositoryMockk = mockk<OrderRepository>(relaxed = true)

    private lateinit var message: String

    @BeforeEach
    fun setup() {
        message = """{
          "Type" : "Notification",
          "MessageId" : "ec3dcdef-f34hjckl-sfasf342df-xctg34g",
          "TopicArn" : "arn:aws:sns:us-east-1:xxxxxxxxxxxx:ORDER_TOPIC",
          "Message" : "{\n  \"orderDTO\" : {\n    \"id\" : 1,\n    \"externalId\" : \"d417d1b5-2345-442e-ad71-8a24c7284111\",\n    \"idClient\" : 1,\n    \"totalPrice\" : 29.99,\n    \"status\" : \"Pendente\",\n    \"waitingTime\" : [ 2024, 1, 19, 3, 46, 33, 53690000 ],\n    \"productList\" : [ {\n      \"id\" : 18,\n      \"idProduct\" : 2,\n      \"idOrder\" : 1\n    } ],\n    \"finished\" : false\n  }\n}",
          "Timestamp" : "2024-01-21T03:29:31.626Z",
          "SignatureVersion" : "1",
          "Signature" : "asdasfsd5$@DFGFG/z7657gdcgsdSAG==",
          "SigningCertURL" : "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-3874283dsgsdgdsg8989d.pem",
          "UnsubscribeURL" : "https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:xxxxxxxxxxxx:ORDER_TOPIC:235kjjkdsg-d32d-4de6-bd9d-safsdfag32423sdf"
        }"""
    }

    @Test
    fun `receiveMessage should process message and save data correctly`() {
        val notificationMessage: NotificationMessage = jacksonObjectMapper().readValue(message)

        val orderEntity = OrderEntity(
            id = "1",
            externalId = "d417d1b5-2345-442e-ad71-8a24c7284111",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )

        every {  orderRepositoryMockk.save(any()) } returns orderEntity
        val savedOrder = orderRepositoryMockk.save(OrderEntity.fromOrderNotification(notificationMessage))

        listenerPaymentService.receiveMessage(message)

        Assertions.assertNotNull(orderRepositoryMockk.findByExternalId(savedOrder.externalId))
    }

    @Test
    fun testReceiveMessage() {
        val notificationMessage: NotificationMessage = jacksonObjectMapper().readValue(message)
        val orderEntity = OrderEntity(
            id = "1",
            externalId = "d417d1b5-2345-442e-ad71-8a24c7284111",
            idClient = 1,
            totalPrice = BigDecimal.TEN,
            status = "PENDING",
            isFinished = false
        )
        every { orderRepositoryMockk.save(any()) } returns orderEntity

        val savedOrder = orderRepositoryMockk.save(OrderEntity.fromOrderNotification(notificationMessage))

        listenerPaymentService.receiveMessage(message)

        Assertions.assertNotNull(orderRepositoryMockk.save(savedOrder))
    }
}
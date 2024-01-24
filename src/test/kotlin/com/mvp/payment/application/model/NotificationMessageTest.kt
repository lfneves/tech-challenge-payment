package com.mvp.payment.application.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mvp.payment.domain.model.payment.listener.NotificationMessage
import org.junit.jupiter.api.Test

class NotificationMessageTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `Order Notification convert to data class test`() {
        val jsonString = """{
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

        val notificationMessage: NotificationMessage = objectMapper.readValue(jsonString)
        println(notificationMessage)
    }
}
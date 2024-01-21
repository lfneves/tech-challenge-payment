package com.mvp.payment.application.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationMessageTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `Order Notification convert to data class test`() {
        val jsonString = """{
          "Type" : "Notification",
          "MessageId" : "ec3dd941-0e54-5708-a818-87679f8f2ec3",
          "TopicArn" : "arn:aws:sns:us-east-1:566907801160:ORDER_TOPIC",
          "Message" : "{\n  \"orderDTO\" : {\n    \"id\" : 1,\n    \"externalId\" : \"d417d1b5-9b25-442e-ad71-8a24c7284aa9\",\n    \"idClient\" : 1,\n    \"totalPrice\" : 29.99,\n    \"status\" : \"Pendente\",\n    \"waitingTime\" : [ 2024, 1, 19, 3, 46, 33, 53690000 ],\n    \"productList\" : [ {\n      \"id\" : 18,\n      \"idProduct\" : 2,\n      \"idOrder\" : 1\n    } ],\n    \"finished\" : false\n  }\n}",
          "Timestamp" : "2024-01-21T03:29:31.626Z",
          "SignatureVersion" : "1",
          "Signature" : "AoFwQldWjDsM0Hbral0r/+N9rcm/cP0x8kqi5IUlyN24PgBtOYLgdDjcMs+ZDgnRizMIAdYxRG6xFtYvHhQEYxaeqJSU9ABdBLQjijstG6JvWeV5o5Kqssj0McS6WU1RNwsOPLs428yzBzRRGigtQ4NzxkWYhuLmzXiqlc2XhUCBOOJHRrJjXWq+0U8Z3P6kE3JreCVA8gAY83ppAb2Y54GmTW3ruAxmXFigh2i8nKj+rbqyqL1XD/jfCi6DqzHvzzzd4X+v4sLdl+QlxMF4RQ3b5ymczi8yyGacpv9pynZKvh7lqh9ErdXzBMQkIorvxwlE/UwJcri/+4Zcfj58Pw==",
          "SigningCertURL" : "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-60eadc530605d63b8e62a523676ef735.pem",
          "UnsubscribeURL" : "https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:566907801160:ORDER_TOPIC:2806ddc8-d32d-4de6-bd9d-16f0319384a0"
        }"""

        val notificationMessage: NotificationMessage = objectMapper.readValue(jsonString)
        println(notificationMessage)
    }
}

data class NotificationMessage(
    val Type: String,
    val MessageId: String,
    val TopicArn: String,
    val Message: String,
    val Timestamp: String,
    val SignatureVersion: String,
    val Signature: String,
    val SigningCertURL: String,
    val UnsubscribeURL: String
) {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    val orderNotification: OrderNotification get() = objectMapper.readValue(Message)
}

data class OrderNotification(
    @JsonProperty("orderDTO") val orderDto: OrderDTO
)

data class OrderDTO(
    val id: Int,
    val externalId: String,
    val idClient: Int,
    val totalPrice: Double,
    val status: String,
    private val waitingTimeRaw: List<Int>,
    val productList: List<Product>,
    val finished: Boolean
){
    val waitingTime: LocalDateTime
        get() = LocalDateTime.of(
            waitingTimeRaw[0], // Year
            waitingTimeRaw[1], // Month
            waitingTimeRaw[2], // Day
            waitingTimeRaw[3], // Hour
            waitingTimeRaw[4], // Minute
            waitingTimeRaw[5], // Second
            waitingTimeRaw[6]  // Nanosecond
        )
}

data class Product(
    val id: Int,
    val idProduct: Int,
    val idOrder: Int
)
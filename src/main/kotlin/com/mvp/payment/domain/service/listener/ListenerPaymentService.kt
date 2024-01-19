package com.mvp.payment.domain.service.listener

import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Service

@Service
class ListenerPaymentService {

    @SqsListener("PAYMENT_QUEUE")
    fun receiveMessage(message: String) {
        println("Received message: $message")
//        println("Received message: $message with sender ID: $senderId")
        // Process the message as required
    }
}
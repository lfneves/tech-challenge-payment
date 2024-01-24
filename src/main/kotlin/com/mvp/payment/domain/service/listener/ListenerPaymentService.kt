package com.mvp.payment.domain.service.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mvp.payment.domain.model.payment.listener.NotificationMessage
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.infrastruture.repository.OrderRepository
import com.mvp.payment.infrastruture.repository.ProductRepository
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Async
class ListenerPaymentService {

    @Autowired
    private lateinit var orderRepository: OrderRepository;
    @Autowired
    private lateinit var productRepository: ProductRepository;

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @SqsListener("PAYMENT_QUEUE")
    fun receiveMessage(message: String) {
        println("Received message:")
        println(message)

        val notificationMessage: NotificationMessage = objectMapper.readValue(message)
        println(notificationMessage.orderNotification)
        saveOrderWithProducts(notificationMessage)
    }

    private fun saveOrderWithProducts(notificationMessage: NotificationMessage) {
        val savedOrder = orderRepository.save(OrderEntity.fromOrderNotification(notificationMessage))

        notificationMessage.orderNotification?.orderDTO?.productList?.forEach { product ->
            product.idOrder = savedOrder.id.toString()
            product.externalId = savedOrder.externalId
            productRepository.save(product)
        }
    }
}
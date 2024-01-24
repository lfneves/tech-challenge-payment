package com.mvp.payment.infrastruture.entity

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.listener.NotificationMessage
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Document(collection = "orderEntity")
data class OrderEntity(
    @Id
    var id: String,
    @Field("external_id")
    val externalId: String = "",
    @Field("id_client")
    var idClient: Int? = null,
    @Field("username")
    var username: String? = null,
    @Field("total_price")
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    @Field("status")
    var status: String = "",
    @Field("updateStatus")
    var updateStatus: String = "",
    @Field("waiting_time")
    var waitingTime: LocalDateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime(),
    @Field("is_finished")
    var isFinished: Boolean = false,
    val className: String = "com.mvp.payment.infrastructure.entity.OrderEntity"
) {
    companion object {

        fun fromOrderNotification(notificationMessage: NotificationMessage): OrderEntity {
            return OrderEntity(
                id = notificationMessage.orderNotification!!.orderDTO.id.toString(),
                externalId = notificationMessage.orderNotification!!.orderDTO.externalId,
                idClient = notificationMessage.orderNotification?.orderDTO?.idClient,
                totalPrice = notificationMessage.orderNotification?.orderDTO?.totalPrice!!,
                status = notificationMessage.orderNotification!!.orderDTO.status,
                waitingTime = notificationMessage.orderNotification!!.orderDTO.waitingTime,
                isFinished = notificationMessage.orderNotification!!.orderDTO.isFinished
            )
        }

        fun fromOrderByIdResponseDTO(orderByIdResponseDTO: OrderByIdResponseDTO): OrderEntity {
            return OrderEntity(
                id =  orderByIdResponseDTO.id.toString(),
                externalId = orderByIdResponseDTO.externalId,
                idClient = orderByIdResponseDTO.idClient,
                totalPrice = orderByIdResponseDTO.totalPrice,
                status = orderByIdResponseDTO.status,
                waitingTime = orderByIdResponseDTO.waitingTime,
                isFinished = orderByIdResponseDTO.isFinished
            )
        }
    }
}
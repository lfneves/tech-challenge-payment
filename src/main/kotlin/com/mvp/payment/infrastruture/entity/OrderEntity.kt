package com.mvp.payment.infrastruture.entity

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.listener.NotificationMessage
import com.mvp.payment.domain.model.payment.listener.Product
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Document(collection = "orderEntity")
data class OrderEntity(
    @Id
    var id: Long?,
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
    fun toResponseDTO(): OrderByIdResponseDTO {
        return OrderByIdResponseDTO(
            id = this.id,
            externalId = this.externalId,
            idClient = this.idClient,
            totalPrice = this.totalPrice,
            status = this.status,
            waitingTime = this.waitingTime,
            isFinished = this.isFinished
        )
    }

    companion object {
        fun fromOrderNotification(notificationMessage: NotificationMessage): OrderEntity {
            return OrderEntity(
                id = notificationMessage.orderNotification!!.orderDTO.id,
                externalId = notificationMessage.orderNotification!!.orderDTO.externalId,
                idClient = notificationMessage.orderNotification?.orderDTO?.idClient,
                totalPrice = BigDecimal.valueOf(notificationMessage.orderNotification?.orderDTO?.totalPrice!!),
                status = notificationMessage.orderNotification!!.orderDTO.status,
                waitingTime = notificationMessage.orderNotification!!.orderDTO.waitingTime,
                isFinished = notificationMessage.orderNotification!!.orderDTO.finished
            )
        }

        fun fromOrderByIdResponseDTO(orderByIdResponseDTO: OrderByIdResponseDTO): OrderEntity {
            return OrderEntity(
                id =  orderByIdResponseDTO.id!!,
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
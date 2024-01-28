package com.mvp.payment.domain.model.payment.listener

import com.fasterxml.jackson.annotation.JsonProperty
import com.mvp.payment.infrastruture.entity.OrderEntity
import com.mvp.payment.utils.Utils
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDTO(
        val id: Long = 0,
        val externalId: String = "",
        val idClient: Int = 0,
        val totalPrice: BigDecimal = BigDecimal.ZERO,
        val status: String = "",
        @JsonProperty("waitingTime")
        private val waitingTimeRaw: List<Int> = emptyList(),
        var productList: List<Product> = emptyList(),
        @JsonProperty("finished")
        val isFinished: Boolean = false,
) {
        val waitingTime: LocalDateTime
                get() = LocalDateTime.of(
                        waitingTimeRaw.getOrElse(0) { 0 },
                        waitingTimeRaw.getOrElse(1) { 1 },
                        waitingTimeRaw.getOrElse(2) { 1 },
                        waitingTimeRaw.getOrElse(3) { 0 },
                        waitingTimeRaw.getOrElse(4) { 0 },
                        waitingTimeRaw.getOrElse(5) { 0 },
                        waitingTimeRaw.getOrElse(6) { 0 }
                )

        companion object {
                fun mapOrderEntityToDTO(orderEntity: OrderEntity): OrderDTO {
                        return OrderDTO(
                                externalId = orderEntity.externalId,
                                idClient = orderEntity.idClient!!,
                                totalPrice = orderEntity.totalPrice,
                                status = orderEntity.status,
                                waitingTimeRaw = Utils.convertLocalDateTimeToList(orderEntity.waitingTime!!),
                                isFinished = orderEntity.isFinished,
                        )
                }
        }
}
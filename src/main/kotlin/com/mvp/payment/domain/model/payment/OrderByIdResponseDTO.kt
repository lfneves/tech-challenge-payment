package com.mvp.payment.domain.model.payment

import com.mvp.payment.domain.model.payment.listener.Product
import com.mvp.payment.domain.model.status.StatusDTO
import com.mvp.payment.infrastruture.entity.OrderEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class OrderByIdResponseDTO(
    var id: Long? = null,
    var externalId: String = "",
    var idClient: Int? = null,
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    var status: String = "",
    var waitingTime: LocalDateTime = ZonedDateTime.now(ZoneId.of( "America/Sao_Paulo")).toLocalDateTime(),
    var isFinished: Boolean = false,
    var products: MutableList<Product> = mutableListOf()
) {
    fun toStatusDTO(): StatusDTO {
        return StatusDTO(
            externalId = this.externalId,
            idClient = this.idClient,
            totalPrice = this.totalPrice,
            status = this.status,
            waitingTime = this.waitingTime,
            isFinished = this.isFinished
        )
    }

    companion object {
        fun fromOrderEntityToOrderByIdResponseDTO(orderEntity: OrderEntity): OrderByIdResponseDTO {
            return OrderByIdResponseDTO(
                externalId = orderEntity.externalId,
                idClient = orderEntity.idClient,
                totalPrice = orderEntity.totalPrice,
                status = orderEntity.status,
                waitingTime = orderEntity.waitingTime,
                isFinished = orderEntity.isFinished
            )
        }
    }
}

data class OrderProductResponseDTO(
    var id: Long? = null,
    var idProduct: Long? = null,
    var idOrder: Long? = null,
    var productName: String? = null,
    var categoryName: String? = null,
    var price: BigDecimal = BigDecimal.ZERO
)
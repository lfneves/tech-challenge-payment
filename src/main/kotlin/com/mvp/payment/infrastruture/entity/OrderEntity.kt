package com.mvp.payment.infrastruture.entity

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Document
data class OrderEntity(
    @Id
    var id: Long? = null,

    @Field("external_id")
    var externalId: UUID? = null,

    @Field("id_client")
    var idClient: Int? = null,

    @Field("username ")
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
    var isFinished: Boolean = false
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
}
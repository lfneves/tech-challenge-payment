package com.mvp.order.infrastruture.entity

import com.mvp.order.domain.model.payment.OrderByIdResponseDTO
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Table("tb_order")
data class OrderEntity(
    @Id
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = [CascadeType.PERSIST])
    var id: Long? = null,
    @Column("external_id") var externalId: UUID? = null,
    @Column("id_client") var idClient: Int? = null,
    @Column("total_price") var totalPrice: BigDecimal = BigDecimal.ZERO,
    @Column("status") var status: String = "",
    @Column("waiting_time")
    var waitingTime: LocalDateTime = ZonedDateTime.now(ZoneId.of( "America/Sao_Paulo")).toLocalDateTime(),
    @Column("is_finished") var isFinished: Boolean = false
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
package com.mvp.payment.infrastruture.repository

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.infrastruture.entity.OrderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : MongoRepository<OrderEntity, Long> {

    fun findByUsername(username: String?): OrderEntity

    override fun findById(id: Long): Optional<OrderEntity>

    fun findByExternalId(externalId: UUID): OrderEntity

    fun updateStatus(orderEntity: OrderByIdResponseDTO?): OrderEntity
}
package com.mvp.order.infrastruture.repository

import com.mvp.order.infrastruture.entity.OrderEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.*

interface OrderRepository : ReactiveMongoRepository<OrderEntity?, Long?> {

    fun findByUsername(username: String?): Mono<OrderEntity>

    fun findByIdOrder(id: Long): Mono<OrderEntity>

    fun findByExternalId(externalId: UUID): Mono<OrderEntity>

    suspend fun updateStatus(orderEntity: OrderEntity): OrderEntity
}
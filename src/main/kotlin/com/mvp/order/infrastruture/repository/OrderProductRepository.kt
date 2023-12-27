package com.mvp.order.infrastruture.repository

import com.mvp.order.infrastruture.entity.OrderProductEntity
import com.mvp.order.infrastruture.entity.OrderProductResponseEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface OrderProductRepository : ReactiveMongoRepository<OrderProductEntity?, Long?> {

    fun findAllByIdOrderInfo(id: Long): Flux<OrderProductResponseEntity>
}
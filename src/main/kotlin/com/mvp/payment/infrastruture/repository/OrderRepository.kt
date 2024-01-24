package com.mvp.payment.infrastruture.repository

import com.mvp.payment.infrastruture.entity.OrderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional


@Repository
interface OrderRepository : MongoRepository<OrderEntity, Long> {

    fun findByExternalId(externalId: String): Optional<OrderEntity>
}
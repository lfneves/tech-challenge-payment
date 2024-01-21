package com.mvp.payment.infrastruture.repository

import com.mvp.payment.domain.model.payment.listener.Product
import com.mvp.payment.infrastruture.entity.OrderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductRepository : MongoRepository<Product, String>
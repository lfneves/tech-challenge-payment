package com.mvp.payment.domain.service.payment

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import java.util.*

interface PaymentService {

    fun getOrderById(id: Long): OrderByIdResponseDTO

    suspend fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO?

    fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): Boolean
}
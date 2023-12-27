package com.mvp.order.domain.service.payment

import com.mvp.order.domain.model.payment.OrderByIdResponseDTO
import com.mvp.order.domain.model.payment.OrderCheckoutDTO
import reactor.core.publisher.Mono
import java.util.*

interface PaymentService {

    fun getOrderById(id: Long): Mono<OrderByIdResponseDTO>

    suspend fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO?

    fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): Mono<Boolean>
}
package com.mvp.payment.domain.service.payment

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.OrderCheckoutDTO
import com.mvp.payment.domain.model.payment.OrderFinishDTO
import java.util.*

interface PaymentService {

    fun getOrderById(id: Long): OrderByIdResponseDTO

    fun getOrderByExternalId(externalId: UUID): OrderByIdResponseDTO?

    fun fakeCheckoutOrder(orderCheckoutDTO: OrderCheckoutDTO): OrderByIdResponseDTO

    fun finishedOrderWithPayment(orderCheckoutDTO: OrderCheckoutDTO): OrderFinishDTO
}
package com.mvp.order.domain.service.payment

import com.mvp.order.domain.model.payment.OrderByIdResponseDTO
import com.mvp.order.domain.model.payment.store.QrDataDTO
import com.mvp.order.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.order.domain.model.payment.store.webhook.MerchantOrderResponseDTO
import reactor.core.publisher.Mono

interface MPOrderService {

    suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO): Mono<Void>

    fun generateOrderQrs(requestBody: String): Mono<QrDataDTO>

    fun orderCheckoutGenerateQrs(order: OrderByIdResponseDTO): String

    fun getMerchantOrderByID(requestUrl: String): Mono<MerchantOrderResponseDTO>

    fun checkoutOrder(username: String): Mono<QrDataDTO>
}
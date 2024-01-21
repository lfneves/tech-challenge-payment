package com.mvp.payment.domain.service.payment

import com.mvp.payment.domain.model.payment.OrderByIdResponseDTO
import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderResponseDTO

interface MPOrderService {

    suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO)

    suspend fun generateOrderQrs(requestBody: String): QrDataDTO

    fun orderCheckoutGenerateQrs(order: OrderByIdResponseDTO): String

    suspend fun getMerchantOrderByID(requestUrl: String): MerchantOrderResponseDTO

    suspend fun checkoutOrder(externalId: String): QrDataDTO
}
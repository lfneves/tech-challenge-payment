package com.mvp.payment.domain.service.payment

import com.mvp.payment.domain.model.payment.store.QrDataDTO
import com.mvp.payment.domain.model.payment.store.webhook.MerchantOrderDTO

interface MPOrderService {

    suspend fun saveCheckoutOrderExternalStoreID(merchantOrderDTO: MerchantOrderDTO)

    suspend fun generateOrderQrs(requestBody: String): QrDataDTO
//
//    fun orderCheckoutGenerateQrs(order: OrderDTO): String
//
//    suspend fun getMerchantOrderByID(requestUrl: String): MerchantOrderResponseDTO

    suspend fun checkoutOrder(externalId: String): QrDataDTO
}
package com.mvp.payment.domain.model.payment.store.payment

data class PaymentRequest(
    val transactionAmount: Float,
    val token: String,
    val description: String,
    val installments: Int,
    val paymentMethodId: String,
    val notificationUrl: String
)
package com.mvp.payment.domain.model.payment

import org.jetbrains.annotations.NotNull

data class OrderFinishDTO(
    @NotNull
    var idOrder: Long = 0,
    var isFinished: Boolean = false
)

data class OrderCheckoutDTO(
    var idOrder: Long = 0,
    var isPayment: Boolean = false
)
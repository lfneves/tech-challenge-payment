package com.mvp.payment.domain.model.payment

data class OrderFinishDTO(
    var isFinished: Boolean = false,
    val externalId: String = ""
)
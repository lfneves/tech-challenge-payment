package com.mvp.payment.domain.model.payment.listener

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderNotification(
    @JsonProperty("orderDTO") val orderDTO: OrderDTO
)
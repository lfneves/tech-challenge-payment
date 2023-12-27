package com.mvp.order.domain.model.payment.store.webhook

import com.fasterxml.jackson.annotation.JsonProperty

data class MerchantOrderDTO(
    @JsonProperty("resource")
    var resource: String,
    @JsonProperty("topic")
    var topic: String
)
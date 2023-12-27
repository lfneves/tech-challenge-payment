package com.mvp.order.domain.model.payment.store


import com.fasterxml.jackson.annotation.JsonProperty

data class QrDataDTO(
    @JsonProperty("in_store_order_id")
    var inStoreOrderId: String,
    @JsonProperty("qr_data")
    var qrData: String
)
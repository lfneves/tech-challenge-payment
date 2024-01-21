package com.mvp.payment.domain.model.payment.store


import com.fasterxml.jackson.annotation.JsonProperty

data class QrDataDTO(
    @JsonProperty("in_store_order_id")
    var inStoreOrderId: String = "",
    @JsonProperty("qr_data")
    var qrData: String = "",
    var error:String = "",
    var message: String = "",
    var status: String = "",
    var causes: List<String> = listOf()
)
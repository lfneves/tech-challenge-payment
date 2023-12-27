package com.mvp.order.domain.model.payment.store.webhook


import com.fasterxml.jackson.annotation.JsonProperty

data class Item(
    @JsonProperty("category_id")
    var categoryId: String?,
    @JsonProperty("currency_id")
    var currencyId: String?,
    @JsonProperty("description")
    var description: String?,
    @JsonProperty("id")
    var id: String?,
    @JsonProperty("picture_url")
    var pictureUrl: Any?,
    @JsonProperty("quantity")
    var quantity: Int?,
    @JsonProperty("title")
    var title: String?,
    @JsonProperty("unit_price")
    var unitPrice: Int?
)
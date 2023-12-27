package com.mvp.order.domain.model.payment.store

data class ItemDTO(
    val category: String,
    val description: String,
    val quantity: Int,
    val sku_number: String,
    val title: String,
    val total_amount: Int,
    val unit_measure: String,
    val unit_price: Int
)
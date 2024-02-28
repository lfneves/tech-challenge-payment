package com.mvp.payment.domain.model.auth

import io.swagger.v3.oas.annotations.media.Schema

@JvmRecord
data class ApiErrorResponse(
    @field:Schema(description = "Error code") @param:Schema(description = "Error code") val errorCode: Int,
    @field:Schema(description = "Error description") @param:Schema(description = "Error description") val description: String
)

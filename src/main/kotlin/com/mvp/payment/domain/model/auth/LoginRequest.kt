package com.mvp.order.domain.model.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


@JvmRecord
data class LoginRequest(
    val username: @NotBlank(message = "Username cannot be blank") String,

    @field:Size(
        min = 6,
        max = 20,
        message = "Password must be between 6 and 20 characters"
    ) @field:Schema(description = "password", example = "123456")
        @param:Schema(
        description = "password",
        example = "123456"
    ) @param:Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    val password: @NotBlank(message = "Password cannot be blank") String
)

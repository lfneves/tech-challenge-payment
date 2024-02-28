package com.mvp.order.domain.model.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(

    @NotBlank(message = "Name cannot be blank")
    val name: String = "",

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    val email: String = "",

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    val password: String,

    var username: String? = null,
)
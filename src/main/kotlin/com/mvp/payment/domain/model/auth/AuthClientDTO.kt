package com.mvp.order.domain.model.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@JvmRecord
data class AuthClientDTO(
    val name: String,
    val email: String,
    val username: String,
    val password: String
): UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =  mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
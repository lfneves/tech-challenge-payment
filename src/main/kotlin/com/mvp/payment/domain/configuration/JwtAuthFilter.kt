package com.mvp.payment.domain.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.mvp.payment.domain.configuration.jwt.JWTUtils
import com.mvp.payment.domain.model.auth.ApiErrorResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthFilter(
    private val objectMapper: ObjectMapper
): OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            var token: String? = null
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7)
            }

            if (token == null) {
                filterChain.doFilter(request, response)
                return
            }

            if(SecurityContextHolder.getContext().authentication == null) {
                if (JWTUtils.validateTokenSecret(token)) {
                    val authenticationToken = UsernamePasswordAuthenticationToken(token, null, null)
                    authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                }
            }

            filterChain.doFilter(request, response)
        } catch (e: AccessDeniedException) {
            val errorResponse = ApiErrorResponse(HttpServletResponse.SC_FORBIDDEN, e.stackTraceToString())
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.writer.write(toJson(errorResponse))
        }
    }

    private fun toJson(response: ApiErrorResponse): String {
        return try {
            objectMapper.writeValueAsString(response)
        } catch (e: Exception) {
            "" // Return an empty string if serialization fails
        }
    }
}
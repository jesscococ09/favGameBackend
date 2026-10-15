package com.example.demo.security

import com.example.demo.service.GithubUserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class GithubAuthenticationFilter(
    private val githubUserService: GithubUserService
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(GithubAuthenticationFilter::class.java)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            val token = authHeader.removePrefix("Bearer").trim()
            try {
                val user = githubUserService.loadOrCreateUser(token)
                val authentication = GithubUserPrincipal(user)
                authentication.isAuthenticated = true
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                logger.warn("GitHub authentication failed: ${e.message}")
            }
        }
        filterChain.doFilter(request, response)
    }
}
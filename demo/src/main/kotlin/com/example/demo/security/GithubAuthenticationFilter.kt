package com.example.demo.security

import com.example.demo.service.GithubUserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class GithubAuthenticationFilter(private val githubUserService: GithubUserService): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader= request.getHeader("Authorization")
        if(authHeader != null && authHeader.startsWith("Bearer")){
            val token= authHeader.removePrefix("Bearer ").trim()
            try{
                val user = githubUserService.loadOrCreateUser(token)
                val authentication = GithubUserPrincipal(user)
                authentication.isAuthenticated = true
                SecurityContextHolder.getContext().authentication = authentication
            }catch (_: Exception){

            }
        }
        filterChain.doFilter(request, response)
    }
}
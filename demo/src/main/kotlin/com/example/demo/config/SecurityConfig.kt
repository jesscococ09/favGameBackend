package com.example.demo.config

import com.example.demo.security.GithubAuthenticationFilter
import com.example.demo.service.GithubUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val githubUserService: GithubUserService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val githubFilter = GithubAuthenticationFilter(githubUserService)
        http
            .csrf { it.disable() }
            .addFilterBefore(githubFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it.requestMatchers("/favGame/v1/admin/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
        return http.build()
    }
}

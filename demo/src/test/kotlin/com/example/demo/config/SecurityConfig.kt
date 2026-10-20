package com.example.demo.config

import com.example.demo.security.GithubAuthenticationFilter
import com.example.demo.service.GithubUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint

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
            .exceptionHandling {
                it.authenticationEntryPoint(
                    HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
            }
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                githubFilter,
                AnonymousAuthenticationFilter::class.java
            )

        return http.build()
    }
}

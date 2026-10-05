package com.example.demo.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestAuthController {
    @GetMapping("/test/oauth")
    fun testOauth(auth: Authentication): Any{
        return mapOf(
            "principal" to auth.principal,
            "authorities" to auth.authorities
        )
    }
}
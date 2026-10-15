package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favGame/v1/users")
class UserController(
    private val userService: UserService
) {
    private fun currentUser(auth: Authentication): User {
        val principal = auth.principal
        return principal as User
    }
    @GetMapping
    fun getMyProfile(auth: Authentication): User { return currentUser(auth) }
    @PatchMapping("/name")
    fun updateDisplayName(
        auth: Authentication,
        @RequestBody body: Map<String, String>
    ): User {
        val user = currentUser(auth)
        val newName = body["displayName"] ?: throw IllegalArgumentException("requires displayName")
        return userService.updateDisplayName(user.githubId, newName)
    }
    @PatchMapping("/icon")
    fun updateIcon(
        auth: Authentication,
        @RequestBody body: Map<String, String>
    ): User {
        val user = currentUser(auth)
        val newIcon = body["iconUrl"] ?: throw IllegalArgumentException("requires iconUrl")
        return userService.updateIcon(user.githubId, newIcon)
    }

    @DeleteMapping
    fun deleteMyAccount(
        auth: Authentication
    ): Map<String, String> {
        val user = currentUser(auth)
        userService.deleteUserAndData(user.githubId)
        return mapOf("status" to "deleted")
    }
}
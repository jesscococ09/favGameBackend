package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.security.GithubUserPrincipal
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
class UserController(private val userService: UserService) {
    @GetMapping
    fun getMyProfile(auth: Authentication): User{
        val principal= auth.principal as GithubUserPrincipal
        return principal.user
    }

    @PatchMapping("/name")
    fun updateDisplayName(
        auth: Authentication,
        @RequestBody body: Map<String, String>
    ): User{
        val principal= auth.principal as GithubUserPrincipal
        val newName= body["displayName"] ?: throw IllegalArgumentException("requires displayName")
        return userService.updateDisplayName(principal.user.githubId,newName)
    }

    @PatchMapping("/icon")
    fun updateIcon(
        auth: Authentication,
        @RequestBody body: Map<String, String>
    ): User{
        val principal= auth.principal as GithubUserPrincipal
        val newIcon = body["iconUrl"] ?: throw IllegalArgumentException("requires iconUrl")
        return userService.updateIcon(principal.user.githubId, newIcon)
    }

    @DeleteMapping
    fun deleteMyAccount(auth: Authentication): Map<String,String>{
        val principal= auth.principal as GithubUserPrincipal
        userService.deleteUserAndData(principal.user.githubId)
        return mapOf("status" to "deleted")
    }

}
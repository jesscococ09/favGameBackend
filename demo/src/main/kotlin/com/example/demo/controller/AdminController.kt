package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.AdminService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favGame/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService
) {
    private fun currentUser(auth: Authentication): User {
        return auth.principal as User
    }
    @GetMapping
    fun listUsers(auth: Authentication): List<User> {
        currentUser(auth)
        return adminService.listUsers()
    }
    @GetMapping("/{userId}")
    fun getUser(
        auth: Authentication,
        @PathVariable userId: String
    ): User {
        currentUser(auth)
        return adminService.getUser(userId)
    }
    @PatchMapping("/{userId}")
    fun updateUser(
        auth: Authentication,
        @PathVariable userId: String,
        @RequestBody body: Map<String, Any>
    ): User {
        currentUser(auth)
        return adminService.updateUser(
            userId,
            body
        )
    }
    @DeleteMapping("/{userId}")
    fun deleteUser(
        auth: Authentication,
        @PathVariable userId: String
    ): Map<String, String> {
        currentUser(auth)
        adminService.deleteUserAndData(userId)
        return mapOf(
            "status" to "deleted"
        )
    }
}
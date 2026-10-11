package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.AdminService
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
class AdminController(private val adminService: AdminService) {
    @GetMapping
    fun listUsers(auth: Authentication): List<User>{
        return adminService.listUsers()
    }
    @GetMapping("/{userId}")
    fun getUser(
        auth: Authentication,
        @PathVariable
        userId: String
    ): User{
        return adminService.getUser(userId)
    }
    @PatchMapping("/{userId}")
    fun updateUser(
        auth: Authentication,
        @PathVariable
        userId: String,
        @RequestBody
        body: Map<String,Any>
    ):User{
        return adminService.updateUser(userId,body)
    }
    @DeleteMapping("/{userId}")
    fun deleteUser(
        auth: Authentication,
        @PathVariable
        userId: String
    ):Map<String, String>{
        adminService.deleteUserAndData(userId)
        return mapOf("status" to "deleted")
    }
}
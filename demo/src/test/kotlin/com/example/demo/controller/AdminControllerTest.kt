package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.service.AdminService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AdminControllerTest {
    private fun adminAuth(): Authentication {
        val user = User(
            id = "admin1",
            githubId = "admin123",
            displayName = "AdminUser",
            iconUrl = "icon.png",
            isAdmin = true
        )

        return UsernamePasswordAuthenticationToken(
            user,
            "",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )
    }
    @Test
    fun `GET listUsers returns all users`() {
        val service = mock(AdminService::class.java)
        val controller = AdminController(service)
        val auth = adminAuth()
        val users = listOf(
            User("1", "gh1", "A", "i1", false),
            User("2", "gh2", "B", "i2", true)
        )
        `when`(service.listUsers()).thenReturn(users)
        val result = controller.listUsers(auth)
        assertEquals(2, result.size)
        verify(service).listUsers()
    }

    @Test
    fun `GET getUser returns user`() {
        val service = mock(AdminService::class.java)
        val controller = AdminController(service)
        val auth = adminAuth()
        val user = User(
            id = "1",
            githubId = "gh1",
            displayName = "Random",
            iconUrl = "icon.png",
            isAdmin = false
        )
        `when`(service.getUser("1")).thenReturn(user)
        val result = controller.getUser(auth, "1")
        assertEquals("gh1", result.githubId)
        verify(service).getUser("1")
    }

    @Test
    fun `PATCH updateUser toggles admin flag`() {
        val service = mock(AdminService::class.java)
        val controller = AdminController(service)
        val auth = adminAuth()

        val updated = User(
            id = "1",
            githubId = "gh1",
            displayName = "Random",
            iconUrl = "icon.png",
            isAdmin = true
        )

        val body: Map<String, Any> = mapOf(
            "isAdmin" to true
        )
        `when`(service.updateUser("1", body)).thenReturn(updated)
        val result = controller.updateUser(auth, "1", body)
        assertEquals(true, result.isAdmin)
        verify(service).updateUser("1", body)
    }

    @Test
    fun `DELETE deleteUser removes user and data`() {
        val service = mock(AdminService::class.java)
        val controller = AdminController(service)
        val auth = adminAuth()
        val result = controller.deleteUser(auth, "1")
        assertEquals("deleted", result["status"])
        verify(service).deleteUserAndData("1")
    }
}
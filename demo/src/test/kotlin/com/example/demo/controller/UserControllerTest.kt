package com.example.demo.controller

import com.example.demo.model.User
import com.example.demo.security.GithubUserPrincipal
import com.example.demo.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class UserControllerTest {

    private fun testAuth(): Authentication {
        val user = User(
            id = "123",
            githubId = "gh123",
            displayName = "Jessika",
            iconUrl = "icon.png",
            isAdmin = false
        )
        val principal = GithubUserPrincipal(user)
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }

    @Test
    fun `GET my profile returns user`() {
        val controller = UserController(mock(UserService::class.java))
        val auth = testAuth()

        val result = controller.getMyProfile(auth)

        assertEquals("gh123", result.githubId)
        assertEquals("Jessika", result.displayName)
    }

    @Test
    fun `PATCH update displayName`() {
        val userService = mock(UserService::class.java)
        val controller = UserController(userService)
        val auth = testAuth()

        val updated = User("123", "gh123", "NewName", "icon.png", false)
        `when`(userService.updateDisplayName("gh123", "NewName")).thenReturn(updated)

        val result = controller.updateDisplayName(auth, mapOf("displayName" to "NewName"))

        assertEquals("NewName", result.displayName)
        verify(userService).updateDisplayName("gh123", "NewName")
    }

    @Test
    fun `PATCH update icon`() {
        val userService = mock(UserService::class.java)
        val controller = UserController(userService)
        val auth = testAuth()

        val updated = User("123", "gh123", "Jessika", "newIcon.png", false)
        `when`(userService.updateIcon("gh123", "newIcon.png")).thenReturn(updated)

        val result = controller.updateIcon(auth, mapOf("iconUrl" to "newIcon.png"))

        assertEquals("newIcon.png", result.iconUrl)
        verify(userService).updateIcon("gh123", "newIcon.png")
    }

    @Test
    fun `DELETE user deletes account`() {
        val userService = mock(UserService::class.java)
        val controller = UserController(userService)
        val auth = testAuth()

        val result = controller.deleteMyAccount(auth)

        assertEquals("deleted", result["status"])
        verify(userService).deleteUserAndData("gh123")
    }
}

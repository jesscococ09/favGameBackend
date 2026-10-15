package com.example.demo.controller

import com.example.demo.model.RecommendedGames
import com.example.demo.model.User
import com.example.demo.service.RecommendedGamesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

class RecommendedGamesControllerTest {

    private fun testAuth(
        isAdmin: Boolean = true
    ): Authentication {
        val user = User(
            id = "123",
            githubId = "admin123",
            displayName = "AdminUser",
            iconUrl = "icon.png",
            isAdmin = isAdmin
        )
        val authorities =
            if (isAdmin) {
                listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            } else {
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            }
        return UsernamePasswordAuthenticationToken(
            user,
            "",
            authorities
        )
    }

    @Test
    fun `GET listRecommendations returns all`() {
        val service = mock(RecommendedGamesService::class.java)
        val controller = RecommendedGamesController(service)
        val recommendations = listOf(
            RecommendedGames(
                "1",
                "Game1",
                "t",
                "d",
                "p",
                "g",
                5,
                "c"
            )
        )
        `when`(service.listAll()).thenReturn(recommendations)
        val result = controller.listRecommendations()
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        verify(service).listAll()
    }

    @Test
    fun `POST createRecommendation creates new recommendation`() {
        val service = mock(RecommendedGamesService::class.java)
        val controller = RecommendedGamesController(service)
        val auth = testAuth()
        val body: Map<String, Any> = mapOf(
            "gameName" to "New Game"
        )
        val saved = RecommendedGames(
            "1",
            "New Game",
            "t",
            "d",
            "p",
            "g",
            5,
            "c"
        )
        `when`(
            service.createRecommendation("admin123", body)
        ).thenReturn(saved)
        val result = controller.createRecommendation(
            auth,
            body
        )
        assertEquals("1", result.id)
        verify(service).createRecommendation(
            "admin123",
            body
        )
    }

    @Test
    fun `PATCH updateRecommendation updates fields`() {
        val service = mock(RecommendedGamesService::class.java)
        val controller = RecommendedGamesController(service)
        val auth = testAuth()
        val body: Map<String, Any> = mapOf(
            "rating" to 9
        )
        val updated = RecommendedGames(
            "1",
            "Game1",
            "t",
            "d",
            "p",
            "g",
            9,
            "c"
        )
        `when`(
            service.updateRecommendation(
                "admin123",
                "1",
                body
            )
        ).thenReturn(updated)
        val result = controller.updateRecommendation(
            auth,
            "1",
            body
        )
        assertEquals(9, result.rating)
        verify(service).updateRecommendation(
            "admin123",
            "1",
            body
        )
    }

    @Test
    fun `PUT replaceRecommendation replaces all fields`() {
        val service = mock(RecommendedGamesService::class.java)
        val controller = RecommendedGamesController(service)
        val auth = testAuth()
        val body: Map<String, Any> = mapOf(
            "gameName" to "NewName",
            "thumbnail" to "new.png",
            "gameDescription" to "newDesc",
            "platform" to "PC",
            "genre" to "Adventure",
            "rating" to 10,
            "comment" to "Amazing!"
        )
        val replaced = RecommendedGames(
            "1",
            "NewName",
            "new.png",
            "newDesc",
            "PC",
            "Adventure",
            10,
            "Amazing!"
        )
        `when`(
            service.replaceRecommendation(
                "admin123",
                "1",
                body
            )
        ).thenReturn(replaced)
        val result = controller.replaceRecommendation(
            auth,
            "1",
            body
        )
        assertEquals("NewName", result.gameName)
        assertEquals("new.png", result.thumbnail)
        assertEquals("newDesc", result.gameDescription)
        assertEquals("PC", result.platform)
        assertEquals("Adventure", result.genre)
        assertEquals(10, result.rating)
        assertEquals("Amazing!", result.comment)
        verify(service).replaceRecommendation(
            "admin123",
            "1",
            body
        )
    }

    @Test
    fun `DELETE deleteRecommendation removes recommendation`() {
        val service = mock(RecommendedGamesService::class.java)
        val controller = RecommendedGamesController(service)
        val auth = testAuth()
        val result = controller.deleteRecommendation(
            auth,
            "1"
        )
        assertEquals("deleted", result["status"])
        verify(service).deleteRecommendation(
            "admin123",
            "1"
        )
    }
}
package com.example.demo.controller

import com.example.demo.model.API
import com.example.demo.model.User
import com.example.demo.security.GithubUserPrincipal
import com.example.demo.service.APIService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class APIControllerTest {

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
    fun `GET listMy returns games`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val games = listOf(
            API("1", "gh123", "t", "d", "p", "g", 5, "c")
        )

        `when`(apiService.listMyGames("gh123", 0, 20, null, null)).thenReturn(games)

        val result = controller.listMy(auth, 0, 20, null, null)

        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        verify(apiService).listMyGames("gh123", 0, 20, null, null)
    }

    @Test
    fun `GET searchMyGames returns results`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val games = listOf(
            API("1", "gh123", "t", "Zelda adventure", "p", "g", 5, "c")
        )

        `when`(apiService.searchMyGames("gh123", "Zelda")).thenReturn(games)

        val result = controller.searchMyGames(auth, "Zelda")

        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        verify(apiService).searchMyGames("gh123", "Zelda")
    }

    @Test
    fun `GET getMyGame returns game`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val game = API("1", "gh123", "t", "d", "p", "g", 5, "c")

        `when`(apiService.getMyGame("gh123", "1")).thenReturn(game)

        val result = controller.getMyGame(auth, "1")

        assertEquals("1", result.id)
        verify(apiService).getMyGame("gh123", "1")
    }

    @Test
    fun `POST addGame creates game`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val body = mapOf("thumbnail" to "t", "gameDescription" to "d")
        val saved = API("1", "gh123", "t", "d", "p", "g", 5, "c")

        `when`(apiService.addGame("gh123", body)).thenReturn(saved)

        val result = controller.addGame(auth, body)

        assertEquals("1", result.id)
        verify(apiService).addGame("gh123", body)
    }

    @Test
    fun `PATCH updateGame updates rating and comment`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val body = mapOf("rating" to 9, "comment" to "updated")
        val updated = API("1", "gh123", "t", "d", "p", "g", 9, "updated")

        `when`(apiService.updateGame("gh123", "1", body)).thenReturn(updated)

        val result = controller.updateGame(auth, "1", body)

        assertEquals(9, result.rating)
        assertEquals("updated", result.comment)
        verify(apiService).updateGame("gh123", "1", body)
    }

    @Test
    fun `DELETE deleteGame removes game`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val result = controller.deleteGame(auth, "1")

        assertEquals("deleted", result["status"])
        verify(apiService).deleteGame("gh123", "1")
    }

    @Test
    fun `POST addComment updates comment`() {
        val apiService = mock(APIService::class.java)
        val controller = APIController(apiService)
        val auth = testAuth()

        val updated = API("1", "gh123", "t", "d", "p", "g", 5, "new comment")

        `when`(apiService.addComment("gh123", "1", "new comment")).thenReturn(updated)

        val result = controller.addComment(auth, "1", mapOf("comment" to "new comment"))

        assertEquals("new comment", result.comment)
        verify(apiService).addComment("gh123", "1", "new comment")
    }
}

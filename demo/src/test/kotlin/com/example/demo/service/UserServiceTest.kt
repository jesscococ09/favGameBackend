package com.example.demo.service

import com.example.demo.model.API
import com.example.demo.model.User
import com.example.demo.repository.APIRepository
import com.example.demo.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class UserServiceTest {

    private val userRepo = mock(UserRepository::class.java)
    private val apiRepo = mock(APIRepository::class.java)
    private val service = UserService(userRepo, apiRepo)

    @Test
    fun `updateDisplayName updates user when found`() {
        val user = User("1", "gh123", "OldName", "icon.png", false)

        `when`(userRepo.findByGithubId("gh123")).thenReturn(user)
        `when`(userRepo.save(any(User::class.java))).thenAnswer { it.arguments[0] }

        val updated = service.updateDisplayName("gh123", "NewName")

        assertEquals("NewName", updated.displayName)
    }

    @Test
    fun `updateDisplayName throws when user not found`() {
        `when`(userRepo.findByGithubId("missing")).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            service.updateDisplayName("missing", "NewName")
        }
    }

    @Test
    fun `updateIcon updates user when found`() {
        val user = User("1", "gh123", "Jessika", "old.png", false)

        `when`(userRepo.findByGithubId("gh123")).thenReturn(user)
        `when`(userRepo.save(any(User::class.java))).thenAnswer { it.arguments[0] }

        val updated = service.updateIcon("gh123", "new.png")

        assertEquals("new.png", updated.iconUrl)
    }

    @Test
    fun `updateIcon throws when user not found`() {
        `when`(userRepo.findByGithubId("missing")).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            service.updateIcon("missing", "newIcon.png")
        }
    }

    @Test
    fun `deleteUserAndData deletes API games and user when found`() {
        val user = User("1", "gh123", "Jessika", "icon.png", false)
        val games = listOf(
            API("g1", "gh123", "Game1", "t1", "d1", "p1", "g1", 5, "c1"),
            API("g2", "gh123", "Game2", "t2", "d2", "p2", "g2", 4, "c2")
        )

        `when`(userRepo.findByGithubId("gh123")).thenReturn(user)
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        service.deleteUserAndData("gh123")

        verify(apiRepo).deleteAll(games)
        verify(userRepo).delete(user)
    }

    @Test
    fun `deleteUserAndData does nothing when user not found`() {
        `when`(userRepo.findByGithubId("missing")).thenReturn(null)

        service.deleteUserAndData("missing")

        verify(apiRepo, never()).deleteAll(anyList())
        verify(userRepo, never()).delete(any())
    }
}

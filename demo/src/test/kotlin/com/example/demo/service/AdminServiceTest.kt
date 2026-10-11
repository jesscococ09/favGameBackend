package com.example.demo.service

import com.example.demo.model.API
import com.example.demo.model.User
import com.example.demo.repository.APIRepository
import com.example.demo.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class AdminServiceTest {

    private val userRepo = mock(UserRepository::class.java)
    private val apiRepo = mock(APIRepository::class.java)
    private val service = AdminService(userRepo, apiRepo)

    @Test
    fun `listUsers returns all users`() {
        val users = listOf(
            User("1", "gh1", "A", "i1", false),
            User("2", "gh2", "B", "i2", true)
        )

        `when`(userRepo.findAll()).thenReturn(users)
        val result = service.listUsers()
        assertEquals(2, result.size)
        verify(userRepo).findAll()
    }

    @Test
    fun `getUser returns user when found`() {
        val user = User("1", "gh1", "Random", "icon.png", false)

        `when`(userRepo.findById("1")).thenReturn(java.util.Optional.of(user))

        val result = service.getUser("1")

        assertEquals("gh1", result.githubId)
        verify(userRepo).findById("1")
    }

    @Test
    fun `getUser throws when not found`() {
        `when`(userRepo.findById("missing")).thenReturn(java.util.Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.getUser("missing")
        }
    }

    @Test
    fun `updateUser updates admin flag`() {
        val user = User("1", "gh1", "Random", "icon.png", false)

        `when`(userRepo.findById("1")).thenReturn(java.util.Optional.of(user))
        `when`(userRepo.save(any(User::class.java))).thenAnswer { it.arguments[0] }

        val result = service.updateUser("1", mapOf("isAdmin" to true))

        assertTrue(result.isAdmin)
        verify(userRepo).save(any(User::class.java))
    }

    @Test
    fun `updateUser updates displayName`() {
        val user = User("1", "gh1", "OldName", "icon.png", false)

        `when`(userRepo.findById("1")).thenReturn(java.util.Optional.of(user))
        `when`(userRepo.save(any(User::class.java))).thenAnswer { it.arguments[0] }

        val result = service.updateUser("1", mapOf("displayName" to "NewName"))

        assertEquals("NewName", result.displayName)
    }

    @Test
    fun `updateUser updates iconUrl`() {
        val user = User("1", "gh1", "Random", "old.png", false)

        `when`(userRepo.findById("1")).thenReturn(java.util.Optional.of(user))
        `when`(userRepo.save(any(User::class.java))).thenAnswer { it.arguments[0] }

        val result = service.updateUser("1", mapOf("iconUrl" to "new.png"))

        assertEquals("new.png", result.iconUrl)
    }

    @Test
    fun `updateUser throws when user not found`() {
        `when`(userRepo.findById("missing")).thenReturn(java.util.Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.updateUser("missing", mapOf("isAdmin" to true))
        }
    }

    @Test
    fun `deleteUserAndData deletes user and their games`() {
        val user = User("1", "gh1", "Random", "icon.png", false)
        val games = listOf(
            API("g1", "gh1", "Game1", "t1", "d1", "p1", "g1", 5, "c1"),
            API("g2", "gh1", "Game2", "t2", "d2", "p2", "g2", 4, "c2")
        )

        `when`(userRepo.findById("1")).thenReturn(java.util.Optional.of(user))
        `when`(apiRepo.findByGithubId("gh1")).thenReturn(games)

        service.deleteUserAndData("1")

        verify(apiRepo).deleteAll(games)
        verify(userRepo).delete(user)
    }

    @Test
    fun `deleteUserAndData throws when user not found`() {
        `when`(userRepo.findById("missing")).thenReturn(java.util.Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.deleteUserAndData("missing")
        }
    }
}

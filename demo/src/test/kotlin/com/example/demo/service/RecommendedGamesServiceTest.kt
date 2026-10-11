package com.example.demo.service

import com.example.demo.model.RecommendedGames
import com.example.demo.repository.RecommendedGamesRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class RecommendedGamesServiceTest {

    private val repo = mock(RecommendedGamesRepository::class.java)
    private val service = RecommendedGamesService(repo)

    @Test
    fun `listAll returns all recommendations`() {
        val recs = listOf(
            RecommendedGames("1", "Game1", "t1", "d1", "p1", "RPG", 5, "c1"),
            RecommendedGames("2", "Game2", "t2", "d2", "p2", "Action", 7, "c2")
        )
        `when`(repo.findAll()).thenReturn(recs)

        val result = service.listAll()

        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun `createRecommendation full fields`() {
        val body = mapOf(
            "gameName" to "New Game",
            "thumbnail" to "t.png",
            "gameDescription" to "desc",
            "platform" to "PC",
            "genre" to "Adventure",
            "rating" to 9,
            "comment" to "Great!"
        )

        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val result = service.createRecommendation("admin123", body)

        assertEquals("New Game", result.gameName)
        assertEquals("Adventure", result.genre)
        assertEquals(9, result.rating)
    }

    @Test
    fun `createRecommendation defaults`() {
        val body = emptyMap<String, Any>()

        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val result = service.createRecommendation("admin123", body)

        assertEquals("", result.gameName)
        assertEquals("", result.thumbnail)
        assertEquals(0, result.rating)
    }

    @Test
    fun `updateRecommendation partial update`() {
        val existing = RecommendedGames("1", "Old", "old.png", "oldDesc", "OldP", "OldG", 3, "oldC")

        `when`(repo.findById("1")).thenReturn(Optional.of(existing))
        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val body = mapOf("gameName" to "Updated", "rating" to 10)

        val updated = service.updateRecommendation("admin123", "1", body)

        assertEquals("Updated", updated.gameName)
        assertEquals(10, updated.rating)
        assertEquals("old.png", updated.thumbnail)
    }

    @Test
    fun `updateRecommendation empty body`() {
        val existing = RecommendedGames("1", "Old", "old.png", "oldDesc", "OldP", "OldG", 3, "oldC")

        `when`(repo.findById("1")).thenReturn(Optional.of(existing))
        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val updated = service.updateRecommendation("admin123", "1", emptyMap())

        assertEquals(existing, updated)
    }

    @Test
    fun `replaceRecommendation full replace`() {
        val existing = RecommendedGames("1", "Old", "old.png", "oldDesc", "OldP", "OldG", 3, "oldC")

        `when`(repo.findById("1")).thenReturn(Optional.of(existing))
        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val body = mapOf(
            "gameName" to "NewName",
            "thumbnail" to "new.png",
            "gameDescription" to "newDesc",
            "platform" to "PC",
            "genre" to "Adventure",
            "rating" to 10,
            "comment" to "Amazing!"
        )

        val replaced = service.replaceRecommendation("admin123", "1", body)

        assertEquals("NewName", replaced.gameName)
        assertEquals("new.png", replaced.thumbnail)
        assertEquals("newDesc", replaced.gameDescription)
        assertEquals("PC", replaced.platform)
        assertEquals("Adventure", replaced.genre)
        assertEquals(10, replaced.rating)
        assertEquals("Amazing!", replaced.comment)
    }

    @Test
    fun `replaceRecommendation defaults`() {
        val existing = RecommendedGames("1", "Old", "old.png", "oldDesc", "OldP", "OldG", 3, "oldC")

        `when`(repo.findById("1")).thenReturn(Optional.of(existing))
        `when`(repo.save(any(RecommendedGames::class.java))).thenAnswer { it.arguments[0] }

        val replaced = service.replaceRecommendation("admin123", "1", emptyMap())

        assertEquals("", replaced.gameName)
        assertEquals("", replaced.thumbnail)
        assertEquals("", replaced.gameDescription)
        assertEquals("", replaced.platform)
        assertEquals("", replaced.genre)
        assertEquals(0, replaced.rating)
        assertEquals("", replaced.comment)
    }

    @Test
    fun `deleteRecommendation deletes when found`() {
        val existing = RecommendedGames("1", "Game", "t", "d", "p", "g", 5, "c")

        `when`(repo.findById("1")).thenReturn(Optional.of(existing))

        service.deleteRecommendation("admin123", "1")

        verify(repo).delete(existing)
    }

    @Test
    fun `deleteRecommendation throws when not found`() {
        `when`(repo.findById("1")).thenReturn(Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.deleteRecommendation("admin123", "1")
        }
    }
}

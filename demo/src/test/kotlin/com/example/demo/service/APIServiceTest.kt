package com.example.demo.service

import com.example.demo.model.API
import com.example.demo.repository.APIRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class APIServiceTest {

    private val apiRepo = mock(APIRepository::class.java)
    private val service = APIService(apiRepo)

    @Test
    fun listMyGames_noSort_noGenre() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Game2", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, null, null)
        assertEquals(2, result.size)
    }

    @Test
    fun listMyGames_genreFilter() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Game2", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, null, "RPG")
        assertEquals(1, result.size)
    }

    @Test
    fun listMyGames_sortAlphabetical() {
        val games = listOf(
            API("1", "gh123", "B Game", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "A Game", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, "alphabetical", null)
        assertEquals("A Game", result[0].gameName)
    }

    @Test
    fun listMyGames_sortRating() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Game2", "t2", "desc2", "p2", "Action", 9, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, "rating", null)
        assertEquals(9, result[0].rating)
    }

    @Test
    fun listMyGames_sortGenre() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Game2", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, "genre", null)
        assertEquals("Action", result[0].genre)
    }

    @Test
    fun listMyGames_unknownSort() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Game2", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 0, 20, "unknown", null)
        assertEquals(games, result)
    }

    @Test
    fun listMyGames_paginationOutOfRange() {
        val games = listOf(
            API("1", "gh123", "Game1", "t1", "desc1", "p1", "RPG", 5, "c1")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.listMyGames("gh123", 10, 20, null, null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchMyGames_match() {
        val games = listOf(
            API("1", "gh123", "Zelda Adventure", "t1", "desc1", "p1", "RPG", 5, "c1"),
            API("2", "gh123", "Mario Fun", "t2", "desc2", "p2", "Action", 7, "c2")
        )
        `when`(apiRepo.findByGithubId("gh123")).thenReturn(games)

        val result = service.searchMyGames("gh123", "zelda")
        assertEquals(1, result.size)
    }

    @Test
    fun getMyGame_owned() {
        val game = API("1", "gh123", "Game1", "t", "d", "p", "g", 5, "c")
        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))

        val result = service.getMyGame("gh123", "1")
        assertEquals("1", result.id)
    }

    @Test
    fun getMyGame_notOwned() {
        val game = API("1", "other", "Game1", "t", "d", "p", "g", 5, "c")
        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))

        assertThrows(IllegalArgumentException::class.java) {
            service.getMyGame("gh123", "1")
        }
    }

    @Test
    fun addGame_fullFields() {
        val body = mapOf(
            "gameName" to "My Game",
            "thumbnail" to "t.png",
            "gameDescription" to "desc",
            "platform" to "Switch",
            "genre" to "RPG",
            "rating" to 8,
            "comment" to "Nice"
        )

        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val result = service.addGame("gh123", body)

        assertEquals("RPG", result.genre)
        assertEquals("My Game", result.gameName)
    }

    @Test
    fun addGame_defaults() {
        val body = emptyMap<String, Any>()

        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val result = service.addGame("gh123", body)

        assertEquals("", result.thumbnail)
        assertEquals("", result.gameName)
        assertEquals(0, result.rating)
    }

    @Test
    fun replaceGame_fullReplace() {
        val original = API("1", "gh123", "OldName", "old.png", "oldDesc", "OldP", "OldG", 3, "oldC")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(original))
        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val body = mapOf(
            "gameName" to "NewName",
            "thumbnail" to "new.png",
            "gameDescription" to "newDesc",
            "platform" to "PC",
            "genre" to "Adventure",
            "rating" to 10,
            "comment" to "Amazing!"
        )

        val updated = service.replaceGame("gh123", "1", body)

        assertEquals("NewName", updated.gameName)
        assertEquals("new.png", updated.thumbnail)
        assertEquals("newDesc", updated.gameDescription)
        assertEquals("PC", updated.platform)
        assertEquals("Adventure", updated.genre)
        assertEquals(10, updated.rating)
        assertEquals("Amazing!", updated.comment)
    }

    @Test
    fun updateGame_fullUpdate() {
        val game = API("1", "gh123", "Game1", "t", "d", "p", "g", 5, "old")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))
        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val body = mapOf("rating" to 9, "comment" to "updated")

        val updated = service.updateGame("gh123", "1", body)

        assertEquals(9, updated.rating)
        assertEquals("updated", updated.comment)
    }

    @Test
    fun updateGame_emptyBody() {
        val game = API("1", "gh123", "Game1", "t", "d", "p", "g", 5, "old")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))
        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val updated = service.updateGame("gh123", "1", emptyMap())

        assertEquals(5, updated.rating)
        assertEquals("old", updated.comment)
    }

    @Test
    fun deleteGame_owned() {
        val game = API("1", "gh123", "Game1", "t", "d", "p", "g", 5, "c")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))

        service.deleteGame("gh123", "1")

        verify(apiRepo).delete(game)
    }

    @Test
    fun deleteGame_notOwned() {
        val game = API("1", "other", "Game1", "t", "d", "p", "g", 5, "c")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))

        assertThrows(IllegalArgumentException::class.java) {
            service.deleteGame("gh123", "1")
        }
    }

    @Test
    fun addComment_update() {
        val game = API("1", "gh123", "Game1", "t", "d", "p", "g", 5, "old")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))
        `when`(apiRepo.save(any(API::class.java))).thenAnswer { it.arguments[0] }

        val updated = service.addComment("gh123", "1", "new")

        assertEquals("new", updated.comment)
    }

    @Test
    fun addComment_notOwned() {
        val game = API("1", "other", "Game1", "t", "d", "p", "g", 5, "old")

        `when`(apiRepo.findById("1")).thenReturn(Optional.of(game))

        assertThrows(IllegalArgumentException::class.java) {
            service.addComment("gh123", "1", "new")
        }
    }
}

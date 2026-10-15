package com.example.demo.controller

import com.example.demo.model.API
import com.example.demo.model.User
import com.example.demo.service.APIService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/favGame/v1/users/games")
class APIController(
    private val apiService: APIService
) {

    private fun currentUser(auth: Authentication): User {
        return auth.principal as User
    }

    @GetMapping
    fun listMy(
        auth: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) genre: String?
    ): List<API> {

        val user = currentUser(auth)

        return apiService.listMyGames(
            user.githubId,
            page,
            size,
            sort,
            genre
        )
    }

    @GetMapping("/search")
    fun searchMyGames(
        auth: Authentication,
        @RequestParam query: String
    ): List<API> {

        val user = currentUser(auth)

        return apiService.searchMyGames(
            user.githubId,
            query
        )
    }

    @GetMapping("/{id}")
    fun getMyGame(
        auth: Authentication,
        @PathVariable id: String
    ): API {

        val user = currentUser(auth)

        return apiService.getMyGame(
            user.githubId,
            id
        )
    }

    @PostMapping
    fun addGame(
        auth: Authentication,
        @RequestBody body: Map<String, Any>
    ): API {

        val user = currentUser(auth)

        return apiService.addGame(
            user.githubId,
            body
        )
    }

    @PutMapping("/{id}")
    fun replaceGame(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody body: Map<String, Any>
    ): API {

        val user = currentUser(auth)

        return apiService.replaceGame(
            user.githubId,
            id,
            body
        )
    }

    @PatchMapping("/{id}")
    fun updateGame(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody body: Map<String, Any>
    ): API {

        val user = currentUser(auth)

        return apiService.updateGame(
            user.githubId,
            id,
            body
        )
    }

    @DeleteMapping("/{id}")
    fun deleteGame(
        auth: Authentication,
        @PathVariable id: String
    ): Map<String, String> {

        val user = currentUser(auth)

        apiService.deleteGame(
            user.githubId,
            id
        )

        return mapOf(
            "status" to "deleted"
        )
    }

    @PostMapping("/{id}/comments")
    fun addComment(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody body: Map<String, String>
    ): API {

        val user = currentUser(auth)

        val comment =
            body["comment"]
                ?: throw IllegalArgumentException("requires comment")
        return apiService.addComment(
            user.githubId,
            id,
            comment
        )
    }
}
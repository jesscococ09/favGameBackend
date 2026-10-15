package com.example.demo.controller

import com.example.demo.model.RecommendedGames
import com.example.demo.model.User
import com.example.demo.service.RecommendedGamesService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favGame/v1/recommendations")
class RecommendedGamesController(
    private val recommendedGamesService: RecommendedGamesService
) {
    private fun currentUser(auth: Authentication): User {
        return auth.principal as User
    }
    // Any users
    @GetMapping
    fun listRecommendations(): List<RecommendedGames> {
        return recommendedGamesService.listAll()
    }
    // Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createRecommendation(
        auth: Authentication,
        @RequestBody body: Map<String, Any>
    ): RecommendedGames {
        val user = currentUser(auth)
        return recommendedGamesService.createRecommendation(
            user.githubId,
            body
        )
    }

    // Admin only
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRecommendation(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody body: Map<String, Any>
    ): RecommendedGames {
        val user = currentUser(auth)
        return recommendedGamesService.updateRecommendation(
            user.githubId,
            id,
            body
        )
    }
    // Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun replaceRecommendation(
        auth: Authentication,
        @PathVariable id: String,
        @RequestBody body: Map<String, Any>
    ): RecommendedGames {
        val user = currentUser(auth)
        return recommendedGamesService.replaceRecommendation(
            user.githubId,
            id,
            body
        )
    }

    // Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRecommendation(
        auth: Authentication,
        @PathVariable id: String
    ): Map<String, String> {
        val user = currentUser(auth)
        recommendedGamesService.deleteRecommendation(
            user.githubId,
            id
        )
        return mapOf("status" to "deleted")
    }
}
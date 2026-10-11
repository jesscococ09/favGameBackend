package com.example.demo.controller

import com.example.demo.model.RecommendedGames
import com.example.demo.security.GithubUserPrincipal
import com.example.demo.service.RecommendedGamesService
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
class RecommendedGamesController(private val recommendedGamesService: RecommendedGamesService) {
    @GetMapping
    fun listRecommendations(): List<RecommendedGames>{
        return recommendedGamesService.listAll()
    }
    @PostMapping
    fun createRecommendation(
        auth: Authentication,
        @RequestBody
        body: Map<String, Any>
    ): RecommendedGames{
        val principal= auth.principal as GithubUserPrincipal
        return recommendedGamesService.createRecommendation(principal.user.githubId,body)
    }
    @PatchMapping("/{id}")
    fun updateRecommendation(
        auth: Authentication,
        @PathVariable
        id: String,
        @RequestBody
        body: Map<String, Any>
    ): RecommendedGames{
        val principal= auth.principal as GithubUserPrincipal
        return recommendedGamesService.updateRecommendation(principal.user.githubId,id,body)
    }
    @PutMapping("/{id}")
    fun replaceRecommendation(
        auth: Authentication,
        @PathVariable
        id: String,
        @RequestBody
        body: Map<String, Any>
    ): RecommendedGames{
        val principal= auth.principal as GithubUserPrincipal
        return recommendedGamesService.replaceRecommendation(principal.user.githubId,id,body)
    }
    @DeleteMapping("/{id}")
    fun deleteRecommendation(
        auth: Authentication,
        @PathVariable
        id: String
    ):Map<String, String>{
        val principal= auth.principal as GithubUserPrincipal
        recommendedGamesService.deleteRecommendation(principal.user.githubId,id)
        return mapOf("status" to "deleted")
    }
}
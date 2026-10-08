package com.example.demo.controller

import com.example.demo.model.API
import com.example.demo.security.GithubUserPrincipal
import com.example.demo.service.APIService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favGame/v1/users/games")
class APIController(private val apiService: APIService) {
    @GetMapping
    fun listMy(
        auth: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) genre: String?
    ): List<API>{
        val principal= auth.principal as GithubUserPrincipal
        return apiService.listMyGames(principal.user.githubId,page,size,sort,genre)
    }
    @GetMapping("/search")
    fun searchMyGames(
        auth: Authentication,
        @RequestParam
        query: String
    ): List<API>{
        val principal=auth.principal as GithubUserPrincipal
        return apiService.searchMyGames(principal.user.githubId,query)
    }
    @GetMapping("/{id}")
    fun getMyGame(
        auth: Authentication,
        @PathVariable
        id: String
    ): API{
        val principal=auth.principal as GithubUserPrincipal
        return apiService.getMyGame(principal.user.githubId,id)
    }
    @PostMapping
    fun addGame(
        auth: Authentication,
        @RequestBody
        body: Map<String, Any>
    ): API{
        val principal=auth.principal as GithubUserPrincipal
        return apiService.addGame(principal.user.githubId,body)
    }
    @PatchMapping("/{id}")
    fun updateGame(
        auth: Authentication,
        @PathVariable
        id: String,
        @RequestBody
        body: Map<String, Any>
    ): API{
        val principal=auth.principal as GithubUserPrincipal
        return apiService.updateGame(principal.user.githubId,id,body)
    }
    @DeleteMapping("/{id}")
    fun deleteGame(
        auth: Authentication,
        @PathVariable
        id: String
    ): Map<String, String>{
        val principal=auth.principal as GithubUserPrincipal
        apiService.deleteGame(principal.user.githubId,id)
        return mapOf("status" to "deleted")
    }
    @PostMapping("/{id}/comments")
    fun addComment(
        auth: Authentication,
        @PathVariable
        id: String,
        @RequestBody
        body: Map<String, String>
    ): API{
        val principal=auth.principal as GithubUserPrincipal
        val comment=body["comment"]?:throw IllegalArgumentException("requires comment")
        return apiService.addComment(principal.user.githubId,id,comment)
    }
}
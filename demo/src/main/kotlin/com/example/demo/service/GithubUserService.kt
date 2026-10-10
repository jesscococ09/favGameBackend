package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class GithubUserService(private val userRepository: UserRepository) {
    private val restTemplate = RestTemplate()
    fun loadOrCreateUser(accessToken: String): User {
        val request= RequestEntity.get(URI("https://api.github.com/user"))
            .header(HttpHeaders.AUTHORIZATION,"Bearer $accessToken")
            .build()
        val response= restTemplate.exchange(request,Map::class.java)
        if(!response.statusCode.is2xxSuccessful || response.body==null){
            throw RuntimeException("Failed to fetch user from GitHub")
        }
        val body= response.body!!
        val githubId = body["id"].toString()
        val login = body["login"] as? String ?: githubId
        val name = body["name"] as? String ?: login
        val avatar = body["avatar_url"] as? String
        val existing= userRepository.findByGithubId(githubId)
        if(existing !=null){
            return existing
        }
        return userRepository.save(User(
            githubId = githubId,
            displayName = name,
            iconUrl = avatar,
            isAdmin = false
        ))
    }
}
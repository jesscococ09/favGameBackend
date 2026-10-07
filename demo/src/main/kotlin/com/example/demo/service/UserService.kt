package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.APIRepository
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val apiRepository: APIRepository
) {
    fun updateDisplayName(githubId: String,newName: String): User {
        val user= userRepository.findByGithubId(githubId)?: throw IllegalArgumentException("User not found")
        val updated= user.copy(displayName = newName)
        return userRepository.save(updated)
    }
    fun updateIcon(githubId: String,newIcon: String): User {
        val user= userRepository.findByGithubId(githubId)?: throw IllegalArgumentException("User not found")
        val updated= user.copy(iconUrl = newIcon)
        return userRepository.save(updated)
    }
    fun deleteUserAndData(githubId: String){
        val user= userRepository.findByGithubId(githubId)?: return
        val games= apiRepository.findByGithubId(githubId)
        apiRepository.deleteAll(games)
        userRepository.delete(user)
    }
}
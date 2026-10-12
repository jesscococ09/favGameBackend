package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.APIRepository
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val apiRepository: APIRepository
) {
    fun listUsers(): List<User>{
        return userRepository.findAll()
    }
    fun getUser(userId:String): User{
        return userRepository.findById(userId).orElseThrow()
    }
    fun updateUser(userId: String, body: Map<String, Any>): User {
        val user = userRepository.findById(userId).orElseThrow()
        val updated = user.copy(
            isAdmin = body["isAdmin"] as? Boolean ?: user.isAdmin
        )

        return userRepository.save(updated)
    }
    fun deleteUserAndData(userId: String){
        val user= userRepository.findById(userId).orElseThrow()
        val game=apiRepository.findByGithubId(user.githubId)
        apiRepository.deleteAll(game)
        userRepository.delete(user)
    }
}
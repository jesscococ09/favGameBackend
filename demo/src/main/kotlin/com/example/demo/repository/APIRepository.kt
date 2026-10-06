package com.example.demo.repository

import com.example.demo.model.API
import org.springframework.data.jpa.repository.JpaRepository

interface APIRepository: JpaRepository<API, String> {
    fun findByGithubId(githubId: String): List<API>
}
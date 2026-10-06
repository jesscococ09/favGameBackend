package com.example.demo.repository

import com.example.demo.model.RecommendedGames
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendedGamesRepository: JpaRepository<RecommendedGames, String> {
}
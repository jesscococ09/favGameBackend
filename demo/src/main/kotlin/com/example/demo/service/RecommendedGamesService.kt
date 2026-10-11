package com.example.demo.service

import com.example.demo.model.RecommendedGames
import com.example.demo.repository.RecommendedGamesRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecommendedGamesService(private val recommendedGamesRepository: RecommendedGamesRepository) {
    fun listAll(): List<RecommendedGames>{
        return recommendedGamesRepository.findAll()
    }
    fun createRecommendation(adminGithubId:String,body: Map<String, Any>): RecommendedGames{
        val id= UUID.randomUUID().toString()
        val rec= RecommendedGames(
            id=id,
            gameName = body["gameName"] as? String ?:"",
            thumbnail = body["thumbnail"] as? String ?:"",
            gameDescription = body["gameDescription"] as? String ?:"",
            platform = body["platform"] as? String ?:"",
            genre= body["genre"] as? String ?:"",
            rating=(body["rating"] as? Number)?.toInt() ?: 0,
            comment = body["comment"] as? String ?:""
        )
        return recommendedGamesRepository.save(rec)
    }
    fun updateRecommendation(adminGithubId: String,id: String,body: Map<String, Any>): RecommendedGames{
        val existing=recommendedGamesRepository.findById(id).orElseThrow()
        val updated= existing.copy(
            gameName = body["gameName"] as? String ?:existing.gameName,
            thumbnail = body["thumbnail"] as? String ?:existing.thumbnail,
            gameDescription = body["gameDescription"] as? String ?:existing.gameDescription,
            platform = body["platform"] as? String ?:existing.platform,
            genre= body["genre"] as? String ?:existing.genre,
            rating=(body["rating"] as? Number)?.toInt() ?: existing.rating,
            comment = body["comment"] as? String ?:existing.comment
        )
        return recommendedGamesRepository.save(updated)
    }
    fun replaceRecommendation(adminGithubId: String,id: String,body: Map<String, Any>): RecommendedGames{
        val existing=recommendedGamesRepository.findById(id).orElseThrow()
        val replaced= existing.copy(
            gameName = body["gameName"] as? String ?:"",
            thumbnail = body["thumbnail"] as? String ?:"",
            gameDescription = body["gameDescription"] as? String ?:"",
            platform = body["platform"] as? String ?:"",
            genre= body["genre"] as? String ?:"",
            rating=(body["rating"] as? Number)?.toInt() ?: 0,
            comment = body["comment"] as? String ?:""
        )
        return recommendedGamesRepository.save(replaced)
    }
    fun deleteRecommendation(adminGithubId: String,id: String){
        val existing=recommendedGamesRepository.findById(id).orElseThrow()
        recommendedGamesRepository.delete(existing)
    }
}
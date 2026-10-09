package com.example.demo.service

import com.example.demo.model.API
import com.example.demo.repository.APIRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class APIService(private val apiRepository: APIRepository) {
    fun listMyGames(
        githubId: String,
        page: Int,
        size: Int,
        sort: String?,
        genre: String?
    ): List<API>{
        var games= apiRepository.findByGithubId(githubId)
        if(genre!= null){
            games= games.filter { it.genre.equals(genre,true) }
        }
        if(sort!= null){
            games= when(sort){
                "alphabetical"->games.sortedBy { it.gameName }
                "rating"-> games.sortedByDescending { it.rating }
                "genre"->games.sortedBy { it.genre }
                else -> games
            }
        }
        val start= page * size
        val end= minOf(start+size,games.size)
        return if(start<games.size) games.subList(start,end) else emptyList()
    }
    fun searchMyGames(githubId: String,query: String): List<API>{
        return apiRepository.findByGithubId(githubId).filter {
            it.gameName.contains(query, ignoreCase = true)
        }
    }
    fun getMyGame(githubId: String,id: String): API{
        val game = apiRepository.findById(id).orElseThrow()
        if (game.githubId != githubId) throw IllegalArgumentException("Game not found")
        return game
    }
    fun addGame(githubId: String,body: Map<String, Any>): API{
        val id= UUID.randomUUID().toString()
        val gameName = body["gameName"] as? String ?: ""
        val thumbnail= body["thumbnail"] as? String ?: ""
        val gameDescription= body["gameDescription"] as? String ?: ""
        val platform= body["platform"] as? String ?: ""
        val genre= body["genre"] as? String ?: ""
        val rating= (body["rating"] as? Number)?.toInt() ?: 0
        val comment= body["comment"] as? String ?: ""
        val game= API(
            id= id,
            githubId= githubId,
            gameName = gameName,
            thumbnail= thumbnail,
            gameDescription= gameDescription,
            platform=platform,
            genre=genre,
            rating=rating,
            comment=comment
        )
        return apiRepository.save(game)
    }
    fun updateGame(githubId: String, id: String, body: Map<String, Any>):API{
        val game= getMyGame(githubId,id)
        val updated=game.copy(
            rating = (body["rating"] as? Number)?.toInt() ?: game.rating,
            comment = body["comment"] as? String ?: game.comment
        )
        return apiRepository.save(updated)
    }
    fun deleteGame(githubId: String,id: String){
        val game= getMyGame(githubId,id)
        apiRepository.delete(game)
    }
    fun addComment(githubId: String,id: String,comment: String): API{
        val game= getMyGame(githubId,id)
        val updated= game.copy(comment = comment)
        return apiRepository.save(updated)
    }
}
package com.example.demo.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/* Er diagram as Ref.
recommendedGames {
        String id PK
        string thumbnail
        string gameName
        string gameDescription
        string platform
        string genre
        int rating
        string comment
        if we want to track admin{
        string adminGithubId
        string adminDisplayName
        string adminIconUrl
        }
    }
 */

@Entity
@Table(name= "recommendedGames")
data class RecommendedGames(
    @Id
    val id: String,
    /*
    if we see which admin added which game
    val adminGithubId: String?= null,
    val adminDisplayName: String?= null,
    val adminIconUrl: String?= null,
     */
    val thumbnail:String,
    val gameDescription: String,
    val platform: String,
    val genre: String,
    val rating: Int,
    val comment: String
) {

}
package com.example.demo.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column

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
@Table(name="recommendedGames")
data class RecommendedGames(
    @Id
    val id: String,

    @Column(name = "gamename")
    val gameName: String,

    @Column(name = "thumbnail")
    val thumbnail: String,

    @Column(name = "gamedescription")
    val gameDescription: String,

    @Column(name = "platform")
    val platform: String,

    @Column(name = "genre")
    val genre: String,

    @Column(name = "rating")
    val rating: Int,

    @Column(name = "comment")
    val comment: String
)
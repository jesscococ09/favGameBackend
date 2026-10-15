package com.example.demo.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
/* Table as ref
API {
    String id PK
    string githubId
    string gameName
    string thumbnail
    string gameDescription
    string platform
    string genre
    int rating
    string comment
}
 */

@Entity
@Table(name="API")
data class API(
    @Id
    val id: String,

    @Column(name = "githubid")
    val githubId: String,

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
package com.example.demo.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
/* Table as ref
API {
    String id PK
    string githubId
    string thumbnail
    string gameName
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
    val githubId: String,
    val thumbnail:String,
    val gameDescription: String,
    val platform: String,
    val genre: String,
    val rating: Int,
    val comment: String
) {
}
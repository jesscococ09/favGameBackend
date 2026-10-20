package com.example.demo.integration

import com.example.demo.model.User
import com.example.demo.repository.RecommendedGamesRepository
import com.example.demo.repository.UserRepository
import com.example.demo.service.GithubUserService
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.jpa.hibernate.ddl-auto=create"])
class RecommendedGamesIntegrationTest {
    @LocalServerPort private var port: Int = 0
    @Autowired private lateinit var recommendedGamesRepository: RecommendedGamesRepository
    @Autowired private lateinit var userRepository: UserRepository
    @MockitoBean private lateinit var githubUserService: GithubUserService
    private lateinit var testUser: User
    private lateinit var adminUser: User

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        recommendedGamesRepository.deleteAll()
        userRepository.deleteAll()
        testUser = userRepository.save(User(githubId = "111111111", displayName = "Integration Test User", iconUrl = null, isAdmin = false))
        adminUser = userRepository.save(User(githubId = "222222222", displayName = "Integration Test Admin", iconUrl = null, isAdmin = true))
        `when`(githubUserService.loadOrCreateUser("test-user-token")).thenReturn(testUser)
        `when`(githubUserService.loadOrCreateUser("test-admin-token")).thenReturn(adminUser)
    }

    @Test
    fun createRecommendation_admin_returnsSavedRecommendation() {
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body(recommendationBody()).`when`().post("/favGame/v1/recommendations").then().statusCode(200).body("gameName", equalTo("Elden Ring")).body("platform", equalTo("PC")).body("genre", equalTo("RPG")).body("rating", equalTo(10)).body("comment", equalTo("Recommended game"))
    }

    @Test
    fun createRecommendation_regularUser_returns403() {
        RestAssured.given().header("Authorization", "Bearer test-user-token").contentType(ContentType.JSON).body(recommendationBody()).`when`().post("/favGame/v1/recommendations").then().statusCode(403)
    }

    @Test
    fun listRecommendations_authenticatedUser_returns200() {
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body(recommendationBody()).`when`().post("/favGame/v1/recommendations").then().statusCode(200)
        RestAssured.given().header("Authorization", "Bearer test-user-token").accept(ContentType.JSON).`when`().get("/favGame/v1/recommendations").then().statusCode(200).body("size()", equalTo(1)).body("[0].gameName", equalTo("Elden Ring"))
    }

    @Test
    fun listRecommendations_withoutAuthentication_returns401() {
        RestAssured.given().accept(ContentType.JSON).`when`().get("/favGame/v1/recommendations").then().statusCode(401)
    }

    @Test
    fun updateRecommendation_resourceDoesNotExist_returns404() {
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body(mapOf("gameName" to "Missing Game")).`when`().patch("/favGame/v1/recommendations/does-not-exist").then().statusCode(404)
    }

    @Test
    fun createRecommendation_invalidRequestBody_returns400() {
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body("{").`when`().post("/favGame/v1/recommendations").then().statusCode(400)
    }

    private fun recommendationBody(): Map<String, Any> = mapOf("gameName" to "Elden Ring", "thumbnail" to "elden-ring.png", "gameDescription" to "Action RPG", "platform" to "PC", "genre" to "RPG", "rating" to 10, "comment" to "Recommended game")

    companion object {
        @Container @JvmField val postgres = PostgreSQLContainer("postgres:17-alpine").withDatabaseName("favgame_test").withUsername("favgame").withPassword("favgame")
        @DynamicPropertySource @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }
}

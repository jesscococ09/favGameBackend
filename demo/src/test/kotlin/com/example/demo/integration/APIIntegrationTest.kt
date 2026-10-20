package com.example.demo.integration

import com.example.demo.model.User
import com.example.demo.repository.APIRepository
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
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.jpa.hibernate.ddl-auto=create"
    ]
)
class APIIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var apiRepository: APIRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var githubUserService: GithubUserService

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port

        apiRepository.deleteAll()
        userRepository.deleteAll()

        testUser = userRepository.save(
            User(
                githubId = "123456789",
                displayName = "Integration Test User",
                iconUrl = null,
                isAdmin = false
            )
        )

        `when`(
            githubUserService.loadOrCreateUser(
                "test-user-token"
            )
        ).thenReturn(testUser)
    }

    @Test
    fun addGame_authenticatedUser_returnsSavedGame() {
        val body = mapOf(
            "gameName" to "The Legend of Zelda",
            "thumbnail" to "zelda.png",
            "gameDescription" to "Adventure game",
            "platform" to "Nintendo Switch",
            "genre" to "Adventure",
            "rating" to 9,
            "comment" to "Great game"
        )

        RestAssured
            .given()
            .header(
                "Authorization",
                "Bearer test-user-token"
            )
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/favGame/v1/users/games")
            .then()
            .statusCode(200)
            .body("githubId", equalTo("123456789"))
            .body("gameName", equalTo("The Legend of Zelda"))
            .body("thumbnail", equalTo("zelda.png"))
            .body("gameDescription", equalTo("Adventure game"))
            .body("platform", equalTo("Nintendo Switch"))
            .body("genre", equalTo("Adventure"))
            .body("rating", equalTo(9))
            .body("comment", equalTo("Great game"))
    }

    @Test
    fun listGames_authenticatedUser_returnsSavedGames() {
        val body = mapOf(
            "gameName" to "The Legend of Zelda",
            "thumbnail" to "zelda.png",
            "gameDescription" to "Adventure game",
            "platform" to "Nintendo Switch",
            "genre" to "Adventure",
            "rating" to 9,
            "comment" to "Great game"
        )

        RestAssured
            .given()
            .header(
                "Authorization",
                "Bearer test-user-token"
            )
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/favGame/v1/users/games")
            .then()
            .statusCode(200)

        RestAssured
            .given()
            .header(
                "Authorization",
                "Bearer test-user-token"
            )
            .accept(ContentType.JSON)
            .`when`()
            .get("/favGame/v1/users/games")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].githubId", equalTo("123456789"))
            .body("[0].gameName", equalTo("The Legend of Zelda"))
    }

    @Test
    fun listGames_withoutAuthentication_returns401() {
        RestAssured
            .given()
            .accept(ContentType.JSON)
            .`when`()
            .get("/favGame/v1/users/games")
            .then()
            .statusCode(401)
    }

    @Test
    fun getGame_resourceDoesNotExist_returns404() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .accept(ContentType.JSON)
            .`when`()
            .get("/favGame/v1/users/games/does-not-exist")
            .then()
            .statusCode(404)
    }

    @Test
    fun addComment_invalidRequestBody_returns400() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .contentType(ContentType.JSON)
            .body(emptyMap<String, String>())
            .`when`()
            .post("/favGame/v1/users/games/does-not-exist/comments")
            .then()
            .statusCode(400)
    }

    companion object {

        @Container
        @JvmField
        val postgres = PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("favgame_test")
            .withUsername("favgame")
            .withPassword("favgame")

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(
            registry: DynamicPropertyRegistry
        ) {
            registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
            )
            registry.add(
                "spring.datasource.username",
                postgres::getUsername
            )
            registry.add(
                "spring.datasource.password",
                postgres::getPassword
            )
            registry.add(
                "spring.datasource.driver-class-name"
            ) {
                "org.postgresql.Driver"
            }
        }
    }
}

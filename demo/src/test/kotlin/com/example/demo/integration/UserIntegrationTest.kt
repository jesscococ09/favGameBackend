package com.example.demo.integration

import com.example.demo.model.User
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
    properties = ["spring.jpa.hibernate.ddl-auto=create"]
)
class UserIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var githubUserService: GithubUserService

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port

        userRepository.deleteAll()

        testUser = userRepository.save(
            User(
                githubId = "111111111",
                displayName = "Integration Test User",
                iconUrl = null,
                isAdmin = false
            )
        )

        `when`(githubUserService.loadOrCreateUser("test-user-token"))
            .thenReturn(testUser)
    }

    @Test
    fun getProfile_authenticatedUser_returns200() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .accept(ContentType.JSON)
            .`when`()
            .get("/favGame/v1/users")
            .then()
            .statusCode(200)
            .body("githubId", equalTo("111111111"))
            .body("displayName", equalTo("Integration Test User"))
            .body("isAdmin", equalTo(false))
    }

    @Test
    fun getProfile_withoutAuthentication_returns401() {
        RestAssured
            .given()
            .accept(ContentType.JSON)
            .`when`()
            .get("/favGame/v1/users")
            .then()
            .statusCode(401)
    }

    @Test
    fun updateDisplayName_authenticatedUser_returnsUpdatedUser() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .contentType(ContentType.JSON)
            .body(mapOf("displayName" to "Updated Test User"))
            .`when`()
            .patch("/favGame/v1/users/name")
            .then()
            .statusCode(200)
            .body("githubId", equalTo("111111111"))
            .body("displayName", equalTo("Updated Test User"))
    }

    @Test
    fun updateIcon_authenticatedUser_returnsUpdatedUser() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .contentType(ContentType.JSON)
            .body(mapOf("iconUrl" to "https://example.com/icon.png"))
            .`when`()
            .patch("/favGame/v1/users/icon")
            .then()
            .statusCode(200)
            .body("githubId", equalTo("111111111"))
            .body("iconUrl", equalTo("https://example.com/icon.png"))
    }

    @Test
    fun updateDisplayName_invalidRequestBody_returns400() {
        RestAssured
            .given()
            .header("Authorization", "Bearer test-user-token")
            .contentType(ContentType.JSON)
            .body(emptyMap<String, String>())
            .`when`()
            .patch("/favGame/v1/users/name")
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
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") {
                "org.postgresql.Driver"
            }
        }
    }
}

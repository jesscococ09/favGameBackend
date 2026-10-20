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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.jpa.hibernate.ddl-auto=create"])
class AdminIntegrationTest {
    @LocalServerPort private var port: Int = 0
    @Autowired private lateinit var userRepository: UserRepository
    @MockitoBean private lateinit var githubUserService: GithubUserService
    private lateinit var testUser: User
    private lateinit var adminUser: User
    private lateinit var targetUser: User

    @BeforeEach
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        userRepository.deleteAll()
        testUser = userRepository.save(User(githubId = "111111111", displayName = "Integration Test User", iconUrl = null, isAdmin = false))
        adminUser = userRepository.save(User(githubId = "222222222", displayName = "Integration Test Admin", iconUrl = null, isAdmin = true))
        targetUser = userRepository.save(User(githubId = "333333333", displayName = "Target User", iconUrl = null, isAdmin = false))
        `when`(githubUserService.loadOrCreateUser("test-user-token")).thenReturn(testUser)
        `when`(githubUserService.loadOrCreateUser("test-admin-token")).thenReturn(adminUser)
    }

    @Test fun listUsers_admin_returns200() { RestAssured.given().header("Authorization", "Bearer test-admin-token").accept(ContentType.JSON).`when`().get("/favGame/v1/admin").then().statusCode(200).body("size()", equalTo(3)) }
    @Test fun listUsers_regularUser_returns403() { RestAssured.given().header("Authorization", "Bearer test-user-token").accept(ContentType.JSON).`when`().get("/favGame/v1/admin").then().statusCode(403) }
    @Test fun listUsers_withoutAuthentication_returns401() { RestAssured.given().accept(ContentType.JSON).`when`().get("/favGame/v1/admin").then().statusCode(401) }

    @Test
    fun updateUserRole_admin_returnsUpdatedUser() {
        val targetId = requireNotNull(targetUser.id)
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body(mapOf("isAdmin" to true)).`when`().patch("/favGame/v1/admin/$targetId").then().statusCode(200).body("githubId", equalTo("333333333")).body("isAdmin", equalTo(true))
    }

    @Test fun getUser_resourceDoesNotExist_returns404() { RestAssured.given().header("Authorization", "Bearer test-admin-token").accept(ContentType.JSON).`when`().get("/favGame/v1/admin/does-not-exist").then().statusCode(404) }

    @Test
    fun updateUser_invalidRequestBody_returns400() {
        val targetId = requireNotNull(targetUser.id)
        RestAssured.given().header("Authorization", "Bearer test-admin-token").contentType(ContentType.JSON).body("{").`when`().patch("/favGame/v1/admin/$targetId").then().statusCode(400)
    }

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

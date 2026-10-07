package com.example.demo.service

import com.example.demo.model.User
import com.example.demo.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.*

class GithubUserServiceTest {

    private val userRepository = mock(UserRepository::class.java)

    @Test
    fun `loadOrCreateUser creates new user when not existing`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.createServer(restTemplate)

        val json = """
            {
              "id": 123,
              "login": "jessika",
              "name": "Jessika",
              "avatar_url": "http://avatar.com/me.png"
            }
        """.trimIndent()

        server.expect(requestTo("https://api.github.com/user"))
            .andExpect(method(org.springframework.http.HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token123"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON))

        val service = GithubUserService(userRepository)

        val restTemplateField = GithubUserService::class.java.getDeclaredField("restTemplate")
        restTemplateField.isAccessible = true
        restTemplateField.set(service, restTemplate)

        `when`(userRepository.findByGithubId("123")).thenReturn(null)

        val savedUser = User(
            githubId = "123",
            displayName = "Jessika",
            iconUrl = "http://avatar.com/me.png",
            isAdmin = false
        )

        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        val result = service.loadOrCreateUser("token123")

        assertEquals("123", result.githubId)
        assertEquals("Jessika", result.displayName)
        assertEquals("http://avatar.com/me.png", result.iconUrl)
        assertFalse(result.isAdmin)

        server.verify()
    }

    @Test
    fun `loadOrCreateUser returns existing user`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.createServer(restTemplate)

        val json = """
            {
              "id": 123,
              "login": "jessika"
            }
        """.trimIndent()

        server.expect(requestTo("https://api.github.com/user"))
            .andRespond(withSuccess(json, MediaType.APPLICATION_JSON))

        val service = GithubUserService(userRepository)

        val restTemplateField = GithubUserService::class.java.getDeclaredField("restTemplate")
        restTemplateField.isAccessible = true
        restTemplateField.set(service, restTemplate)

        val existing = User(
            githubId = "123",
            displayName = "Jessika",
            iconUrl = null,
            isAdmin = false
        )

        `when`(userRepository.findByGithubId("123")).thenReturn(existing)

        val result = service.loadOrCreateUser("token123")

        assertEquals(existing, result)

        server.verify()
    }

    @Test
    fun `loadOrCreateUser throws when GitHub fails`() {
        val restTemplate = RestTemplate()
        val server = MockRestServiceServer.createServer(restTemplate)

        server.expect(requestTo("https://api.github.com/user"))
            .andRespond(withStatus(org.springframework.http.HttpStatus.UNAUTHORIZED))

        val service = GithubUserService(userRepository)

        val restTemplateField = GithubUserService::class.java.getDeclaredField("restTemplate")
        restTemplateField.isAccessible = true
        restTemplateField.set(service, restTemplate)

        assertThrows(RuntimeException::class.java) {
            service.loadOrCreateUser("badtoken")
        }

        server.verify()
    }
}

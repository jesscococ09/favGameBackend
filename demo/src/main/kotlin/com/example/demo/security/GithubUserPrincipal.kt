package com.example.demo.security

import com.example.demo.model.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class GithubUserPrincipal(val user: User) :
    AbstractAuthenticationToken(userAuthorities(user)) {
    init { super.setAuthenticated(true) }
    override fun getCredentials(): Any = ""
    override fun getPrincipal(): Any = user
    override fun getName(): String = user.githubId.toString()
    companion object {
        fun userAuthorities(user: User): Collection<GrantedAuthority> =
            if (user.isAdmin)
                listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            else
                listOf(SimpleGrantedAuthority("ROLE_USER"))
    }
}
package com.example.demo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(name = "githubid")
    val githubId: String,

    @Column(name = "displayname")
    val displayName: String,

    @Column(name = "iconurl")
    val iconUrl: String? = null,

    @Column(name = "isadmin")
    val isAdmin: Boolean = false
)
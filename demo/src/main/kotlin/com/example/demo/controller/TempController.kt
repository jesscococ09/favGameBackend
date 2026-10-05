package com.example.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favGame/v1")
class TempController {
    @GetMapping("/admin")
    fun adminTest() = mapOf("status" to "admin ok")
    @GetMapping("/games")
    fun gamesTest() = mapOf("status" to "games ok")
}
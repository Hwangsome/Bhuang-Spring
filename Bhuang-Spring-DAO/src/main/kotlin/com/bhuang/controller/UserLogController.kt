package com.bhuang.controller

import com.bhuang.model.UserEntity
import com.bhuang.service.UserLogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserLogController @Autowired constructor(
    private val userLogService: UserLogService
) {
    @PostMapping("/logUserCreation")
    fun logUserCreation() {
        val john =  UserEntity().apply {
            name = "John Doe"
            age = 30
            email = "john@email.com"
        }
        userLogService.logUserCreation(john)
    }
}
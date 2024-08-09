package com.bhuang.controller

import com.bhuang.model.UserEntity
import com.bhuang.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController @Autowired constructor(
     private val userService: UserService
) {

    @PostMapping("/addUser")
    fun addUser():ResponseEntity<String> {
        val john =  UserEntity().apply {
            name = "John Doe"
            age = 30
            email = "john@email.com"
        }
        return if (userService.addUser(john) != 0) {
            ResponseEntity.ok("User added")
        } else {
            ResponseEntity.ok("User not added")
        }
    }
}
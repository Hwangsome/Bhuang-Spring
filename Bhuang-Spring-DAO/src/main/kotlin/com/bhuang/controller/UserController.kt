package com.bhuang.controller

import com.bhuang.model.UserEntity
import com.bhuang.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController @Autowired constructor(
     private val userService: UserService
) {

    @PostMapping("/addUser")
    // @RequestBody注解：在方法参数中，缺少@RequestBody注解会导致Spring无法将请求体中的JSON数据自动映射到UserEntity对象.
    fun addUser(@RequestBody userEntity: UserEntity):ResponseEntity<String> {
        return if (userService.addUser(userEntity) != 0) {
            ResponseEntity.ok("User added")
        } else {
            ResponseEntity.ok("User not added")
        }
    }

    @PostMapping("/addUserWithTransaction")
    fun addUserWithTransaction(@RequestBody userEntity: UserEntity):ResponseEntity<String> {
        return if (userService.addUserWithTransaction(userEntity) != 0) {
            ResponseEntity.ok("User added")
        } else {
            ResponseEntity.ok("User not added")
        }
    }

    @PostMapping("/addUserWithTransactionAnno")
    fun addUserWithTransactionAnno(@RequestBody userEntity: UserEntity):ResponseEntity<String> {
        return if (userService.addUserWithTransactionAnno(userEntity) != 0) {
            ResponseEntity.ok("User added")
        } else {
            ResponseEntity.ok("User not added")
        }
    }

    @PostMapping("/addUserByTestPropagation")
    fun addUserByTestPropagation(@RequestBody userEntity: UserEntity):ResponseEntity<String> {
        return if (userService.addUserByTestPropagation(userEntity) != 0) {
            ResponseEntity.ok("User added")
        } else {
            ResponseEntity.ok("User not added")
        }
    }
}
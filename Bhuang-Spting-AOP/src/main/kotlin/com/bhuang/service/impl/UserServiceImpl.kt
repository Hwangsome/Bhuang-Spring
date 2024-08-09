package com.bhuang.service.impl

import com.bhuang.annos.LogExecutionTime
import com.bhuang.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl: UserService {
    override fun addUser(name: String) {
        println("User added: $name")
    }

    @LogExecutionTime
    override fun getUser(name: String): String {
        println("User get: $name")
        return name
    }
}
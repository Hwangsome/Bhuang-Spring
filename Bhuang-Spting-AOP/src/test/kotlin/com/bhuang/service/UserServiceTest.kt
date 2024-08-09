package com.bhuang.service

import com.bhuang.config.AppConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService


    /**
     * 13:59:43.156 [main] INFO com.bhuang.aspect.LoggingAspect -- Before method execution
     * User added: John Doe
     * 13:59:43.158 [main] INFO com.bhuang.aspect.LoggingAspect -- After method execution
     */
    @Test
    fun testUserService() {
        userService.addUser("John Doe")
    }

    @Test
    fun testGetUser() {
        userService.getUser("John Doe")
    }
}
package com.bhuang.model

import com.bhuang.configuration.AppConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
class UserEntityTest {

    @Autowired
    lateinit var userEntity: UserEntity

    @Autowired
    lateinit var personEntity: PersonEntity

    @Test
    fun testUserEntity() {
        assertEquals("John Doe", userEntity.name)
        assertEquals(30, userEntity.age)
        assertEquals("johndoe@example.com", userEntity.email)
        assertEquals("JOHN DOE", userEntity.upperCaseName)
        assertEquals("Development Mode", userEntity.environmentMode)

        assertEquals("Tom smith", personEntity.name)
        assertEquals(39, personEntity.age)
        assertEquals("Tom@example.com", personEntity.email)

    }
}
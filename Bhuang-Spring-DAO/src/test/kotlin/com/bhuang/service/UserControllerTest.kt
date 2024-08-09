package com.bhuang.service

import com.bhuang.controller.UserController
import com.bhuang.model.UserEntity
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever


@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
class UserControllerTest  {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun testAddUser() {
        val john =  UserEntity().apply {
            name = "John Doe"
            age = 30
            email = "john@email.com"
        }
        whenever(userService.addUser(john)).thenReturn(1)
        mockMvc.perform(
            post("/addUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(john))
        )
            .andExpect(status().isOk())
            .andExpect(content().string("User added"))
    }
}
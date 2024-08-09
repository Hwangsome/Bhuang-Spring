package com.bhuang.dao

import com.bhuang.model.UserEntity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UserDaoSpec: FunSpec({

    val userDao = mockk<UserDao>()


    test("test add user") {
        val john = UserEntity().apply {
            name = "John Doe"
            age = 30
            email = "john@email.com"
        }
        every { userDao.addUser(any()) } returns 1
        userDao.addUser(john) shouldBe  1

    }
})
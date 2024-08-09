package com.bhuang.service

import com.bhuang.dao.UserDao
import com.bhuang.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userDao: UserDao
) {
    fun addUser(userEntity: UserEntity) = userDao.addUser(
        userEntity
    )
}
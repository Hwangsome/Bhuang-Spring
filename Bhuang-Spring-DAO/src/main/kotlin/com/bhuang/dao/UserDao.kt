package com.bhuang.dao

import com.bhuang.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserDao @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate
){
    fun addUser(userEntity: UserEntity):Int {
        return jdbcTemplate.update("insert into user(name, age, email) values(?, ?, ?)", userEntity.name, userEntity.age, userEntity.email)
    }
}
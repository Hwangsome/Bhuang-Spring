package com.bhuang.dao

import com.bhuang.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionSynchronizationManager

@Repository
class UserDao @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate
){
    fun addUser(userEntity: UserEntity):Int {
        return jdbcTemplate.update("insert into user(name, age, email) values(?, ?, ?)", userEntity.name, userEntity.age, userEntity.email)
    }

    fun findUserById(id: Int): UserEntity? {
        return  try {
            jdbcTemplate.queryForObject("select * from user where id = ?", arrayOf(id)) { rs, _ ->
                UserEntity(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    age = rs.getInt("age"),
                    email = rs.getString("email")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun findUserList() = jdbcTemplate.query("select * from user") { rs, _ ->
        UserEntity(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            age = rs.getInt("age"),
            email = rs.getString("email")
        )
    }
}
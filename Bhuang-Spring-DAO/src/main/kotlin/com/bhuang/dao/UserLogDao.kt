package com.bhuang.dao

import com.bhuang.model.UserLogEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserLogDao(private val jdbcTemplate: JdbcTemplate) {

    fun save(userLogEntity: UserLogEntity) {
        val sql = """
            INSERT INTO user_log_entity (name, age, email, created_at) 
            VALUES (?, ?, ?, ?)
        """
        jdbcTemplate.update(
            sql,
            userLogEntity.name,
            userLogEntity.age,
            userLogEntity.email,
            userLogEntity.createdAt
        )
    }
}
package com.bhuang.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class UserEntity(
    @Value("\${users.name}")
    val name: String,

    @Value("\${users.age}")
    val age: Int,

    @Value("\${users.email}")
    val email: String,

    // 使用 SpEL 表达式来处理属性
    @Value("#{'\${users.name}'.toUpperCase()}")
    val upperCaseName: String,

    // 计算属性值
    @Value("#{T(java.lang.Math).random() * 100.0}")
    val randomNumber: Double,

    // 条件表达式
    @Value("#{'\${app.environment}' == 'production' ? 'Production Mode' : 'Development Mode'}")
    val environmentMode: String
)
package com.bhuang.model

import java.time.LocalDateTime

data class UserLogEntity(
    var id: Int? = null,
    var name: String? = null,
    var age: Int? = null,
    var email: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
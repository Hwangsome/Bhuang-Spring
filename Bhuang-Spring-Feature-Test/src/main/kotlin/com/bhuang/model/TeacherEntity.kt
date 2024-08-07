package com.bhuang.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


data class TeacherEntity (
    var name: String = "",
    var age: Int = 0,
    var email: String = ""
)
package com.bhuang.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "person")
data class PersonEntity (
    var name: String = "",
    var age: Int = 0,
    var email: String = ""
)
package com.bhuang.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ComponentScan(basePackages = ["com.bhuang"])
@PropertySource("classpath:application.properties")
class AppConfig
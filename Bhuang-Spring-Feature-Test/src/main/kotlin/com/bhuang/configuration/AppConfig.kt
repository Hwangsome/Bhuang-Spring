package com.bhuang.configuration

import com.bhuang.model.PersonEntity
import com.bhuang.model.TeacherEntity
import com.bhuang.model.TestBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ComponentScan(basePackages = ["com.bhuang"])
@PropertySource("classpath:application.properties")
class AppConfig {


    @Bean
    fun teacherEntity1():TeacherEntity = TeacherEntity("zs", 15, "zs@163.com")

    @Bean
    fun teacherEntity2():TeacherEntity = TeacherEntity("ls", 18, "ls@163.com")

    @Bean(initMethod = "init", destroyMethod = "destroy")
    fun testBean(): TestBean {
        return TestBean().apply {
            name = "TestBean"
        }

    }


}
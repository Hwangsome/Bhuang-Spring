package com.bhuang.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionTemplate

@Configuration
@ComponentScan(basePackages = ["com.bhuang"])
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy
class AppConfig {
    @Value("\${datasource.url}")
    private lateinit var url:String

    @Value("\${datasource.username}")
    private lateinit var userName:String

    @Value("\${datasource.password}")
    private lateinit var passWord:String

    @Value("\${datasource.driver-class-name}")
    private lateinit var driver_Class_Name:String


//    @Bean
//    fun dataSource() =
//        DriverManagerDataSource(url, userName, passWord).apply {
//            setDriverClassName(driver_Class_Name)
//        }

    @Bean
    fun hikariDataSource() =
        HikariDataSource().apply {
            username = userName
            password = passWord
            jdbcUrl = url
            driverClassName = driver_Class_Name
        }

    // use hikariDataSource
    @Bean
    fun jdbcTemplate() = JdbcTemplate(hikariDataSource())


    // DataSourceTransactionManager ：事务管理器，它负责控制事务
    @Bean
    fun dataSourceTransactionManager() = DataSourceTransactionManager(hikariDataSource())

    // TransactionTemplate ：事务模板，使用它可以完成编程式事务
    @Bean
    fun transactionTemplate() = TransactionTemplate(dataSourceTransactionManager())
}
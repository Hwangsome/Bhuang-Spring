package com.bhuang.aspect

import com.bhuang.annos.LogExecutionTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {
    private val logger: Logger get() = LoggerFactory.getLogger(this::class.java)

    @Before("execution(* com.bhuang.service.UserService.addUser(..))")
    fun logBefore() {
        logger.info("Before method execution")
    }

    @After("execution(* com.bhuang.service.UserService.addUser(..))")
    fun logAfter() {
        logger.info("After method execution")
    }

//    @Around("execution(* com.bhuang.service.UserService.getUser(..))")
    @Around("@annotation(com.bhuang.annos.LogExecutionTime)")
    fun around( joinPoint: ProceedingJoinPoint):Any? {
        // this target 输出一样是因为代理类没有增强 toString 方法
        // this: com.bhuang.service.impl.UserServiceImpl@39ab5ef7
        //target: com.bhuang.service.impl.UserServiceImpl@39ab5ef7
        println("this: ${joinPoint.`this`}");
        println("target: ${joinPoint.target}");
        println("signature: ${joinPoint.signature}");
        println("args : ${joinPoint.args}")
        var proceed : Any? = null
        try {
            val startTime = System.currentTimeMillis()
            proceed = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            println("Around after get user");
            println("Method execution time: " + (endTime - startTime) + " milliseconds");
        } catch (e: Exception) {
          println("Exception: $e")
        } finally {
            println("Finally")
        }
        return proceed
    }

}
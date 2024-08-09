package com.bhuang.processor

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
class MyProcessor(): BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (beanName == "testBean") {
            println("BeforeInitialization : $beanName")
        }

        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (beanName == "testBean") {
            println("AfterInitialization : $beanName")
        }

        return bean
    }
}
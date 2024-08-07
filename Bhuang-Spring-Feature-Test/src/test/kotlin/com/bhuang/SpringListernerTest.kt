//package com.bhuang
//
//import com.bhuang.listener.MyApplicationContextEventListener
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.ConfigurableApplicationContext
//import org.springframework.test.annotation.DirtiesContext
//import kotlin.test.assertTrue
//
//
////@SpringBootTest
////@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//class SpringListernerTest {
//
////    @Autowired
////    private lateinit var context:ConfigurableApplicationContext
////
////    @Autowired
////    private lateinit var listener: MyApplicationContextEventListener
//
////    @Test
//    fun testListener() {
//        // 手动启动和停止上下文
//        context.start()
//        context.stop()
//        context.close()
////
////
////        // 获取捕获的事件
//        val events = listener.events
////
////
////        // 验证事件
////        assertTrue(events.contains("ContextRefreshedEvent"), "ContextRefreshedEvent was not fired")
////        assertTrue(events.contains("ContextStartedEvent"), "ContextStartedEvent was not fired")
////        assertTrue(events.contains("ContextStoppedEvent"), "ContextStoppedEvent was not fired")
////        assertTrue(events.contains("ContextClosedEvent"), "ContextClosedEvent was not fired")
//    }
//}
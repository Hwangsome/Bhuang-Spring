package com.bhuang.model

import com.bhuang.configuration.AppConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
class UserEntityTest {

    @Autowired
    lateinit var userEntity: UserEntity

    @Autowired
    lateinit var personEntity: PersonEntity

    @Autowired
    lateinit var testBean: TestBean

    // 在 Spring 中使用集合类型（如 List、Set 或 Map）来接收多个相同类型的 Bean。使用 List 或 Set 可以简单地接收所有相同类型的 Bean
    @Autowired
    lateinit var teachers: List<TeacherEntity>

    // 使用 Map 可以将 Bean 名称与 Bean 实例关联起来，这在需要按名称处理 Bean 的场景中非常有用。这样可以确保在处理多个相同类型的 Bean 时，灵活且准确地进行依赖注入
    @Autowired
    lateinit var teacherMap: Map<String, TeacherEntity>

    @Test
    fun testUserEntity() {
        assertEquals("John Doe", userEntity.name)
        assertEquals(30, userEntity.age)
        assertEquals("johndoe@example.com", userEntity.email)
        assertEquals("JOHN DOE", userEntity.upperCaseName)
        assertEquals("Development Mode", userEntity.environmentMode)

        assertEquals("Tom smith", personEntity.name)
        assertEquals(39, personEntity.age)
        assertEquals("Tom@example.com", personEntity.email)

        teachers.forEach { person ->
            println("Person: ${person.name}")
        }

        assertEquals(2, teachers.size)

        // Key: teacherEntity1, Value: zs
        // Key: teacherEntity2, Value: ls
        teacherMap.forEach { (key, value) ->
            println("Key: $key, Value: ${value.name}")
        }


        println(testBean.name)
    }
}
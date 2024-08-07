# Bhuang-Spring
## Spring 的监听机制
Spring Boot 的监听机制主要是通过事件（Events）和监听器（Listeners）来实现的。这种机制使得在应用程序的生命周期内，可以捕捉和处理各种事件。下面是一个概述：

### 1. 事件和监听器

#### 事件（Events）

Spring Boot 提供了多种事件，这些事件在应用程序启动和运行过程中会被触发。常见的事件包括：

- **ApplicationStartingEvent**：Spring Boot 应用启动开始时发送。
- **ApplicationEnvironmentPreparedEvent**：在 Spring Boot 的 `Environment` 准备好但没有创建上下文时发送。
- **ApplicationPreparedEvent**：在刷新上下文之前发送。
- **ApplicationStartedEvent**：在上下文已刷新且应用已启动，但还没有调用 `CommandLineRunner` 和 `ApplicationRunner` 之前发送。
- **ApplicationReadyEvent**：在调用所有 `CommandLineRunner` 和 `ApplicationRunner` 之后发送。
- **ApplicationFailedEvent**：在启动过程中发生异常时发送。

ApplicationContextEvent 是 Spring 框架中的一个基础事件类，表示与 ApplicationContext 生命周期相关的事件。ApplicationContextEvent 是一个抽象类，Spring 提供了几个具体的子类来代表不同的上下文事件。

1. ApplicationContextEvent 子类
 以下是 ApplicationContextEvent 的几个常见子类及其作用：
- ContextRefreshedEvent：在 ApplicationContext 被初始化或刷新时发布。当所有的 Beans 都被完全初始化或刷新完成时触发。
- ContextStartedEvent：在 ApplicationContext 启动时发布，启动时会调用 start() 方法。
- ContextStoppedEvent：在 ApplicationContext 停止时发布，停止时会调用 stop() 方法。
- ContextClosedEvent：在 ApplicationContext 关闭时发布，关闭时会调用 close() 方法。

#### 监听器（Listeners）

监听器是实现 `ApplicationListener` 接口的类，用于监听上述事件。例如：

```java
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 处理事件的逻辑
        System.out.println("Application is ready!");
    }
}
```

### 2. 注册监听器

有几种方式可以注册监听器：

#### 使用 `@Component`

如上例所示，通过 `@Component` 注解将监听器注册为 Spring 的 Bean。

#### 在 `SpringApplication` 中注册

可以在 `SpringApplication` 实例中直接注册监听器：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MyApplication.class);
        app.addListeners(new MyApplicationReadyEventListener());
        app.run(args);
    }
}
```

#### 使用 `META-INF/spring.factories`

在 `META-INF/spring.factories` 文件中，添加监听器的全限定类名：

```
org.springframework.context.ApplicationListener=\
com.example.MyApplicationReadyEventListener
```

### 3. 自定义事件

除了使用 Spring Boot 提供的事件，还可以自定义事件：

#### 定义事件类

```java
import org.springframework.context.ApplicationEvent;

public class MyCustomEvent extends ApplicationEvent {
    private String message;

    public MyCustomEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

#### 发布事件

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MyEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(String message) {
        MyCustomEvent event = new MyCustomEvent(this, message);
        applicationEventPublisher.publishEvent(event);
    }
}
```

#### 监听事件

```java
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyCustomEventListener implements ApplicationListener<MyCustomEvent> {
    @Override
    public void onApplicationEvent(MyCustomEvent event) {
        System.out.println("Received custom event - " + event.getMessage());
    }
}
```

### 4. 总结

Spring Boot 的监听机制通过事件和监听器使得开发者可以在应用的不同生命周期阶段插入自定义逻辑，从而更好地控制应用的启动、运行和关闭过程。这种机制非常灵活，既支持内置事件，也支持自定义事件，适用于各种场景。

## 占位符的取值范围
是的，你说得很对。在 Spring 框架中，属性文件加载后会转换成 `Map` 的形式，并存储在 `Environment` 抽象中。`Environment` 是 Spring 提供的一个接口，用于表示应用环境，包含了一组 `PropertySource` 对象，这些对象用于管理应用配置属性的来源。通过 `Environment`，Spring 可以统一管理和访问这些配置属性。

### Environment 接口

`Environment` 接口主要有以下几个实现：
- `StandardEnvironment`：用于标准的环境配置，包含系统属性和环境变量。
- `StandardServletEnvironment`：扩展了 `StandardEnvironment`，增加了 `ServletContext` 和 `ServletConfig` 的属性支持。

`Environment` 提供的方法包括：
- `getProperty(String key)`：获取指定键的属性值。
- `containsProperty(String key)`：检查是否包含指定键的属性。
- `getActiveProfiles()`：获取当前激活的配置文件（profiles）。
- `getDefaultProfiles()`：获取默认的配置文件。

### PropertySource

`PropertySource` 是 Spring 用于抽象配置属性来源的类，它可以从各种来源（如属性文件、系统属性、环境变量等）读取配置属性。

Spring 提供了多种 `PropertySource` 实现：
- `PropertiesPropertySource`：从 `Properties` 对象读取属性。
- `MapPropertySource`：从 `Map` 对象读取属性。
- `SystemEnvironmentPropertySource`：从系统环境变量读取属性。
- `ServletContextPropertySource` 和 `ServletConfigPropertySource`：从 `ServletContext` 和 `ServletConfig` 读取属性。

### @PropertySource 和 @Value 示例

以下是一个完整的示例，展示了如何使用 `@PropertySource` 加载属性文件，并使用 `@Value` 注解注入属性值。

#### 项目结构

```
src
└── main
    ├── kotlin
    │   └── com
    │       └── example
    │           └── project
    │               ├── AppConfig.kt
    │               ├── MainApp.kt
    │               └── model
    │                   └── UserEntity.kt
    └── resources
        └── application.properties
```

#### application.properties

```properties
user.name=John Doe
user.age=30
user.email=johndoe@example.com
```

#### AppConfig.kt

```kotlin
package com.example.project

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ComponentScan(basePackages = ["com.example.project"])
@PropertySource("classpath:application.properties")
class AppConfig
```

#### UserEntity.kt

```kotlin
package com.example.project.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class UserEntity(
    @Value("\${user.name}")
    val name: String,

    @Value("\${user.age}")
    val age: Int,

    @Value("\${user.email}")
    val email: String
)
```

#### MainApp.kt

```kotlin
package com.example.project

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import com.example.project.model.UserEntity

fun main() {
    val context: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java)
    val user: UserEntity = context.getBean(UserEntity::class.java)
    println(user)
}
```

#### UserEntityTest.kt

```kotlin
package com.bhuang.model

import com.bhuang.configuration.AppConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AppConfig::class])
class UserEntityTest {

    @Autowired
    lateinit var userEntity: UserEntity

    @Test
    fun testUserEntity() {
        assertEquals("John Doe", userEntity.name)
        assertEquals(30, userEntity.age)
        assertEquals("johndoe@example.com", userEntity.email)
    }
}
```

### Environment 的使用

你可以在任何需要访问配置属性的地方注入 `Environment` 对象，直接访问配置属性。

#### 示例

**MyService.kt**

```kotlin
package com.example.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class MyService @Autowired constructor(private val env: Environment) {
    fun printProperty() {
        val userName = env.getProperty("user.name")
        val userAge = env.getProperty("user.age")
        val userEmail = env.getProperty("user.email")
        println("User Name: $userName")
        println("User Age: $userAge")
        println("User Email: $userEmail")
    }
}
```

**MainApp.kt**

```kotlin
package com.example.project

import com.example.project.service.MyService
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    val context: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java)
    val myService: MyService = context.getBean(MyService::class.java)
    myService.printProperty()
}
```

通过这种方式，你可以灵活地访问和管理 Spring 应用中的配置属性。`Environment` 提供了一个统一的接口，可以从各种不同的来源获取配置属性，确保你的应用配置是灵活和可管理的。

## SpEL表达式
Spring Expression Language（SpEL）是一种功能强大的表达式语言，用于在 Spring 应用程序中动态地解析和评估表达式。SpEL 支持变量、函数、属性访问、方法调用、逻辑和算术运算、正则表达式等。它可以在多种场景下使用，如 `@Value` 注解、XML 配置、注解配置等。

### 基本语法

SpEL 表达式通常用 `${}` 或 `#{}` 包裹：
- `${}`：用于属性占位符解析。
- `#{}`：用于 SpEL 表达式解析。

### 示例项目结构

```
src
└── main
    ├── kotlin
    │   └── com
    │       └── example
    │           └── project
    │               ├── AppConfig.kt
    │               ├── MainApp.kt
    │               └── model
    │                   └── UserEntity.kt
    │                   └── MyService.kt
    └── resources
        └── application.properties
```

### 1. 创建属性文件

**application.properties**

```properties
user.name=John Doe
user.age=30
user.email=johndoe@example.com
app.environment=production
```

### 2. 创建配置类

**AppConfig.kt**

```kotlin
package com.example.project

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ComponentScan(basePackages = ["com.example.project"])
@PropertySource("classpath:application.properties")
class AppConfig
```

### 3. 使用 SpEL 表达式的 UserEntity 类

**UserEntity.kt**

```kotlin
package com.example.project.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class UserEntity(
    @Value("\${user.name}")
    val name: String,

    @Value("\${user.age}")
    val age: Int,

    @Value("\${user.email}")
    val email: String,

    // 使用 SpEL 表达式来处理属性
    @Value("#{'\${user.name}'.toUpperCase()}")
    val upperCaseName: String,

    // 计算属性值
    @Value("#{T(java.lang.Math).random() * 100.0}")
    val randomNumber: Double,

    // 条件表达式
    @Value("#{'\${app.environment}' == 'production' ? 'Production Mode' : 'Development Mode'}")
    val environmentMode: String
)
```

### 4. 创建服务类，演示更多 SpEL 用法

**MyService.kt**

```kotlin
package com.example.project.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class MyService @Autowired constructor(private val env: Environment) {

    @Value("#{'\${user.age}' > 25}")
    lateinit var isAdult: Boolean

    @Value("#{systemProperties['user.home']}")
    lateinit var userHome: String

    fun printSpelExamples() {
        println("Is user an adult? $isAdult")
        println("User home directory: $userHome")
    }
}
```


### 解释

1. **@Value("${user.name}")**：从属性文件中注入 `user.name` 的值。
2. **@Value("#{'\${user.name}'.toUpperCase()}")**：使用 SpEL 表达式将 `user.name` 转换为大写。
3. **@Value("#{T(java.lang.Math).random() * 100.0}")**：使用 SpEL 表达式生成一个随机数。
4. **@Value("#{'\${app.environment}' == 'production' ? 'Production Mode' : 'Development Mode'}")**：使用 SpEL 表达式基于条件设置属性值。
5. **@Value("#{'\${user.age}' > 25}")**：使用 SpEL 表达式判断用户是否是成年人。
6. **@Value("#{systemProperties['user.home']}")**：使用 SpEL 表达式获取系统属性 `user.home` 的值。

通过上述示例，我们展示了如何在 Spring 应用中使用 SpEL 表达式来动态解析和评估属性值。这使得配置更加灵活和强大，能够处理更复杂的需求。

# IOC基础-依赖注入-自动注入&复杂类型注入
在 Spring 框架中，`@Autowired` 注解用于自动注入依赖，可以用于构造函数、属性、setter 方法和配置类中的 `@Bean` 方法。对于多个相同类型的 Bean，Spring 提供了 `@Qualifier` 注解来指定注入的 Bean 名称，以及 `@Primary` 注解来指定默认的 Bean。

### 1. 使用 @Autowired 进行自动注入

`@Autowired` 注解可以用于构造函数、属性和 setter 方法，来自动注入 Spring 容器中的 Bean。

#### 示例

```kotlin
package com.example.project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(private val userRepository: UserRepository) {
    fun performService() {
        println("Service performed with user: ${userRepository.getUser()}")
    }
}
```

### 2. 在配置类中使用 @Autowired 和 @Bean

`@Autowired` 注解可以用于配置类中的 `@Bean` 方法，以便在创建 Bean 时注入依赖。

#### 示例

```kotlin
package com.example.project.configuration

import com.example.project.repository.UserRepository
import com.example.project.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun userRepository(): UserRepository {
        return UserRepository()
    }

    @Bean
    fun userService(@Autowired userRepository: UserRepository): UserService {
        return UserService(userRepository)
    }
}
```

### 3. 多个相同类型 Bean 的自动注入

当存在多个相同类型的 Bean 时，可以使用 `@Qualifier` 注解来指定注入的 Bean 名称，或者使用 `@Primary` 注解来指定默认的 Bean。

#### 示例

**UserRepository.kt**

```kotlin
package com.example.project.repository

import org.springframework.stereotype.Repository

@Repository("primaryUserRepository")
class PrimaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Primary User"
    }
}

@Repository("secondaryUserRepository")
class SecondaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Secondary User"
    }
}

interface UserRepository {
    fun getUser(): String
}
```

**UserService.kt**

```kotlin
package com.example.project.service

import com.example.project.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    @Qualifier("primaryUserRepository")
    lateinit var userRepository: UserRepository

    fun performService() {
        println("Service performed with user: ${userRepository.getUser()}")
    }
}
```

**AppConfig.kt**

```kotlin
package com.example.project.configuration

import com.example.project.repository.PrimaryUserRepository
import com.example.project.repository.SecondaryUserRepository
import com.example.project.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AppConfig {

    @Bean
    @Primary
    fun primaryUserRepository(): PrimaryUserRepository {
        return PrimaryUserRepository()
    }

    @Bean
    fun secondaryUserRepository(): SecondaryUserRepository {
        return SecondaryUserRepository()
    }

    @Bean
    fun userService(): UserService {
        return UserService()
    }
}
```

### 4. 使用 @Primary 注解指定默认的 Bean

当有多个相同类型的 Bean 时，可以使用 `@Primary` 注解来指定其中一个为默认的 Bean。

#### 示例

```kotlin
package com.example.project.repository

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository("primaryUserRepository")
@Primary
class PrimaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Primary User"
    }
}

@Repository("secondaryUserRepository")
class SecondaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Secondary User"
    }
}

interface UserRepository {
    fun getUser(): String
}
```

**UserService.kt**

```kotlin
package com.example.project.service

import com.example.project.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(private val userRepository: UserRepository) {
    fun performService() {
        println("Service performed with user: ${userRepository.getUser()}")
    }
}
```

### 5. 使用 @Qualifier 指定特定的 Bean

当需要指定特定的 Bean 时，可以使用 `@Qualifier` 注解。

#### 示例

```kotlin
package com.example.project.service

import com.example.project.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    @Qualifier("secondaryUserRepository") private val userRepository: UserRepository
) {
    fun performService() {
        println("Service performed with user: ${userRepository.getUser()}")
    }
}
```

### 6. 完整示例

以下是一个完整的示例，展示了如何使用 `@Autowired`、`@Qualifier` 和 `@Primary` 来自动注入依赖。

#### 项目结构

```
src
└── main
    ├── kotlin
    │   └── com
    │       └── example
    │           └── project
    │               ├── configuration
    │               │   └── AppConfig.kt
    │               ├── repository
    │               │   ├── PrimaryUserRepository.kt
    │               │   ├── SecondaryUserRepository.kt
    │               │   └── UserRepository.kt
    │               ├── service
    │               │   └── UserService.kt
    │               └── MainApp.kt
    └── resources
        └── application.properties
```

#### application.properties

```properties
# Application properties
```

#### AppConfig.kt

```kotlin
package com.example.project.configuration

import com.example.project.repository.PrimaryUserRepository
import com.example.project.repository.SecondaryUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AppConfig {

    @Bean
    @Primary
    fun primaryUserRepository(): PrimaryUserRepository {
        return PrimaryUserRepository()
    }

    @Bean
    fun secondaryUserRepository(): SecondaryUserRepository {
        return SecondaryUserRepository()
    }
}
```

#### PrimaryUserRepository.kt

```kotlin
package com.example.project.repository

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class PrimaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Primary User"
    }
}
```

#### SecondaryUserRepository.kt

```kotlin
package com.example.project.repository

import org.springframework.stereotype.Repository

@Repository
class SecondaryUserRepository : UserRepository {
    override fun getUser(): String {
        return "Secondary User"
    }
}
```

#### UserRepository.kt

```kotlin
package com.example.project.repository

interface UserRepository {
    fun getUser(): String
}
```

#### UserService.kt

```kotlin
package com.example.project.service

import com.example.project.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    @Qualifier("secondaryUserRepository") private val userRepository: UserRepository
) {
    fun performService() {
        println("Service performed with user: ${userRepository.getUser()}")
    }
}
```

#### MainApp.kt

```kotlin
package com.example.project

import com.example.project.service.UserService
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    val context: ApplicationContext = AnnotationConfigApplicationContext(AppConfig::class.java)
    val userService: UserService = context.getBean(UserService::class.java)
    userService.performService()
}
```

运行 `MainApp.kt` 文件，你应该会看到如下输出：

```
Service performed with user: Secondary User
```

通过这种方式，你可以使用 `@Autowired`、`@Qualifier` 和 `@Primary` 注解来管理和注入多个相同类型的 Bean，从而灵活地控制依赖注入。

## @Autowired注入的原理逻辑
你的描述准确地解释了 `@Autowired` 注入的逻辑，当 Spring 容器遇到 `@Autowired` 注解时，它会按照以下步骤进行依赖注入：

1. **类型匹配**：
 - 首先，Spring 会根据属性的类型（或构造函数参数的类型）在 IoC 容器中查找相应类型的 bean。
 - 如果找到唯一的一个 bean，Spring 会直接注入该 bean。
 - 如果找到多个相同类型的 bean，Spring 进入下一步。

2. **名称匹配**：
 - 当找到多个相同类型的 bean 时，Spring 会将要注入的属性名（或构造函数参数名）与这些 bean 的 id 逐一对比。
 - 如果有一个 bean 的 id 与属性名相同，Spring 会注入这个 bean。
 - 如果没有任何 bean 的 id 与属性名相同，Spring 会抛出 `NoUniqueBeanDefinitionException` 异常。

3. **处理冲突**：
 - 使用 `@Qualifier` 注解可以指定具体要注入的 bean 名称。
 - 使用 `@Primary` 注解可以指定一个默认的 bean，当存在多个相同类型的 bean 时，优先注入带有 `@Primary` 注解的 bean。


## @Resource,@Inject,@Autowired
在 Spring 框架中，`@Autowired`、`@Resource` 和 `@Inject` 是用于依赖注入的注解，虽然它们都用于注入依赖，但它们的工作方式和配置细节略有不同。下面详细介绍每个注解的功能、用法和区别。

### 1. @Autowired

`@Autowired` 是 Spring 提供的注解，用于自动装配依赖。

- **按类型注入**：默认情况下，`@Autowired` 按类型进行注入。
- **可选注入**：可以使用 `required` 属性来指示是否必须注入。
- **使用场景**：可以用于构造函数、属性、setter 方法。

#### 示例

```kotlin
package com.example.project.service

import com.example.project.model.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PersonService @Autowired constructor(
    private val person: Person
) {
    fun displayPerson() {
        println("Person: ${person.getName()}")
    }
}
```

### 2. @Resource

`@Resource` 是 JSR-250 提供的注解，通常用于将命名资源注入到 Java 应用中。

- **按名称注入**：默认情况下，`@Resource` 按名称进行注入。
- **支持类型注入**：如果找不到名称匹配的 bean，则会按类型进行匹配。
- **使用场景**：可以用于属性和 setter 方法。

#### 示例

```kotlin
package com.example.project.service

import com.example.project.model.Person
import javax.annotation.Resource
import org.springframework.stereotype.Service

@Service
class PersonService {

    @Resource(name = "teacher") // 按名称注入
    private lateinit var person: Person

    fun displayPerson() {
        println("Person: ${person.getName()}")
    }
}
```

### 3. @Inject

`@Inject` 是 JSR-330 提供的注解，由 Java 依赖注入规范定义。

- **按类型注入**：默认情况下，`@Inject` 按类型进行注入。
- **类似 @Autowired**：功能类似于 `@Autowired`，但没有 Spring 的 `required` 属性。
- **使用场景**：可以用于构造函数、属性、setter 方法。

#### 示例

```kotlin
package com.example.project.service

import com.example.project.model.Person
import javax.inject.Inject
import org.springframework.stereotype.Service

@Service
class PersonService @Inject constructor(
    private val person: Person
) {
    fun displayPerson() {
        println("Person: ${person.getName()}")
    }
}
```

### 对比

| 特性            | @Autowired                         | @Resource                           | @Inject                          |
|-----------------|------------------------------------|-------------------------------------|----------------------------------|
| 提供者          | Spring                              | JSR-250                             | JSR-330                          |
| 注入类型        | 按类型（默认），可以结合 @Qualifier 按名称 | 按名称（默认），找不到则按类型注入     | 按类型                            |
| 支持构造函数注入  | 是                                  | 否                                  | 是                                |
| 支持属性注入      | 是                                  | 是                                  | 是                                |
| 支持 setter 注入 | 是                                  | 是                                  | 是                                |
| 可选属性          | required=false 可选                | 无                                  | 无                                |
| 主要使用场景      | Spring 应用中的依赖注入             | Java EE 和 Spring 应用中的命名资源注入 | Java 依赖注入规范的依赖注入场景    |


# IOC进阶-依赖注入-回调注入&延迟注入
##  比较常用的几个回调接口
当然可以！下面是一些常见场景，每个场景中展示了如何使用这些回调接口：

### 1. `BeanFactoryAware` - 动态获取Bean实例

场景：在运行时根据条件动态获取不同的Bean实例。

```java
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

@Component
public class MyService implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void performTask(String beanName) {
        MyTask task = beanFactory.getBean(beanName, MyTask.class);
        task.execute();
    }
}
```

### 2. `ApplicationContextAware` - 获取应用上下文中的环境变量

场景：在Bean中需要访问Spring应用上下文提供的环境变量。

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyComponent implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws ApplicationContextException {
        this.applicationContext = applicationContext;
    }

    public void printEnvironmentProperty() {
        Environment environment = applicationContext.getEnvironment();
        String property = environment.getProperty("my.property");
        System.out.println("Property value: " + property);
    }
}
```

### 3. `EnvironmentAware` - 根据环境配置进行初始化

场景：根据不同的环境变量进行不同的初始化操作。

```java
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyInitializer implements EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void initialize() {
        String profile = environment.getActiveProfiles()[0];
        if ("development".equals(profile)) {
            // 初始化开发环境
        } else if ("production".equals(profile)) {
            // 初始化生产环境
        }
    }
}
```

### 4. `ApplicationEventPublisherAware` - 发布自定义事件

场景：在某些操作后发布自定义事件，通知其他组件。

```java
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class MyEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent() {
        MyEvent event = new MyEvent(this);
        eventPublisher.publishEvent(event);
    }
}

public class MyEvent extends ApplicationEvent {
    public MyEvent(Object source) {
        super(source);
    }
}
```

### 5. `ResourceLoaderAware` - 加载外部资源文件

场景：在Bean中加载和处理外部配置文件或资源。

```java
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class MyResourceLoader implements ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void loadResource(String location) throws IOException {
        Resource resource = resourceLoader.getResource(location);
        String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
        System.out.println("Resource content: " + content);
    }
}
```

### 6. `BeanClassLoaderAware` - 动态加载类

场景：在运行时动态加载类进行操作。

```java
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.stereotype.Component;

@Component
public class MyClassLoader implements BeanClassLoaderAware {
    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass(className);
        System.out.println("Loaded class: " + clazz.getName());
    }
}
```

### 7. `BeanNameAware` - 获取Bean名称

场景：在Bean中需要访问或记录其在Spring容器中的名称。

```java
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

@Component
public class MyBean implements BeanNameAware {
    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void printBeanName() {
        System.out.println("Bean name: " + beanName);
    }
}
```

这些示例展示了在实际应用中如何使用Spring的回调接口，使得Bean能够与Spring容器更好地集成和互动。

## ObjectProvider
在Spring框架中，`ObjectProvider`是一个强大的工具，用于处理依赖注入的懒加载和依赖管理。它提供了一种灵活的方法来获取和使用Spring Bean，同时支持可选依赖和按需实例化。

### `ObjectProvider` 的主要功能

1. **懒加载**：Bean只有在实际需要时才会被实例化。
2. **可选依赖**：如果所请求的Bean不存在，`ObjectProvider`不会抛出异常，而是返回一个空的提供者。
3. **多实例获取**：可以按需获取Bean的多个实例。

### 使用场景

#### 1. 懒加载Bean

假设我们有一个`ExpensiveService`，它的实例化非常耗时。我们希望在需要时才加载它，而不是在应用启动时。

```java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class ExpensiveService {
    public ExpensiveService() {
        System.out.println("ExpensiveService initialized!");
    }

    public void performTask() {
        System.out.println("Performing task...");
    }
}

@Component
public class ServiceBean {
    private final ObjectProvider<ExpensiveService> expensiveServiceProvider;

    public ServiceBean(ObjectProvider<ExpensiveService> expensiveServiceProvider) {
        this.expensiveServiceProvider = expensiveServiceProvider;
    }

    public void useExpensiveService() {
        System.out.println("Using ExpensiveService...");
        ExpensiveService expensiveService = expensiveServiceProvider.getObject();
        expensiveService.performTask();
    }
}
```

在这个例子中，`ExpensiveService`只有在`useExpensiveService`方法被调用时才会被实例化。

#### 2. 可选依赖

如果某个Bean可能不存在，而我们不希望注入失败，可以使用`ObjectProvider`的`ifAvailable`方法。

```java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class OptionalServiceUser {
    private final ObjectProvider<OptionalService> optionalServiceProvider;

    public OptionalServiceUser(ObjectProvider<OptionalService> optionalServiceProvider) {
        this.optionalServiceProvider = optionalServiceProvider;
    }

    public void useOptionalService() {
        optionalServiceProvider.ifAvailable(optionalService -> {
            optionalService.performAction();
        });
    }
}
```

如果`OptionalService`不存在，`useOptionalService`方法将不会执行任何操作。

#### 3. 获取多个实例

使用`ObjectProvider`可以按需获取Bean的多个实例。

```java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class MultiServiceUser {
    private final ObjectProvider<MyService> serviceProvider;

    public MultiServiceUser(ObjectProvider<MyService> serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void useAllServices() {
        serviceProvider.stream().forEach(MyService::performAction);
    }
}
```

### 示例应用

让我们将这些场景整合到一个示例应用中：

```java
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
class MyService {
    public void performAction() {
        System.out.println("Action performed!");
    }
}

@Component
class OptionalService {
    public void performAction() {
        System.out.println("Optional Service Action performed!");
    }
}

@Component
class ExpensiveService {
    public ExpensiveService() {
        System.out.println("ExpensiveService initialized!");
    }

    public void performTask() {
        System.out.println("Performing task...");
    }
}

@Component
class ServiceBean {
    private final ObjectProvider<ExpensiveService> expensiveServiceProvider;

    public ServiceBean(ObjectProvider<ExpensiveService> expensiveServiceProvider) {
        this.expensiveServiceProvider = expensiveServiceProvider;
    }

    public void useExpensiveService() {
        System.out.println("Using ExpensiveService...");
        ExpensiveService expensiveService = expensiveServiceProvider.getObject();
        expensiveService.performTask();
    }
}

@Component
class OptionalServiceUser {
    private final ObjectProvider<OptionalService> optionalServiceProvider;

    public OptionalServiceUser(ObjectProvider<OptionalService> optionalServiceProvider) {
        this.optionalServiceProvider = optionalServiceProvider;
    }

    public void useOptionalService() {
        optionalServiceProvider.ifAvailable(optionalService -> {
            optionalService.performAction();
        });
    }
}

@Component
class MultiServiceUser {
    private final ObjectProvider<MyService> serviceProvider;

    public MultiServiceUser(ObjectProvider<MyService> serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void useAllServices() {
        serviceProvider.stream().forEach(MyService::performAction);
    }
}

@Configuration
@ComponentScan
class AppConfig {
}

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        ServiceBean serviceBean = context.getBean(ServiceBean.class);
        serviceBean.useExpensiveService();

        OptionalServiceUser optionalServiceUser = context.getBean(OptionalServiceUser.class);
        optionalServiceUser.useOptionalService();

        MultiServiceUser multiServiceUser = context.getBean(MultiServiceUser.class);
        multiServiceUser.useAllServices();

        context.close();
    }
}
```

### 总结

`ObjectProvider`是Spring框架中一个强大的工具，特别适用于需要延迟加载、可选依赖和多实例管理的场景。通过`ObjectProvider`，我们可以显著提高应用的灵活性和性能。

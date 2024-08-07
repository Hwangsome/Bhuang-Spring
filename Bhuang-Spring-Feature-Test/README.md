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
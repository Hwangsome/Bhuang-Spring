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

# AOP基础-原生动态代理与Cglib动态代理回顾
在Spring框架中，主要有两种常见的动态代理实现方式：JDK原生动态代理和CGLIB动态代理。每种方式都有其优缺点和适用场景。下面详细讲解这两种动态代理的工作原理、使用方法及其优缺点。

### JDK原生动态代理

#### 原理
JDK原生动态代理依赖于Java的反射机制。它只能代理实现了接口的类。JDK动态代理通过生成一个实现了指定接口的匿名类，在调用方法时拦截并执行代理逻辑。

#### 使用方法
1. **定义接口和实现类**

    ```java
    public interface UserService {
        void addUser(String userName);
    }

    public class UserServiceImpl implements UserService {
        @Override
        public void addUser(String userName) {
            System.out.println("User " + userName + " has been added.");
        }
    }
    ```

2. **定义代理处理器**

    ```java
    import java.lang.reflect.InvocationHandler;
    import java.lang.reflect.Method;

    public class LoggingInvocationHandler implements InvocationHandler {
        private Object target;

        public LoggingInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Before method: " + method.getName());
            Object result = method.invoke(target, args);
            System.out.println("After method: " + method.getName());
            return result;
        }
    }
    ```

3. **创建代理对象并使用**

    ```java
    import java.lang.reflect.Proxy;

    public class Main {
        public static void main(String[] args) {
            UserService userService = new UserServiceImpl();
            LoggingInvocationHandler handler = new LoggingInvocationHandler(userService);

            UserService proxyInstance = (UserService) Proxy.newProxyInstance(
                    userService.getClass().getClassLoader(),
                    userService.getClass().getInterfaces(),
                    handler);

            proxyInstance.addUser("John Doe");
        }
    }
    ```

#### 输出结果
```
Before method: addUser
User John Doe has been added.
After method: addUser
```

#### 优缺点
**优点**
- 简单易用，直接利用Java的反射机制。
- 不需要额外的库，依赖少。

**缺点**
- 只能代理实现了接口的类，无法代理没有实现接口的类。
- 性能相对CGLIB稍逊。

### CGLIB动态代理

#### 原理
CGLIB（Code Generation Library）通过继承目标类并生成目标类的子类来创建代理对象。它可以代理没有实现接口的类。CGLIB使用字节码增强技术生成动态代理类，性能优于JDK动态代理。

#### 使用方法
1. **定义类**

    ```java
    public class UserService {
        public void addUser(String userName) {
            System.out.println("User " + userName + " has been added.");
        }
    }
    ```

2. **定义方法拦截器**

    ```java
    import org.springframework.cglib.proxy.MethodInterceptor;
    import org.springframework.cglib.proxy.MethodProxy;

    import java.lang.reflect.Method;

    public class LoggingMethodInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("Before method: " + method.getName());
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("After method: " + method.getName());
            return result;
        }
    }
    ```

3. **创建代理对象并使用**

    ```java
    import org.springframework.cglib.proxy.Enhancer;

    public class Main {
        public static void main(String[] args) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(UserService.class);
            enhancer.setCallback(new LoggingMethodInterceptor());

            UserService proxy = (UserService) enhancer.create();
            proxy.addUser("John Doe");
        }
    }
    ```

#### 输出结果
```
Before method: addUser
User John Doe has been added.
After method: addUser
```

#### 优缺点
**优点**
- 可以代理没有实现接口的类。
- 性能优于JDK动态代理。

**缺点**
- 需要引入CGLIB库，依赖较多。
- 生成的代理类是目标类的子类，不能代理final类和final方法。

### 比较

| 特性            | JDK动态代理                            | CGLIB动态代理                     |
|-----------------|---------------------------------------|----------------------------------|
| 代理对象        | 需要实现接口                           | 不需要实现接口                    |
| 性能            | 较低                                  | 较高                             |
| 依赖            | 无                                    | 需要CGLIB库                       |
| 代理类限制      | 只能代理接口                           | 不能代理final类和final方法        |
| 代理原理        | 反射机制                               | 字节码生成                        |

### 总结

- **JDK动态代理**：适用于所有的代理目标类都实现了接口的情况，简单直接，不需要额外的库。
- **CGLIB动态代理**：适用于需要代理没有实现接口的类的情况，性能更高，但需要引入额外的库。

根据具体的需求选择合适的代理方式，可以有效地实现面向切面编程、事务管理、权限控制等功能。Spring框架中默认会根据目标类是否实现了接口自动选择合适的代理方式，我们也可以通过配置强制使用某种代理方式。

#  AOP基础-AOP概述与术语
面向切面编程（Aspect-Oriented Programming，AOP）是一种编程范式，用于将横切关注点（cross-cutting concerns）分离出来，这些关注点通常包括日志记录、事务管理、安全性等。AOP通过将这些关注点分离成独立的模块（称为切面），使得业务逻辑代码更加简洁和聚焦。下面详细解释AOP的概念和常用术语。

### AOP概述

AOP的核心思想是通过动态代理或字节码增强技术，在运行时将切面代码织入到目标对象的指定位置，从而实现关注点的分离。AOP常用于增强现有的业务逻辑，而无需修改原有的代码。

### AOP术语

#### 1. 切面（Aspect）
切面是AOP的核心模块，封装了横切关注点的逻辑。切面通常是一个类，其中包含了增强（advice）和切点（pointcut）。

#### 2. 连接点（Join Point）
连接点是程序执行的一个特定位置，如方法调用、异常抛出等。AOP允许在这些连接点上插入增强逻辑。通常，在Spring AOP中，连接点指的是方法执行。

#### 3. 切点（Pointcut）
切点是一个表达式，用于匹配一个或多个连接点。切点定义了在哪些连接点上应用增强。Spring AOP使用AspectJ切点表达式语言来定义切点。

#### 4. 增强（Advice）
增强是切面中的具体动作，在切点处执行。根据执行时间的不同，增强分为以下几种类型：
- **前置增强（Before Advice）**：在目标方法执行前执行。
- **后置增强（After Advice）**：在目标方法执行后执行，无论是否抛出异常。
- **返回增强（After Returning Advice）**：在目标方法成功返回后执行。
- **异常增强（After Throwing Advice）**：在目标方法抛出异常后执行。
- **环绕增强（Around Advice）**：包裹目标方法的执行，能够控制目标方法的执行前后以及是否执行目标方法。

#### 5. 目标对象（Target Object）
目标对象是被增强的对象，即应用了切面的对象。目标对象包含了业务逻辑。

#### 6. 代理对象（Proxy Object）
代理对象是AOP创建的对象，它包含了目标对象和切面逻辑。代理对象在目标对象的方法调用前后织入增强逻辑。

#### 7. 引入（Introduction）
引入允许在不修改代码的情况下为现有类添加新的方法或属性。

### AOP示例

下面通过一个具体的示例演示AOP的基本概念和使用。

#### 示例场景：日志记录

我们希望在执行用户服务的方法前后记录日志。

1. **定义业务接口和实现类**

    ```java
    public interface UserService {
        void addUser(String userName);
    }

    public class UserServiceImpl implements UserService {
        @Override
        public void addUser(String userName) {
            System.out.println("User " + userName + " has been added.");
        }
    }
    ```

2. **定义切面**

    ```java
    import org.aspectj.lang.annotation.After;
    import org.aspectj.lang.annotation.Aspect;
    import org.aspectj.lang.annotation.Before;
    import org.springframework.stereotype.Component;

    @Aspect
    @Component
    public class LoggingAspect {

        @Before("execution(* com.example.UserService.addUser(..))")
        public void logBefore() {
            System.out.println("Before adding user");
        }

        @After("execution(* com.example.UserService.addUser(..))")
        public void logAfter() {
            System.out.println("After adding user");
        }
    }
    ```

3. **配置Spring**

    ```xml
    <!-- applicationContext.xml -->
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xsi:schemaLocation="http://www.springframework.org/schema/beans 
                               http://www.springframework.org/schema/beans/spring-beans.xsd
                               http://www.springframework.org/schema/aop 
                               http://www.springframework.org/schema/aop/spring-aop.xsd">

        <!-- 扫描组件 -->
        <context:component-scan base-package="com.example" />

        <!-- 启用AOP -->
        <aop:aspectj-autoproxy />

        <!-- 定义UserService bean -->
        <bean id="userService" class="com.example.UserServiceImpl" />
    </beans>
    ```

4. **测试**

    ```java
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;

    public class Main {
        public static void main(String[] args) {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            UserService userService = context.getBean(UserService.class);
            userService.addUser("John Doe");
        }
    }
    ```

#### 输出结果

```
Before adding user
User John Doe has been added.
After adding user
```

### 解析

1. **切面（Aspect）**：`LoggingAspect` 类是一个切面，它包含了两个增强方法 `logBefore` 和 `logAfter`。
2. **连接点（Join Point）**：在本例中，连接点是 `addUser` 方法的执行。
3. **切点（Pointcut）**：`execution(* com.example.UserService.addUser(..))` 是一个切点表达式，匹配 `UserService` 的 `addUser` 方法。
4. **增强（Advice）**：`logBefore` 和 `logAfter` 方法分别是前置增强和后置增强。
5. **目标对象（Target Object）**：`UserServiceImpl` 是目标对象。
6. **代理对象（Proxy Object）**：Spring AOP 创建的 `UserService` 代理对象，在调用 `addUser` 方法时，会执行切面中的增强逻辑。

通过AOP，我们可以在不修改业务代码的情况下，实现横切关注点（如日志记录）的分离和增强，从而提高代码的可维护性和可扩展性。

## 面试中如何概述AOP
在面试中对AOP（面向切面编程）的概述可以简明扼要，但也要足够全面，展示出对该概念的深刻理解。下面是一个面试中可能使用的AOP概述：

---

### 面向切面编程（AOP）概述

面向切面编程（AOP，Aspect-Oriented Programming）是一种编程范式，旨在将横切关注点（cross-cutting concerns）从业务逻辑中分离出来。这些关注点包括日志记录、事务管理、权限控制等，它们通常会散布在多个模块中，导致代码的重复和难以维护。AOP通过定义切面（Aspect）将这些关注点模块化，以提高代码的可维护性和复用性。

### 关键术语

1. **切面（Aspect）**：
   切面是AOP的核心模块，封装了横切关注点的逻辑。它通常由一个类来实现，其中包含了增强（Advice）和切点（Pointcut）。

2. **连接点（Join Point）**：
   连接点是程序执行的一个特定位置，如方法调用、异常抛出等。在Spring AOP中，连接点主要指方法的执行。

3. **切点（Pointcut）**：
   切点是一个表达式，用于匹配一个或多个连接点。它定义了在什么位置应用增强。

4. **增强（Advice）**：
   增强是切面中的具体操作，在切点处执行。根据执行时间不同，分为前置增强、后置增强、返回增强、异常增强和环绕增强。

5. **目标对象（Target Object）**：
   目标对象是被增强的对象，包含了业务逻辑。

6. **代理对象（Proxy Object）**：
   代理对象是AOP创建的对象，它包含了目标对象和切面逻辑。在目标对象的方法调用前后织入增强逻辑。

### AOP在Spring中的应用

Spring框架通过动态代理和CGLIB实现AOP。它能够自动选择合适的代理方式来为目标对象生成代理对象。具体应用包括：

- **日志记录**：在方法调用前后记录日志。
- **事务管理**：在方法调用前开启事务，方法调用成功后提交事务，出现异常时回滚事务。
- **权限控制**：在方法调用前检查权限。

### AOP示例

```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature().getName());
    }

    @After("execution(* com.example.UserService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getSignature().getName());
    }
}
```

在这个示例中，`LoggingAspect` 是一个切面，它在 `UserService` 的所有方法执行前后分别记录日志。通过这样的方式，我们可以将日志记录与业务逻辑分离，保持代码的简洁和聚焦。

---

# 基于注解的AOP配置
在Spring中，基于注解的AOP配置是一种常见且便捷的方式来实现面向切面编程（AOP）。通过注解，可以更直观地将横切关注点（如日志记录、事务管理等）应用到业务逻辑中。以下是如何使用注解进行AOP配置的详细步骤和示例。

### 步骤1：引入依赖

确保你的项目已经引入了Spring AOP的相关依赖。如果使用Maven，`pom.xml`文件中需要添加以下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 步骤2：定义业务接口和实现类

创建一个简单的业务接口和实现类，用于示例AOP应用。

```java
public interface UserService {
    void addUser(String userName);
}

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("User " + userName + " has been added.");
    }
}
```

### 步骤3：定义切面类

使用`@Aspect`注解定义一个切面类，并使用其他AOP注解定义增强方法。

```java
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.UserService.addUser(..))")
    public void logBefore() {
        System.out.println("Before adding user");
    }

    @After("execution(* com.example.UserService.addUser(..))")
    public void logAfter() {
        System.out.println("After adding user");
    }
}
```

### 步骤4：配置Spring

使用Java配置类或XML配置文件启用AOP功能和组件扫描。

#### Java配置类

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### XML配置文件

如果你更喜欢使用XML配置，可以创建一个Spring配置文件（如`applicationContext.xml`）：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop 
                           http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 启用AOP -->
    <aop:aspectj-autoproxy />

    <!-- 扫描组件 -->
    <context:component-scan base-package="com.example" />

    <!-- 定义UserService bean -->
    <bean id="userService" class="com.example.UserServiceImpl" />
</beans>
```

### 步骤5：测试AOP配置

创建一个测试类来验证AOP配置是否正确。

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = context.getBean(UserService.class);
        userService.addUser("John Doe");
    }
}
```

### 运行结果

运行上述测试类，应该会看到以下输出：

```
Before adding user
User John Doe has been added.
After adding user
```

### 解析

1. **注解`@Aspect`**：标记一个类为切面类。
2. **注解`@Component`**：将切面类声明为Spring组件，便于Spring容器管理。
3. **注解`@Before`和`@After`**：定义了前置增强和后置增强，指定在`addUser`方法执行前后分别执行日志记录。
4. **注解`@EnableAspectJAutoProxy`**：启用基于注解的AOP代理支持。
5. **`@ComponentScan`**：配置Spring扫描指定包中的组件。

通过这种方式，我们可以轻松地将AOP切面应用到业务逻辑中，实现横切关注点的分离，增强代码的可维护性和可扩展性。

## 环绕通知
### 环绕通知详细介绍

环绕通知（Around Advice）是AOP中功能最强大的通知类型。它能够在目标方法执行的前后插入自定义逻辑，并且可以完全控制目标方法的执行，包括决定是否执行目标方法、修改目标方法的输入参数和返回值。

#### 环绕通知的核心概念

- **ProceedingJoinPoint**：环绕通知方法的参数类型是`ProceedingJoinPoint`，它是`JoinPoint`的子接口。`ProceedingJoinPoint`提供了`proceed()`方法，用于执行目标方法。
- **控制目标方法的执行**：通过调用`proceed()`方法可以执行目标方法，也可以不调用它从而阻止目标方法的执行。
- **修改参数和返回值**：在调用`proceed()`方法之前，可以修改目标方法的参数；在调用之后，可以修改目标方法的返回值。

#### 环绕通知的实现步骤

1. **定义业务接口和实现类**
2. **定义切面类**
3. **配置Spring**
4. **编写单元测试**

### 示例实现

#### 1. 定义业务接口和实现类

**UserService.java**
```java
package com.example;

public interface UserService {
    void addUser(String userName);
}
```

**UserServiceImpl.java**
```java
package com.example;

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("User " + userName + " has been added.");
    }
}
```

#### 2. 定义切面类

**LoggingAspect.java**
```java
package com.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.UserService.addUser(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around before adding user");

        // 获取并修改参数
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof String) {
            args[0] = ((String) args[0]).toUpperCase();
        }

        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed(args);

        long endTime = System.currentTimeMillis();
        System.out.println("Around after adding user");
        System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds");

        // 修改返回值（如果有的话）
        return result;
    }
}
```

#### 3. 配置Spring

**AppConfig.java**
```java
package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### 4. 编写单元测试

**UserServiceTest.java**
```java
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);

        // 捕获标准输出流
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddUser() {
        userService.addUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("Around before adding user"));
        assertTrue(output.contains("User JOHN DOE has been added."));
        assertTrue(output.contains("Around after adding user"));
        assertTrue(output.contains("Method execution time"));
    }
}
```

### 解析

1. **环绕通知方法**：
   - 使用`@Around`注解标记，环绕通知的方法必须接收一个`ProceedingJoinPoint`参数。
   - 在执行目标方法之前，打印日志并修改方法参数。
   - 调用`proceed(args)`方法执行目标方法，可以传递修改后的参数。
   - 在执行目标方法之后，打印日志并测量方法执行时间。

2. **修改参数和返回值**：
   - 在调用`proceed()`方法之前，可以修改目标方法的参数。
   - 调用`proceed(args)`方法时，传递修改后的参数。
   - 可以在调用`proceed()`方法之后修改返回值（如果目标方法有返回值）。

### 总结

环绕通知是AOP中最灵活和强大的通知类型，通过`ProceedingJoinPoint`可以完全控制目标方法的执行，包括修改输入参数、返回值以及在方法执行前后插入自定义逻辑。通过这种方式，可以实现非常复杂和灵活的增强逻辑，满足各种业务需求。

## AspectJ注解抽取
在AOP开发中，特别是使用AspectJ注解时，可能会遇到多个切点定义重复的情况。为了使代码更加整洁和可维护，可以将这些切点抽取到一个单独的方法中，通过注解引用这些抽取的方法来实现复用。

### 抽取切点表达式

#### 场景

假设我们有一个`UserService`，并且我们希望在所有的`addUser`和`deleteUser`方法执行前后进行日志记录。

#### 1. 定义业务接口和实现类

**UserService.java**
```java
package com.example;

public interface UserService {
    void addUser(String userName);
    void deleteUser(String userName);
}
```

**UserServiceImpl.java**
```java
package com.example;

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("User " + userName + " has been added.");
    }

    @Override
    public void deleteUser(String userName) {
        System.out.println("User " + userName + " has been deleted.");
    }
}
```

#### 2. 定义切面类

我们将切点表达式抽取到一个单独的方法中，并通过其他注解引用它。

**LoggingAspect.java**
```java
package com.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 抽取切点表达式
    @Pointcut("execution(* com.example.UserService.*User(..))")
    public void userServiceMethods() {}

    @Before("userServiceMethods()")
    public void logBefore() {
        System.out.println("Before method execution");
    }

    @After("userServiceMethods()")
    public void logAfter() {
        System.out.println("After method execution");
    }

    @Around("userServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around before method execution");

        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        System.out.println("Around after method execution");
        System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds");

        return result;
    }
}
```

在这个例子中：
- `@Pointcut`注解用于定义一个切点表达式方法`userServiceMethods()`，该方法没有实现，只是一个标识方法。
- 其他通知（`@Before`、`@After`、`@Around`）通过方法引用（`userServiceMethods()`）来使用这个切点表达式。

#### 3. 配置Spring

**AppConfig.java**
```java
package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### 4. 编写单元测试

**UserServiceTest.java**
```java
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);

        // 捕获标准输出流
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddUser() {
        userService.addUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("Around before method execution"));
        assertTrue(output.contains("User John Doe has been added."));
        assertTrue(output.contains("Around after method execution"));
        assertTrue(output.contains("Method execution time"));
        assertTrue(output.contains("Before method execution"));
        assertTrue(output.contains("After method execution"));
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("Around before method execution"));
        assertTrue(output.contains("User John Doe has been deleted."));
        assertTrue(output.contains("Around after method execution"));
        assertTrue(output.contains("Method execution time"));
        assertTrue(output.contains("Before method execution"));
        assertTrue(output.contains("After method execution"));
    }
}
```

### 解析

1. **抽取切点表达式**：
   - 使用`@Pointcut`注解定义一个切点方法`userServiceMethods()`，表示匹配`UserService`中所有以`User`结尾的方法。
   - 其他通知（`@Before`、`@After`、`@Around`）通过引用这个切点方法来实现复用。

2. **环绕通知**：
   - `logAround`方法在目标方法执行前后分别记录日志，并测量方法执行时间。


通过这种方式，我们可以更方便地管理和复用切点表达式，使代码更加简洁和易于维护。

## @annotation的使用 
在Spring AOP中，`@annotation`用于匹配带有特定注解的方法。通过这种方式，我们可以更灵活地应用切面逻辑，而无需依赖方法名或方法签名。下面是一个使用`@annotation`的详细示例。

### 示例场景：基于注解的日志记录

假设我们希望为带有特定注解的方法添加日志记录功能。我们可以定义一个自定义注解，并在切面类中使用`@annotation`匹配该注解。

#### 1. 定义自定义注解

**LogExecutionTime.java**
```java
package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogExecutionTime {
}
```

#### 2. 定义业务接口和实现类

**UserService.java**
```java
package com.example;

public interface UserService {
    void addUser(String userName);
    void deleteUser(String userName);
}
```

**UserServiceImpl.java**
```java
package com.example;

public class UserServiceImpl implements UserService {
    @Override
    @LogExecutionTime
    public void addUser(String userName) {
        System.out.println("User " + userName + " has been added.");
    }

    @Override
    public void deleteUser(String userName) {
        System.out.println("User " + userName + " has been deleted.");
    }
}
```

在这个示例中，我们在`addUser`方法上使用了自定义注解`@LogExecutionTime`，而`deleteUser`方法没有使用该注解。

#### 3. 定义切面类

**LoggingAspect.java**
```java
package com.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(com.example.LogExecutionTime)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around before method execution: " + joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        System.out.println("Around after method execution: " + joinPoint.getSignature().getName());
        System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds");

        return result;
    }
}
```

在这个切面类中，我们使用了`@Around`注解和`@annotation`表达式来匹配带有`@LogExecutionTime`注解的方法。`logAround`方法会在这些方法执行的前后记录日志，并测量方法执行时间。

#### 4. 配置Spring

**AppConfig.java**
```java
package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### 5. 编写单元测试

**UserServiceTest.java**
```java
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);

        // 捕获标准输出流
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddUser() {
        userService.addUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("Around before method execution: addUser"));
        assertTrue(output.contains("User John Doe has been added."));
        assertTrue(output.contains("Around after method execution: addUser"));
        assertTrue(output.contains("Method execution time"));
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertFalse(output.contains("Around before method execution: deleteUser"));
        assertTrue(output.contains("User John Doe has been deleted."));
        assertFalse(output.contains("Around after method execution: deleteUser"));
        assertFalse(output.contains("Method execution time"));
    }
}
```

### 解释

1. **自定义注解**：
   - `@LogExecutionTime`注解用于标记需要记录执行时间的方法。

2. **切面类**：
   - `@Around("@annotation(com.example.LogExecutionTime)")`表示匹配带有`@LogExecutionTime`注解的方法。
   - `logAround`方法在这些方法执行前后记录日志，并测量方法执行时间。


通过使用`@annotation`，我们可以灵活地应用AOP切面逻辑到带有特定注解的方法，避免硬编码方法签名，使代码更加清晰和易于维护。

## 切面类的通知方法参数 
在Spring AOP中，通知方法可以接受多种类型的参数，这些参数提供了关于连接点和目标方法的信息。通过这些参数，可以实现更复杂和灵活的切面逻辑。以下是常见的通知方法参数及其详细解释。

### 常见的通知方法参数

1. **JoinPoint**
2. **ProceedingJoinPoint**
3. **@Before、@After、@Around 等注解中的切点表达式**
4. **@Pointcut 定义的切点表达式**

### 详细解释

#### 1. JoinPoint

`JoinPoint`接口提供了对当前连接点状态和目标方法的访问。适用于所有类型的通知（`@Before`、`@After`、`@AfterReturning`、`@AfterThrowing`）。

**常用方法**：
- `Object[] getArgs()`：获取目标方法的参数。
- `Signature getSignature()`：获取被增强方法的签名（方法名称、修饰符、参数等）。
- `Object getTarget()`：获取目标对象（即被代理对象）。
- `Object getThis()`：获取代理对象自身。

在Spring AOP中，默认情况下，代理类的toString方法不会被增强，因此this和target在调用toString方法时输出相同。这是因为Spring AOP通常使用动态代理（JDK动态代理或CGLIB）来创建代理对象，而这些代理对象的toString方法默认行为是调用原始目标对象的toString方法。

**示例**：
```java
@Before("execution(* com.example.UserService.*(..))")
public void logBefore(JoinPoint joinPoint) {
    System.out.println("Method name: " + joinPoint.getSignature().getName());
    System.out.println("Arguments: " + Arrays.toString(joinPoint.getArgs()));
}
```

#### 2. ProceedingJoinPoint

`ProceedingJoinPoint`是`JoinPoint`的子接口，仅适用于环绕通知（`@Around`）。它提供了`proceed()`方法，用于执行目标方法。

**常用方法**：
- `Object proceed()`：执行目标方法。
- `Object proceed(Object[] args)`：执行目标方法，使用新的参数。

**示例**：
```java
@Around("execution(* com.example.UserService.*(..))")
public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("Method name: " + joinPoint.getSignature().getName());
    System.out.println("Arguments: " + Arrays.toString(joinPoint.getArgs()));

    long startTime = System.currentTimeMillis();

    // 执行目标方法
    Object result = joinPoint.proceed();

    long endTime = System.currentTimeMillis();
    System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds");

    return result;
}
```

#### 3. @Before、@After、@Around 等注解中的切点表达式

切点表达式用于匹配特定的连接点，通常通过`execution`、`within`、`@annotation`等方式来定义。

**示例**：
```java
@Before("execution(* com.example.UserService.*(..))")
public void logBefore(JoinPoint joinPoint) {
    System.out.println("Before method: " + joinPoint.getSignature().getName());
}

@AfterReturning(pointcut = "execution(* com.example.UserService.*(..))", returning = "result")
public void logAfterReturning(JoinPoint joinPoint, Object result) {
    System.out.println("After method: " + joinPoint.getSignature().getName());
    System.out.println("Method returned: " + result);
}

@AfterThrowing(pointcut = "execution(* com.example.UserService.*(..))", throwing = "error")
public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
    System.out.println("Method exception: " + joinPoint.getSignature().getName());
    System.out.println("Exception: " + error);
}
```

#### 4. @Pointcut 定义的切点表达式

`@Pointcut`注解用于定义可复用的切点表达式，其他通知通过引用这些切点表达式来应用切面逻辑。

**示例**：
```java
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.UserService.*(..))")
    public void userServiceMethods() {}

    @Before("userServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature().getName());
    }

    @After("userServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getSignature().getName());
    }

    @Around("userServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Around before method: " + joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        System.out.println("Around after method: " + joinPoint.getSignature().getName());
        System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds");

        return result;
    }
}
```

## 多个切面的执行顺序
在Spring AOP中，如果有多个切面（Aspects）应用到同一个连接点（Join Point），它们的执行顺序是可控的。可以通过实现`org.springframework.core.Ordered`接口或者使用`@Order`注解来定义切面的执行顺序。

### 控制切面执行顺序

#### 1. 使用 `@Order` 注解

`@Order`注解用于指定切面的优先级。值越小，优先级越高，切面会越早执行。

#### 2. 实现 `Ordered` 接口

切面类可以实现`org.springframework.core.Ordered`接口，并覆盖`getOrder`方法来指定优先级。

### 示例

假设我们有两个切面，一个用于日志记录，另一个用于权限验证。我们希望先进行权限验证，然后再记录日志。

#### 1. 定义业务接口和实现类

**UserService.java**
```java
package com.example;

public interface UserService {
    void addUser(String userName);
    void deleteUser(String userName);
}
```

**UserServiceImpl.java**
```java
package com.example;

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("User " + userName + " has been added.");
    }

    @Override
    public void deleteUser(String userName) {
        System.out.println("User " + userName + " has been deleted.");
    }

    @Override
    public String toString() {
        return "UserServiceImpl instance";
    }
}
```

#### 2. 定义切面类

**LoggingAspect.java**
```java
package com.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)  // 低优先级
public class LoggingAspect {

    @Around("execution(* com.example.UserService.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("LoggingAspect: Around before method: " + joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        System.out.println("LoggingAspect: Around after method: " + joinPoint.getSignature().getName());
        System.out.println("LoggingAspect: Method execution time: " + (endTime - startTime) + " milliseconds");

        return result;
    }
}
```

**SecurityAspect.java**
```java
package com.example;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)  // 高优先级
public class SecurityAspect {

    @Around("execution(* com.example.UserService.*(..))")
    public Object checkSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("SecurityAspect: Checking security before method: " + joinPoint.getSignature().getName());

        // 进行权限验证逻辑
        // ...

        Object result = joinPoint.proceed();

        System.out.println("SecurityAspect: Checking security after method: " + joinPoint.getSignature().getName());

        return result;
    }
}
```

在这个示例中，`SecurityAspect`的优先级高于`LoggingAspect`，因此权限验证会先于日志记录执行。

#### 3. 配置Spring

**AppConfig.java**
```java
package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### 4. 编写单元测试

**UserServiceTest.java**
```java
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);

        // 捕获标准输出流
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddUser() {
        userService.addUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("SecurityAspect: Checking security before method: addUser"));
        assertTrue(output.contains("LoggingAspect: Around before method: addUser"));
        assertTrue(output.contains("User John Doe has been added."));
        assertTrue(output.contains("LoggingAspect: Around after method: addUser"));
        assertTrue(output.contains("LoggingAspect: Method execution time"));
        assertTrue(output.contains("SecurityAspect: Checking security after method: addUser"));
    }
}
```

### 解析

1. **@Order 注解**：`@Order(1)`表示高优先级，`@Order(2)`表示低优先级。优先级高的切面先执行。
2. **切面执行顺序**：`SecurityAspect`的`checkSecurity`方法先执行，随后是`LoggingAspect`的`logAround`方法。
3. **单元测试**：通过捕获标准输出验证切面执行顺序是否符合预期。

### 结论

通过使用`@Order`注解或实现`Ordered`接口，我们可以精确控制多个切面的执行顺序，确保切面按照预期的顺序应用到目标方法上。这样可以实现更复杂的逻辑，保证切面间的依赖关系和执行顺序。

## 代理对象调用自身的方法
在Spring AOP中，代理对象调用自身的方法时，切面不会被应用到被调用的方法上。这是因为Spring AOP是基于代理的，而代理对象调用自身的方法不会通过代理对象来调用，而是直接调用目标对象的方法。因此，任何配置的切面都不会生效。

### 场景解释

假设我们有一个`UserService`类，其中包含两个方法：`addUser`和`deleteUser`。我们希望在这两个方法执行前后记录日志。如果在`addUser`方法中调用`deleteUser`方法，那么`deleteUser`方法上的切面将不会生效。

### 代码示例

#### 1. 定义业务接口和实现类

**UserService.java**
```java
package com.example;

public interface UserService {
    void addUser(String userName);
    void deleteUser(String userName);
}
```

**UserServiceImpl.java**
```java
package com.example;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("Adding user: " + userName);
        deleteUser(userName); // 调用自身方法
    }

    @Override
    public void deleteUser(String userName) {
        System.out.println("Deleting user: " + userName);
    }

    @Override
    public String toString() {
        return "UserServiceImpl instance";
    }
}
```

#### 2. 定义切面类

**LoggingAspect.java**
```java
package com.example;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class LoggingAspect {

    @Before("execution(* com.example.UserService.*(..))")
    public void logBefore() {
        System.out.println("Before method execution");
    }

    @After("execution(* com.example.UserService.*(..))")
    public void logAfter() {
        System.out.println("After method execution");
    }
}
```

#### 3. 配置Spring

**AppConfig.java**
```java
package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
```

#### 4. 编写单元测试

**UserServiceTest.java**
```java
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        userService = context.getBean(UserService.class);

        // 捕获标准输出流
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testAddUser() {
        userService.addUser("John Doe");
        
        // 验证日志输出
        String output = outContent.toString();
        assertTrue(output.contains("Before method execution")); // for addUser
        assertTrue(output.contains("Adding user: John Doe"));
        assertFalse(output.contains("Before method execution")); // for deleteUser (should not be there)
        assertTrue(output.contains("Deleting user: John Doe"));
        assertFalse(output.contains("After method execution")); // for deleteUser (should not be there)
        assertTrue(output.contains("After method execution")); // for addUser
    }
}
```

### 解析

1. **业务实现类**：
   - 在`addUser`方法中调用了自身的`deleteUser`方法。
   - 由于是直接调用，因此`deleteUser`方法上的切面不会生效。

2. **切面类**：
   - 使用`@Before`和`@After`注解分别在方法执行前后记录日志。

3. **单元测试**：
   - 测试中调用`addUser`方法，验证日志输出。
   - 预期在调用`deleteUser`方法前后不会记录日志，因为切面没有生效。

### 解决方法

为了确保在代理对象调用自身方法时，切面依然生效，可以通过以下方法解决：

#### 1. 通过`AopContext`获取当前代理对象

Spring提供了`AopContext`类，可以在方法内部获取当前代理对象，从而确保切面生效。

**UserServiceImpl.java**
```java
package com.example;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String userName) {
        System.out.println("Adding user: " + userName);
        ((UserService) AopContext.currentProxy()).deleteUser(userName); // 调用代理对象的方法
    }

    @Override
    public void deleteUser(String userName) {
        System.out.println("Deleting user: " + userName);
    }

    @Override
    public String toString() {
        return "UserServiceImpl instance";
    }
}
```

#### Spring aop中的 @EnableAspectJAutoProxy exposeProxy的作用
`@EnableAspectJAutoProxy` 是 Spring AOP 中的一个注解，用于启用基于 AspectJ 的自动代理功能。在使用 AOP 时，Spring 自动为标记了切面的类生成代理，以便在方法调用时能够正确地执行切面逻辑。这个注解提供了一个可选的 `exposeProxy` 属性，它在某些高级场景下非常有用。

### `@EnableAspectJAutoProxy` 注解

- **基本作用**：`@EnableAspectJAutoProxy` 用于启用基于 AspectJ 的 AOP 代理。Spring AOP 通过代理对象拦截方法调用，并在适当的时候应用切面逻辑。

- **典型使用**：你可以在 Spring 配置类上使用这个注解，以便在应用程序中启用 AOP 功能。

### `exposeProxy` 属性

- **作用**：`exposeProxy` 属性用于控制是否在当前线程中公开代理对象。当设置为 `true` 时，Spring 会在当前线程的 `ThreadLocal` 中暴露当前代理对象，这使得你可以在同一个类内部的方法调用中访问到代理对象，并通过它触发切面逻辑。

- **默认值**：`exposeProxy` 的默认值是 `false`，即代理对象不会在 `ThreadLocal` 中公开。

- **设置为 `true` 的场景**：
   - **自调用**：当一个类的方法调用该类的另一个方法时，通常不会触发 AOP，因为 AOP 依赖于代理对象，而在自调用时，调用的是同一个类的普通方法，而不是代理对象的方法。如果你想在这种情况下触发 AOP，需要设置 `exposeProxy = true`，并使用 `AopContext.currentProxy()` 来获取当前的代理对象。
   - **事务管理**：在事务管理场景中，如果一个事务性方法调用同一个类中的另一个事务性方法，也可能需要触发事务管理逻辑，此时可以使用 `exposeProxy`。

### 使用示例

假设有一个服务类 `MyService`，其中两个方法 `methodA` 和 `methodB` 都有切面逻辑。`methodA` 调用 `methodB` 时，想要触发 `methodB` 的切面逻辑，你可以使用 `exposeProxy`。

**配置类**

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AppConfig {
    // 其他 Bean 配置
}
```

**服务类**

```java
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    public void methodA() {
        System.out.println("Inside methodA");
        // 通过代理对象调用 methodB 以触发 AOP
        ((MyService) AopContext.currentProxy()).methodB();
    }

    public void methodB() {
        System.out.println("Inside methodB");
    }
}
```

**切面类**

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    @Before("execution(* com.example.MyService.methodB(..))")
    public void beforeMethodB() {
        System.out.println("Before methodB");
    }
}
```

### 运行结果

当 `methodA` 调用 `methodB` 时，由于 `exposeProxy` 设置为 `true`，`methodB` 的切面逻辑将会触发：

```
Inside methodA
Before methodB
Inside methodB
```

### 总结

- **`@EnableAspectJAutoProxy`**：用于启用基于 AspectJ 的自动代理功能。
- **`exposeProxy = true`**：在 `ThreadLocal` 中暴露当前代理对象，以便在同类自调用时使用代理对象触发 AOP 逻辑。
- **典型场景**：主要用于需要在类的内部方法调用中触发 AOP 切面逻辑的场景，如事务管理、自调用等。

使用 `exposeProxy` 时要注意性能和复杂性，因为这会略微增加代理的开销和代码的复杂度。

#### 使用ApplicationContextAware获取代理对象
是的，你可以使用 `ApplicationContextAware` 接口来获取 `ApplicationContext`，然后通过 `ApplicationContext` 获取当前类的代理对象，从而实现同类内部方法调用时触发 AOP 逻辑的效果。这种方法是另一种替代使用 `exposeProxy` 属性的方式。

### 使用 `ApplicationContextAware` 实现 AOP 代理对象的获取

以下是如何通过实现 `ApplicationContextAware` 接口来获取当前类的代理对象的步骤和示例代码。

### 1. 实现 `ApplicationContextAware` 接口

首先，在你的服务类中实现 `ApplicationContextAware` 接口，以便获取 `ApplicationContext`。

**服务类**

```java
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class MyService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void methodA() {
        System.out.println("Inside methodA");

        // 从 ApplicationContext 获取代理对象
        MyService proxy = (MyService) applicationContext.getBean(MyService.class);
        proxy.methodB();
    }

    public void methodB() {
        System.out.println("Inside methodB");
    }
}
```

### 2. 配置切面类

保持切面类不变，确保在 `methodB` 调用时触发 AOP 逻辑。

**切面类**

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    @Before("execution(* com.example.MyService.methodB(..))")
    public void beforeMethodB() {
        System.out.println("Before methodB");
    }
}
```

### 3. 运行结果

当你运行 `methodA` 时，通过 `ApplicationContext` 获取的 `MyService` 代理对象调用 `methodB`，会触发 AOP 切面逻辑。

**控制台输出**：

```
Inside methodA
Before methodB
Inside methodB
```

### 总结

- **`ApplicationContextAware` 实现**：通过实现 `ApplicationContextAware` 接口，获取 `ApplicationContext`，然后通过 `ApplicationContext` 获取当前类的代理对象，实现类内部方法调用时触发 AOP 逻辑。

- **代理对象的使用**：这种方式的核心是通过 `applicationContext.getBean(MyService.class)` 获取当前类的代理对象，并在同类内部调用时使用该代理对象。

- **与 `exposeProxy` 的比较**：使用 `ApplicationContextAware` 的方式不需要 `exposeProxy`，但需要通过上下文获取代理对象，这可能稍微增加代码的复杂性。`exposeProxy` 则是通过 `ThreadLocal` 机制直接访问当前代理对象，适用于一些性能敏感的场景。

这种方法在复杂的 Spring 应用中可能更灵活，因为它允许你在不同的上下文中使用代理对象，尤其是在没有直接使用 `AopContext.currentProxy()` 的情况下。

通过以上解决方法，代理对象调用自身方法时，切面依然能够生效，确保日志记录等切面逻辑正确执行。

#### 为什么我实现了ApplicationContextAware 就可以拿到ApplicationContext？
当你实现了 `ApplicationContextAware` 接口时，Spring 会自动将 `ApplicationContext` 注入到你的 Bean 中。这是因为 Spring 框架内部有一个 `ApplicationContextAwareProcessor`，它负责检测并处理实现了 `ApplicationContextAware` 接口的 Bean。

### 1. `ApplicationContextAware` 接口

`ApplicationContextAware` 是 Spring 的一个标记接口，定义了一个 `setApplicationContext(ApplicationContext applicationContext)` 方法。实现这个接口的任何 Spring Bean 都会在初始化时获得一个 `ApplicationContext` 实例。

- **接口定义**：
  ```java
  public interface ApplicationContextAware {
      void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
  }
  ```

- **作用**：实现该接口的类可以在 Spring 容器启动时获取 `ApplicationContext` 实例。这使得你的类能够访问 Spring 上下文中的其他 Bean 或使用 Spring 的功能。

### 2. `ApplicationContextAwareProcessor`

`ApplicationContextAwareProcessor` 是 Spring 的一个内部处理器，它的作用是在 Spring 容器初始化 Bean 时，检测是否实现了 `ApplicationContextAware` 或其他类似的 Aware 接口（如 `BeanNameAware`、`BeanFactoryAware` 等），如果实现了这些接口，它会在 Bean 初始化过程中调用相应的方法。

- **内部工作原理**：

   - 当 Spring 容器创建和初始化 Bean 时，`ApplicationContextAwareProcessor` 会检查该 Bean 是否实现了 `ApplicationContextAware` 接口。
   - 如果实现了 `ApplicationContextAware` 接口，Spring 会调用 `setApplicationContext` 方法，将 `ApplicationContext` 实例传递给该 Bean。

- **简化流程**：

   1. **Bean 定义加载**：Spring 从配置文件或注解中加载 Bean 定义。
   2. **Bean 实例化**：Spring 创建 Bean 的实例。
   3. **`ApplicationContextAwareProcessor` 处理**：在 Bean 初始化过程中，Spring 检测 Bean 是否实现了 `ApplicationContextAware` 接口。如果是，调用 `setApplicationContext` 方法，注入 `ApplicationContext`。
   4. **Bean 初始化完成**：Spring 完成其他 Bean 初始化过程（例如依赖注入、调用 `InitializingBean` 等）。

- **关键代码**：

  `ApplicationContextAwareProcessor` 主要的工作发生在其 `postProcessBeforeInitialization` 方法中：

  ```java
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
      if (bean instanceof ApplicationContextAware) {
          ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
      }
      return bean;
  }
  ```

  这个方法会在 Bean 初始化之前被调用，将 `ApplicationContext` 注入给实现了 `ApplicationContextAware` 接口的 Bean。

### 3. 使用 `ApplicationContextAware` 的示例

通过实现 `ApplicationContextAware`，你可以在任何 Spring Bean 中获取 `ApplicationContext`，例如：

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MyService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void doSomething() {
        // 使用 applicationContext 获取其他 Bean 或执行其他操作
        MyOtherService myOtherService = applicationContext.getBean(MyOtherService.class);
        myOtherService.performTask();
    }
}
```

### 4. 总结

- **为什么可以获取 `ApplicationContext`**：因为 Spring 在创建和初始化 Bean 时，通过 `ApplicationContextAwareProcessor` 处理器自动检测实现了 `ApplicationContextAware` 接口的 Bean，并在初始化过程中将 `ApplicationContext` 注入到 Bean 中。

- **`ApplicationContextAwareProcessor` 的作用**：该处理器确保所有实现了 `ApplicationContextAware` 接口的 Bean 都能够在初始化时获得 `ApplicationContext`，从而能够在运行时访问 Spring 容器中的其他 Bean 或资源。

这种机制使得 Spring 的依赖注入更加灵活，允许 Bean 在初始化时动态获取和使用 Spring 容器中的其他组件。

## AOP实现事务控制




# @EnableAspectJAutoProxy （AOP 源码理解）
## Advisor 
在Spring框架中，`Advisor`是Spring AOP（面向切面编程）中的一个核心概念。`Advisor`将"通知"（advice）与"切入点"（pointcut）结合在一起，是Spring AOP机制中执行横切关注点的基本构件。简而言之，`Advisor`定义了在何处（切入点）和何时（通知）执行某些横切逻辑。
对于 Aspect 切面类中定义的通知方法，方法体 + 方法上的通知注解就可以看做一个 Advisor 增强器。
### `Advisor`的组成

1. **通知（Advice）**：
   - `Advice`是指横切关注点的具体实现，即要在目标方法执行之前、之后或抛出异常时执行的代码。Spring AOP中有多种类型的通知，如`Before Advice`、`After Advice`、`Around Advice`等。
   - 在Spring中，`Advice`通常是一个实现了`MethodInterceptor`接口或类似接口的类。

2. **切入点（Pointcut）**：
   - `Pointcut`定义了哪些连接点（即方法）应该应用通知。它通过切入点表达式来指定，例如使用AspectJ表达式来选择一组方法。
   - 在Spring中，`Pointcut`通常是一个实现了`Pointcut`接口的类，包含了两个方法：`getClassFilter()`和`getMethodMatcher()`。

### `Advisor`的作用

`Advisor`将`Advice`与`Pointcut`结合，形成一个完整的横切逻辑。每个`Advisor`包含以下两个部分：

- 一个`Pointcut`：定义在哪些连接点（方法）上应用横切逻辑。
- 一个`Advice`：定义具体的横切逻辑。

当Spring AOP在运行时创建代理对象时，`Advisor`被用来决定哪个通知在什么地方执行。




#  IOC基础-注解驱动IOC与组件扫描

在 Spring 框架中，注解驱动的 IoC（Inversion of Control，控制反转）和组件扫描（Component Scanning）是核心功能，用于自动发现和注册应用程序上下文中的 Spring bean。

### 注解驱动的 IoC

注解驱动的 IoC 是通过使用注解来配置和管理 Spring 容器中的 bean。这种方法相对于 XML 配置更加简洁和直观。常用的注解包括：

- `@Component`: 标记一个类为 Spring 的组件类，它是一个通用的 Stereotype 注解。其他特定的注解如 `@Controller`、`@Service`、`@Repository` 也是 `@Component` 的变种。

- `@Controller`: 标记一个类为 Spring MVC 控制器。

- `@Service`: 标记一个类为服务类（通常是业务逻辑层）。

- `@Repository`: 标记一个类为数据访问层的类，通常用于 DAO 层。

- `@Autowired`: 自动注入依赖，默认按类型装配，可以与 `@Qualifier` 配合使用按名称装配。

- `@Value`: 用于注入属性值。

- `@Configuration`: 标记一个类为配置类，用于定义 bean。

- `@Bean`: 定义在 `@Configuration` 类中的方法，返回一个 bean，并注册到 Spring 容器中。

- `@Scope`: 指定 bean 的作用范围，如 singleton、prototype 等。

### 组件扫描

组件扫描用于自动发现和注册带有特定注解的类为 Spring bean。组件扫描的核心配置是 `@ComponentScan` 注解。以下是如何使用组件扫描的步骤：

1. **启用组件扫描**: 在配置类上使用 `@ComponentScan` 注解，指定要扫描的包。

    ```java
    @Configuration
    @ComponentScan(basePackages = "com.example.project")
    public class AppConfig {
    }
    ```

2. **定义组件**: 在需要被 Spring 管理的类上使用 `@Component` 或其他派生注解。

    ```java
    @Service
    public class UserService {
        // 业务逻辑
    }
    ```

3. **自动装配依赖**: 使用 `@Autowired` 注解将依赖注入到 bean 中。

    ```java
    @Component
    public class UserController {

        @Autowired
        private UserService userService;

        public void handleRequest() {
            userService.performAction();
        }
    }
    ```

### 示例代码

下面是一个完整的示例，展示了注解驱动的 IoC 与组件扫描的使用：

**1. 配置类**

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.project")
public class AppConfig {
}
```

**2. 组件类**

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void performAction() {
        System.out.println("UserService action performed");
    }
}
```

**3. 控制器类**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public void handleRequest() {
        userService.performAction();
    }
}
```

**4. 主应用类**

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserController userController = context.getBean(UserController.class);
        userController.handleRequest();
    }
}
```

### 运行效果

当你运行 `MainApp` 类时，Spring 会自动扫描 `com.example.project` 包及其子包中的所有类，并注册带有 `@Component`、`@Service`、`@Controller` 等注解的类为 Spring bean。`UserController` 的 `userService` 属性会被自动注入，并调用 `performAction` 方法。

### 总结

通过注解驱动的 IoC 和组件扫描，Spring 提供了一种更为简洁和强大的方式来管理和配置应用程序的组件。这种方法不仅减少了 XML 配置文件的使用，还提高了代码的可读性和维护性。

在 Spring 框架中，IoC 容器的核心功能是管理应用程序的 bean，包括它们的创建、配置和依赖注入。Spring 提供了两种主要的配置方式：注解驱动的 IoC 容器和 XML 驱动的 IoC 容器。以下是对这两种配置方式的详细讲述，包括它们的优缺点和使用示例。

## 注解驱动的 IoC 容器

注解驱动的 IoC 容器利用 Java 注解来定义和管理 Spring bean。主要的注解包括 `@Component`、`@Controller`、`@Service`、`@Repository`、`@Autowired`、`@Value`、`@Configuration` 和 `@Bean` 等。

### 优点

1. **简洁性**: 注解使得配置更加简洁，避免了冗长的 XML 配置文件。
2. **类型安全**: 注解基于 Java 类型系统，可以在编译时检测错误。
3. **强大的元数据支持**: 注解可以提供丰富的元数据信息，有助于自动配置和组件扫描。

### 缺点

1. **紧耦合**: 注解与代码耦合在一起，可能增加代码的复杂性。
2. **灵活性不足**: 某些复杂的配置场景可能难以通过注解实现。

### 使用示例

**1. 配置类**

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.project")
public class AppConfig {
}
```

**2. 组件类**

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void performAction() {
        System.out.println("UserService action performed");
    }
}
```

**3. 控制器类**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public void handleRequest() {
        userService.performAction();
    }
}
```

**4. 主应用类**

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserController userController = context.getBean(UserController.class);
        userController.handleRequest();
    }
}
```

## XML 驱动的 IoC 容器

XML 驱动的 IoC 容器使用 XML 配置文件来定义和管理 Spring bean。这种方式是 Spring 早期版本的主要配置方式。

### 优点

1. **分离配置和代码**: 配置文件与代码分离，便于管理和维护。
2. **灵活性**: XML 提供了高度的灵活性，适用于各种复杂的配置场景。
3. **无侵入性**: 不需要在代码中添加任何 Spring 特定的注解。

### 缺点

1. **冗长**: XML 配置文件可能会变得非常冗长，难以维护。
2. **类型安全性差**: XML 配置是字符串格式，编译时无法检测错误。

### 使用示例

**1. XML 配置文件（beans.xml）**

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <context:component-scan base-package="com.example.project"/>

    <bean id="userService" class="com.example.project.UserService"/>

    <bean id="userController" class="com.example.project.UserController">
        <property name="userService" ref="userService"/>
    </bean>
</beans>
```

**2. 组件类**

```java
public class UserService {
    public void performAction() {
        System.out.println("UserService action performed");
    }
}
```

**3. 控制器类**

```java
public class UserController {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void handleRequest() {
        userService.performAction();
    }
}
```

**4. 主应用类**

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserController userController = context.getBean("userController", UserController.class);
        userController.handleRequest();
    }
}
```

### 对比

| 特性                     | 注解驱动的 IoC 容器                        | XML 驱动的 IoC 容器                           |
|------------------------|-------------------------------------|------------------------------------------|
| 配置方式                   | 使用注解                              | 使用 XML 配置文件                          |
| 配置与代码的耦合               | 紧耦合                               | 松耦合                                    |
| 代码简洁性                  | 高                                  | 低                                       |
| 类型安全性                  | 高                                  | 低                                       |
| 灵活性                     | 适用于简单和中等复杂度的配置                 | 适用于各种复杂配置场景                          |
| 可读性和可维护性               | 高（对于简单配置）                       | 随着配置文件增长，可读性和可维护性可能下降             |

### 总结
注解驱动的 IoC 容器和 XML 驱动的 IoC 容器各有优缺点，选择哪种方式主要取决于项目的具体需求和开发团队的偏好。在实际应用中，可以根据需要结合使用这两种配置方式，以便在获得最佳性能和灵活性的同时简化配置和维护。

## 注解驱动与xml驱动互通
在实际的 Spring 项目中，可以灵活地将注解驱动和 XML 驱动的配置方式结合使用，以充分利用两者的优势。以下是如何实现注解驱动与 XML 驱动的互通的详细讲解和示例。

### 1. 启用注解驱动的配置

首先，我们需要在 XML 配置文件中启用注解驱动的配置。这可以通过在 XML 配置文件中添加 `<context:annotation-config />` 和 `<context:component-scan />` 元素来实现。

**XML 配置文件（beans.xml）**

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context 
           http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 启用注解驱动 -->
    <context:annotation-config />

    <!-- 扫描指定包中的注解组件 -->
    <context:component-scan base-package="com.example.project" />

    <!-- 定义 XML 配置的 bean -->
    <bean id="xmlConfiguredBean" class="com.example.project.XmlConfiguredBean">
        <property name="property" value="Value from XML" />
    </bean>

</beans>
```

### 2. 定义注解组件

然后，我们可以在代码中定义使用注解的组件，例如使用 `@Component`、`@Service`、`@Controller` 等注解。

**示例注解组件类**

```java
package com.example.project;

import org.springframework.stereotype.Service;

@Service
public class AnnotatedService {
    public void performService() {
        System.out.println("Service performed by AnnotatedService");
    }
}
```

### 3. 使用 XML 配置的 bean 和注解配置的 bean

我们可以在注解组件中注入 XML 配置的 bean，或者在 XML 配置的 bean 中注入注解配置的 bean。

**注解组件使用 XML 配置的 bean**
在注解配置类上使用 @ImportResource 注解，指定 XML 配置文件的路径。
```java
package com.example.project;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = "com.example.project")
@ImportResource("classpath:beans.xml")
public class AppConfig {
}
```

**XML 配置的 bean 使用注解配置的 bean**

```java
package com.example.project;

public class XmlConfiguredBean {

    private AnnotatedService annotatedService;

    public void setAnnotatedService(AnnotatedService annotatedService) {
        this.annotatedService = annotatedService;
    }

    public String getProperty() {
        return "some property value";
    }

    public void performAction() {
        annotatedService.performService();
    }
}
```

在 XML 配置文件中，定义 `AnnotatedService` 的注入：

```xml
<bean id="xmlConfiguredBean" class="com.example.project.XmlConfiguredBean">
    <property name="annotatedService" ref="annotatedService" />
</bean>
```

### 4. 主应用类

最后，在主应用类中，我们可以加载 XML 配置文件，并使用注解配置的上下文。

**主应用类**

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        AnnotatedController controller = context.getBean(AnnotatedController.class);
        controller.handleRequest();

        XmlConfiguredBean xmlBean = context.getBean(XmlConfiguredBean.class);
        xmlBean.performAction();
    }
}
```

### 总结

通过以上步骤，我们实现了注解驱动与 XML 驱动的互通：

1. 在 XML 配置文件中启用注解驱动并进行组件扫描。
2. 定义注解组件和 XML 配置的 bean。
3. 在注解组件中注入 XML 配置的 bean，或者在 XML 配置的 bean 中注入注解配置的 bean。
4. 在主应用类中加载 XML 配置文件，并使用 Spring 上下文获取 bean。

这种混合配置方式可以让我们在享受注解驱动简洁性和类型安全性的同时，保留 XML 配置的灵活性和无侵入性。

## 组件扫描
在 Spring 框架中，组件扫描（Component Scanning）是一个核心功能，它允许 Spring 自动发现和注册应用程序上下文中的 Spring bean。这种机制使得开发者不再需要手动配置每一个 bean，从而大大简化了应用程序的配置。以下是关于 Spring 组件扫描的详细讲解。

### 1. 组件扫描的概念

组件扫描是 Spring 的一种自动化机制，它通过扫描指定的包及其子包中的类，寻找特定的注解（如 `@Component`、`@Service`、`@Repository`、`@Controller` 等），并将这些类注册为 Spring 容器中的 bean。

### 2. 常用注解

- **`@Component`**: 标记一个类为 Spring 组件，这个类会被自动检测并注册为 Spring bean。
- **`@Service`**: `@Component` 的一个特化，标记一个类为服务层组件。
- **`@Repository`**: `@Component` 的一个特化，标记一个类为数据访问层组件。
- **`@Controller`**: `@Component` 的一个特化，标记一个类为 Spring MVC 控制器。

这些注解的作用本质上是相同的，只是为了语义清晰，将不同用途的组件区分开来。

### 3. 启用组件扫描

#### 使用 XML 配置

在 XML 配置中，可以使用 `<context:component-scan>` 元素启用组件扫描。需要指定 `base-package` 属性来定义要扫描的包。

**示例：**

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/context 
           http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 启用组件扫描 -->
    <context:component-scan base-package="com.example.project" />

</beans>
```

#### 使用 Java 配置

在 Java 配置中，可以使用 `@ComponentScan` 注解启用组件扫描。需要在配置类上标注 `@ComponentScan` 并指定 `basePackages` 属性。

**示例：**

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.project")
public class AppConfig {
}
```

### 4. 扫描范围

组件扫描会扫描指定包及其子包中的所有类，寻找带有 `@Component`、`@Service`、`@Repository`、`@Controller` 等注解的类，并将这些类注册为 Spring bean。

### 5. 使用过滤器

在某些情况下，可能需要排除或包括特定的类，可以通过使用过滤器来实现。

#### 使用 XML 配置过滤器

可以使用 `include-filter` 和 `exclude-filter` 元素来包括或排除特定的类。

**示例：**

```xml
<context:component-scan base-package="com.example.project">
    <!-- 排除带有 @Controller 注解的类 -->
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
    <!-- 仅包含带有 @Service 注解的类 -->
    <context:include-filter type="annotation" expression="org.springframework.stereotype.Service" />
</context:component-scan>
```

#### 使用 Java 配置过滤器

可以在 `@ComponentScan` 注解中使用 `includeFilters` 和 `excludeFilters` 属性来实现。

**示例：**

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Configuration
@ComponentScan(basePackages = "com.example.project",
               includeFilters = @Filter(Service.class),
               excludeFilters = @Filter(Controller.class))
public class AppConfig {
}
```

### 6. 自定义注解

有时候，我们可能需要定义自己的注解，并使用组件扫描机制来注册带有自定义注解的类为 Spring bean。可以通过 `@Component` 注解的组合来实现这一点。

**示例：**

```java
import org.springframework.stereotype.Component;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CustomComponent {
}
```

然后在类上使用自定义注解：

```java
@CustomComponent
public class CustomService {
    public void performService() {
        System.out.println("Service performed by CustomService");
    }
}
```

### 7. 实例示例

下面是一个完整的实例，展示了如何使用组件扫描来自动发现和注册 Spring bean。

**项目结构：**

```
src
└── main
    └── java
        └── com
            └── example
                └── project
                    ├── AppConfig.java
                    ├── MainApp.java
                    ├── service
                    │   └── UserService.java
                    └── controller
                        └── UserController.java
```

**AppConfig.java**

```java
package com.example.project;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.project")
public class AppConfig {
}
```

**UserService.java**

```java
package com.example.project.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void performAction() {
        System.out.println("UserService action performed");
    }
}
```

**UserController.java**

```java
package com.example.project.controller;

import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public void handleRequest() {
        userService.performAction();
    }
}
```

**MainApp.java**

```java
package com.example.project;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.example.project.controller.UserController;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserController userController = context.getBean(UserController.class);
        userController.handleRequest();
    }
}
```

### 总结

通过组件扫描，Spring 可以自动发现和注册应用程序中的 bean，简化了配置工作。组件扫描可以通过 XML 配置或 Java 配置启用，并支持使用过滤器来包括或排除特定的类。开发者还可以定义自定义注解，并使用组件扫描机制来注册带有自定义注解的类为 Spring bean。组件扫描机制极大地提高了开发效率和代码的可维护性。
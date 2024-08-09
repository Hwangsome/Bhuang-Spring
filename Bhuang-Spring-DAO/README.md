# Dao编程基础-事务回顾&原生jdbc事务

## 事务回顾
事务有四个基本特性，简称为 **ACID**，分别是：

1. **原子性（Atomicity）**：
    - **定义**：事务是一个不可分割的工作单元，事务中的操作要么全部成功，要么全部失败。如果事务中任何一个操作失败，已经执行的操作也必须回滚到初始状态，就像这个事务从未发生过一样。
    - **示例**：假设你有一个银行转账操作，从账户A转账100元到账户B。这个事务包括两个操作：从账户A扣除100元，以及将100元添加到账户B。如果其中一个操作失败，整个事务都必须回滚，保证账户A和账户B的余额不会受到影响。

2. **一致性（Consistency）**：
    - **定义**：事务在执行前后，数据库都必须处于一致的状态。具体而言，事务开始前数据库处于一致的状态，事务结束后数据库也必须保持一致状态。事务的执行不能破坏数据库的完整性约束（如主键、外键约束）。
    - **示例**：在银行转账的例子中，假设总资金是200元，事务开始前账户A有100元，账户B有100元。无论事务如何执行，最终总资金应该还是200元，数据库的状态必须满足这个一致性规则。

3. **隔离性（Isolation）**：
    - **定义**：多个事务同时进行时，彼此之间是相互隔离的，一个事务的执行不能被其他事务干扰。不同的事务之间的操作不会相互影响。
    - **示例**：假设两个事务同时从账户A转账，一个事务从账户A转出50元，另一个事务从账户A转出30元。如果事务A和事务B都未完成，彼此应该看不到对方的操作结果。只有当事务A和事务B都提交后，最终账户A的余额才能正确反映。

   隔离性级别有4种：未提交读（Read Uncommitted）、已提交读（Read Committed）、可重复读（Repeatable Read）和串行化（Serializable）。

4. **持久性（Durability）**：
    - **定义**：一旦事务提交，事务对数据库的修改就会永久保存下来，即使系统出现故障，事务的结果也不会丢失。
    - **示例**：假设事务成功将100元从账户A转移到账户B，并提交了事务，即使此时系统崩溃或出现电源故障，事务结果仍然应该被保存，账户A减少了100元，账户B增加了100元。

### 总结

- **原子性**：事务中的所有操作要么全部执行成功，要么全部不执行。
- **一致性**：事务开始和结束时，数据库都处于一致的状态。
- **隔离性**：多个并发事务之间互不干扰。
- **持久性**：事务一旦提交，其结果将永久保存。

## 事务的并发问题
在数据库的并发操作中，多个事务同时执行时，由于资源竞争或事务之间的相互影响，可能会导致一些并发问题。这些问题主要包括以下几种：

### 1. **脏读（Dirty Read）**

**描述**：脏读是指一个事务读取了另一个事务尚未提交的数据。如果该事务回滚，则读取的数据将是无效的，从而导致数据的不一致性。

**示例**：
- 事务A修改了一个数据并且未提交，事务B读取了这个数据。随后事务A回滚了操作，那么事务B读取的数据就是无效的“脏数据”。

### 2. **不可重复读（Non-Repeatable Read）**

**描述**：在同一个事务中，两次读取同一条记录，结果却不同。通常这是因为在两次读取之间，另一个事务修改了该记录并提交了。

**示例**：
- 事务A第一次读取了一条记录的值为100，随后事务B修改了这条记录的值为200并提交。当事务A再次读取这条记录时，值变成了200。这种现象就是不可重复读。

### 3. **幻读（Phantom Read）**

**描述**：幻读是指在同一个事务中，两次查询返回的结果集不同。通常是因为在两次查询之间，另一个事务插入了新的数据，使得第二次查询返回了“幻影”般的新记录。

**示例**：
- 事务A执行了一次查询，得到5条记录。事务B随后插入了几条符合事务A查询条件的新记录。当事务A再次执行相同的查询时，结果集变成了8条记录，这种现象称为幻读。

### 4. **丢失更新（Lost Update）**

**描述**：两个事务同时读取同一条记录，并基于读取的数据分别进行修改。由于事务的并发，后提交的事务会覆盖前一个事务的修改，导致前一个事务的更新被丢失。

**示例**：
- 事务A和事务B都读取同一条记录，值为100。事务A将值修改为200并提交，事务B将读取的值加50修改为150并提交。最终的结果是值变为150，而事务A的修改被丢失了。

### 解决并发问题的隔离级别

为了避免这些并发问题，数据库系统提供了不同的隔离级别来控制事务之间的交互。这些隔离级别由低到高分别是：

1. **未提交读（Read Uncommitted）**：
    - 允许脏读、不可重复读和幻读。
    - 这是最低的隔离级别，事务可以读取未提交的数据。

2. **已提交读（Read Committed）**：
    - 允许不可重复读和幻读，但不允许脏读。
    - 这是大多数数据库的默认隔离级别，事务只能读取已提交的数据。

3. **可重复读（Repeatable Read）**：
    - 允许幻读，但不允许脏读和不可重复读。
    - 在事务期间，其他事务不能修改或删除该事务已经读取的记录。

4. **串行化（Serializable）**：
    - 不允许脏读、不可重复读和幻读。
    - 这是最高的隔离级别，所有事务顺序执行，完全隔离。

每个隔离级别在处理并发问题时有不同的表现和性能影响。通常，选择隔离级别时需要在数据一致性和系统性能之间进行权衡。例如，使用“可重复读”可以避免不可重复读的问题，但在高并发场景下可能会导致性能下降。

### 实际应用

在实际应用中，不同的事务隔离级别适用于不同的场景：

- **Read Uncommitted**：几乎不会使用，因为允许脏读可能导致数据不一致。
- **Read Committed**：适用于大多数应用程序，尤其是在高并发场景下，它在避免脏读的同时保持了较好的性能。
- **Repeatable Read**：适用于需要强一致性的场景，如银行系统，以避免不可重复读的问题。
- **Serializable**：适用于需要绝对一致性的场景，但由于性能开销大，通常只在关键任务中使用。

通过合理选择事务隔离级别，可以有效解决数据库并发操作中的常见问题，确保数据的一致性和完整性。

## 原生jdbc事务的操作
在使用原生JDBC时，事务操作是通过`java.sql.Connection`接口来控制的。以下是使用原生JDBC进行事务管理的基本步骤和示例。

### 1. **获取数据库连接**

首先，需要从数据源或驱动管理器获取一个数据库连接。通常，使用`DriverManager`或数据源来获取连接。

```java
Connection connection = null;
try {
    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdatabase", "username", "password");
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 2. **关闭自动提交模式**

在默认情况下，JDBC的`Connection`是自动提交模式，这意味着每条SQL语句执行后都会立即提交。为了使用事务，必须关闭自动提交模式。

```java
try {
    connection.setAutoCommit(false);
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 3. **执行SQL操作**

在关闭自动提交模式后，你可以执行一系列的SQL操作，这些操作将在同一个事务中执行。

```java
try {
    // 执行第一条SQL语句
    PreparedStatement statement1 = connection.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = ?");
    statement1.setInt(1, 1);
    statement1.executeUpdate();
    
    // 执行第二条SQL语句
    PreparedStatement statement2 = connection.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = ?");
    statement2.setInt(1, 2);
    statement2.executeUpdate();
    
    // ... 继续执行其他SQL操作
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 4. **提交事务**

如果所有的SQL操作都成功执行，则可以提交事务，将所有的更改永久保存到数据库中。

```java
try {
    connection.commit();
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 5. **回滚事务**

如果在执行SQL操作的过程中发生了异常或错误，为了保证数据的一致性，你可以选择回滚事务，撤销所有未提交的更改。

```java
try {
    connection.rollback();
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 6. **关闭连接**

最后，关闭数据库连接以释放资源。关闭连接时，未提交的事务会被自动回滚。

```java
try {
    if (connection != null) {
        connection.close();
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 完整示例

以下是一个完整的JDBC事务管理的示例：

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcTransactionExample {

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // 1. 获取数据库连接
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdatabase", "username", "password");

            // 2. 关闭自动提交模式
            connection.setAutoCommit(false);

            // 3. 执行SQL操作
            PreparedStatement statement1 = connection.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = ?");
            statement1.setInt(1, 1);
            statement1.executeUpdate();

            PreparedStatement statement2 = connection.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = ?");
            statement2.setInt(1, 2);
            statement2.executeUpdate();

            // 4. 提交事务
            connection.commit();
            System.out.println("Transaction committed successfully.");

        } catch (SQLException e) {
            try {
                // 5. 回滚事务
                if (connection != null) {
                    connection.rollback();
                    System.out.println("Transaction rolled back due to an error.");
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // 6. 关闭连接
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException closeException) {
                closeException.printStackTrace();
            }
        }
    }
}
```

### 事务管理注意事项

1. **异常处理**：务必在SQL操作中处理异常，并在异常发生时回滚事务，以确保数据的一致性。
2. **自动提交模式**：默认情况下，JDBC是自动提交模式。使用事务时必须将自动提交关闭。
3. **多操作原子性**：在一个事务中执行的所有操作，要么全部成功，要么全部失败，保持数据的一致性。

通过上述步骤，你可以使用原生JDBC来实现事务管理，从而保证数据库操作的原子性、一致性、隔离性和持久性（ACID）。

## 事务的保存点
在数据库事务处理中，**保存点**（Savepoint）是一种机制，允许在事务执行过程中设置一个或多个回滚点。通过使用保存点，你可以在事务内部将数据库的状态保存到某个特定点，以便在发生错误时，只回滚到这个保存点，而不必回滚整个事务。

### 使用保存点的典型场景

保存点特别适用于以下场景：

1. **部分回滚**：在一个事务中执行多步操作，如果某一步失败，你希望只回滚到这一操作之前，而不影响已经成功的操作。
2. **复杂的事务逻辑**：当事务逻辑复杂，包含多个步骤，有些步骤可能会失败，但希望继续执行事务的其他部分。

### 使用保存点的步骤

1. **创建保存点**：使用`Connection`对象的`setSavepoint()`方法创建一个保存点。
2. **回滚到保存点**：如果某个操作失败，可以使用`rollback(Savepoint savepoint)`方法回滚到指定的保存点。
3. **释放保存点**：一旦保存点不再需要，可以使用`releaseSavepoint(Savepoint savepoint)`方法释放它，以便于管理资源。

### 代码示例

以下是一个使用保存点的JDBC事务管理示例：

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.sql.SQLException;

public class JdbcSavepointExample {

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // 1. 获取数据库连接
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdatabase", "username", "password");

            // 2. 关闭自动提交模式
            connection.setAutoCommit(false);

            // 3. 执行第一条SQL操作
            PreparedStatement statement1 = connection.prepareStatement("UPDATE accounts SET balance = balance - 100 WHERE id = ?");
            statement1.setInt(1, 1);
            statement1.executeUpdate();

            // 4. 创建保存点
            Savepoint savepoint1 = connection.setSavepoint("Savepoint1");

            // 5. 执行第二条SQL操作
            PreparedStatement statement2 = connection.prepareStatement("UPDATE accounts SET balance = balance + 100 WHERE id = ?");
            statement2.setInt(1, 2);
            statement2.executeUpdate();

            // 6. 模拟发生异常，回滚到保存点
            if (true) {
                throw new SQLException("Simulated error");
            }

            // 7. 提交事务
            connection.commit();
            System.out.println("Transaction committed successfully.");

        } catch (SQLException e) {
            try {
                // 8. 回滚到保存点
                if (connection != null) {
                    connection.rollback(savepoint1);
                    System.out.println("Rolled back to savepoint.");
                }

                // 9. 继续其他操作或直接提交剩余的事务
                connection.commit();
                System.out.println("Transaction committed successfully after rollback.");

            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            // 10. 关闭连接
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException closeException) {
                closeException.printStackTrace();
            }
        }
    }
}
```

### 解释

1. **创建保存点**：
    - 使用`connection.setSavepoint("Savepoint1")`创建一个名为`Savepoint1`的保存点。
    - 该保存点记录了当前事务的状态。

2. **回滚到保存点**：
    - 当捕获到异常时，使用`connection.rollback(savepoint1)`回滚到指定的保存点`Savepoint1`。
    - 这样，所有在保存点之后的更改都被撤销，但保存点之前的操作不会被回滚。

3. **提交事务**：
    - 如果没有异常发生或异常处理完毕后，可以继续提交剩余的事务。
    - 你也可以选择不提交，或根据逻辑继续进行其他操作。

4. **释放保存点**：
    - 一旦保存点不再需要，你可以通过`connection.releaseSavepoint(savepoint1)`释放它。

### 注意事项

- **保存点的使用**：保存点的使用会增加事务的复杂性，尤其是在涉及多个保存点时。因此，在实际应用中应谨慎使用保存点。
- **性能影响**：频繁使用保存点可能会影响性能，尤其是在大量事务操作中，因此应根据实际需求使用保存点。
- **回滚范围**：回滚到保存点只会影响从保存点开始的事务操作，保存点之前的操作不会受到影响。

通过使用保存点，你可以更加精细地控制事务，确保在复杂的业务逻辑中既保证数据的一致性，又不必完全回滚整个事务。

# Dao编程基础-编程式事务

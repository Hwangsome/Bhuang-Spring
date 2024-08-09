package com.bhuang.utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * docker run --name aop -e MYSQL_ROOT_PASSWORD=root -p 33010:3306 -d mysql
 *
 */
object JdbcUtils {
    init {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }

    private const val JDBC_URL = "jdbc:mysql://localhost:33010/test?characterEncoding=utf8"

    /**
     * 切面里怎么拿到 service 方法中正在使用的 Connection 呢？而且两个 Dao 方法中获取的 Connection 也都是全新的，这个问题怎么解决为好呢？小伙伴们可以开动脑筋想一下有没有什么之间学过的东西能让两个 Dao 的方法执行期间，只有一个 Connection ？
     * 既然是在同一个方法中执行，那就一定是同一个线程咯？那是不是可以用一下 ThreadLocal 呀！使用 ThreadLocal ，可以实现一个线程中的对象资源共享！
     */
    private val connectionThreadLocal = ThreadLocal<Connection>()

    @JvmStatic
    fun getConnection(): Connection {
        // ThreadLocal中有，直接取出返回
        connectionThreadLocal.get()?.let {
            return it
        }
        // 没有，则创建新的，并放入ThreadLocal中
        val connection: Connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, "root", "root")
            connectionThreadLocal.set(connection)
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
        return connection
    }

    fun openConnection(): Connection {
        val connection: Connection
        try {
            connection = DriverManager.getConnection(JDBC_URL, "root", "root")
            connectionThreadLocal.set(connection)
        } catch (e: SQLException) {
            throw java.lang.RuntimeException(e)
        }
        return connection
    }

    @JvmStatic
    fun remove() {
        connectionThreadLocal.remove()
    }
}

package com.bhuang.aspect

import com.bhuang.utils.JdbcUtils.getConnection
import com.bhuang.utils.JdbcUtils.remove
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.sql.Connection


@Component
@Aspect
class TransactionAspect {

    @Around("@annotation(com.bhuang.annos.Transactional)")
    @Throws(Throwable::class)
    fun doWithTransaction(joinPoint: ProceedingJoinPoint): Any {
        val connection: Connection = getConnection()
        // 开启事务
        connection.autoCommit = false
        return try {
            val retval = joinPoint.proceed()
            // 方法执行成功，提交事务
            connection.commit()
            retval
        } catch (e: Throwable) {
            // 方法出现异常，回滚事务
            connection.rollback()
            throw e
        } finally {
            // 最后关闭连接，释放资源
            remove()
        }
    }
}
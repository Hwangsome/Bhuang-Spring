package com.bhuang.dao

import com.bhuang.utils.JdbcUtils
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

@Repository
class FinanceDao {

    fun addMoney(id: Long, money: Int) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = JdbcUtils.getConnection()
            preparedStatement = connection
                .prepareStatement("update tbl_employee set salary = salary + ? where id = ?")
            preparedStatement.setInt(1, money)
            preparedStatement.setLong(2, id)
            preparedStatement.executeUpdate()

        } catch (e: SQLException) {
            throw RuntimeException(e)
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    fun subtractMoney(id: Long, money: Int) {
        try {
            val connection: Connection = JdbcUtils.getConnection()
            val preparedStatement: PreparedStatement = connection
                .prepareStatement("update tbl_employee set salary = salary - ? where id = ?")
            preparedStatement.setInt(1, money)
            preparedStatement.setLong(2, id)
            preparedStatement.executeUpdate()
            preparedStatement.close()
            connection.close()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }
}

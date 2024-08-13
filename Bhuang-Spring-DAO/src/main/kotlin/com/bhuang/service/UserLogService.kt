package com.bhuang.service

import com.bhuang.dao.UserLogDao
import com.bhuang.model.UserEntity
import com.bhuang.model.UserLogEntity
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
class UserLogService @Autowired constructor(
    private val userLogDao: UserLogDao
){

    companion object {
        private val logger = KLogging().logger()
    }

    @Transactional(propagation = Propagation.NEVER)
    fun logUserCreation( user: UserEntity) {
        println(TransactionSynchronizationManager.isActualTransactionActive())
        println(TransactionSynchronizationManager.getCurrentTransactionName())
        val userLog = UserLogEntity(
            name = user.name,
            age = user.age,
            email = user.email
        )
        userLogDao.save(userLog)
        println("Logged INSERT action for user: at ${userLog.createdAt}")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logUserUpdate(newUser: UserEntity, oldUser: UserEntity) {
        val userLog = UserLogEntity(
            name = newUser.name,
            age = newUser.age,
            email = newUser.email
        )
        userLogDao.save(userLog)
        println("Logged UPDATE action for user: ${newUser.id} at ${userLog.createdAt}")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logUserDeletion(deletedUser: UserEntity) {
        val userLog = UserLogEntity(
            name = deletedUser.name,
            age = deletedUser.age,
            email = deletedUser.email
        )
        userLogDao.save(userLog)
        println("Logged DELETE action for user: ${deletedUser.id} at ${userLog.createdAt}")
    }
}
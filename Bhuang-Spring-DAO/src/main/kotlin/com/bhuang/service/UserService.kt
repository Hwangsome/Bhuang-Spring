package com.bhuang.service

import com.bhuang.dao.UserDao
import com.bhuang.model.UserEntity
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate

@Service
class UserService @Autowired constructor(
    private val userDao: UserDao,
    private val transactionTemplate: TransactionTemplate,
    private val eventPublisher:ApplicationEventPublisher,
    private val userLogService: UserLogService
) {

    companion object {
        private val logger = KLogging().logger()
    }

    fun addUser(userEntity: UserEntity):Int {
        val size = userDao.addUser(
            userEntity
        )
        // 模拟异常
        val i = 1 / 0
        // 上面虽然有异常，但是因为没有事务的控制，所以数据已经插入到数据库中了
        userDao.findUserList()
        return size
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun findUserById(id: Int): UserEntity? {
        println(TransactionSynchronizationManager.getCurrentTransactionName())
        return userDao.findUserById(id)
    }

    fun addUserWithTransaction(userEntity: UserEntity) = transactionTemplate.execute {
        userDao.addUser(userEntity)
        // 模拟异常
        val i = 1 / 0
        // 虽然有异常发生，但是因为有事务的控制，所以数据不会插入到数据库中
        val findUserList = userDao.findUserList()

        findUserList.map {
         logger.info { "user: $it" }
        }
        findUserList.size

    }

    @Transactional
    fun addUserWithTransactionAnno(userEntity: UserEntity) :Int {
        val size = userDao.addUser(
            userEntity
        )
        // 模拟异常
     //   val i = 1 / 0
        // 上面虽然有异常，但是因为没有事务的控制，所以数据已经插入到数据库中了
        userDao.findUserList()
        eventPublisher.publishEvent(userEntity)
        return size
    }

    // 测试事务的传播行为, 添加user的时候，先检查是否存在这个user, 存在就不添加，不存在才添加
     @Transactional(propagation = Propagation.REQUIRED)
    fun addUserByTestPropagation(userEntity: UserEntity) :Int{
        return try {
            userDao.addUser(userEntity)
            // 记录插入操作日志
            userLogService.logUserCreation(userEntity)
            1
        } catch (e: Exception) {
            logger.error { "插入失败: ${e.message}" }
            0
        }
    }

}
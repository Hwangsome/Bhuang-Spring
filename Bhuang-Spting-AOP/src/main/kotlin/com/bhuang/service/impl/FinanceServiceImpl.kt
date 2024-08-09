package com.bhuang.service.impl

import com.bhuang.dao.FinanceDao
import com.bhuang.service.FinanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FinanceServiceImpl @Autowired constructor (
    private val financeDao: FinanceDao
): FinanceService {
    override fun transfer(source: Long, target: Long, money: Int) {
        financeDao.subtractMoney(source, money)
        financeDao.addMoney(target, money)
    }
}
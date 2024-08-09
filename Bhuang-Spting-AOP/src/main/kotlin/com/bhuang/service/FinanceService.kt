package com.bhuang.service

interface FinanceService {
    fun transfer(source: Long, target: Long, money: Int)
}
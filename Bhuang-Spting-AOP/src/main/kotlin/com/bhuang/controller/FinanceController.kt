package com.bhuang.controller

import com.bhuang.service.FinanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FinanceController @Autowired constructor(
    private val financeService: FinanceService
){
    @GetMapping("/finance")
    fun getFinance(): String {
        financeService.transfer(1, 2, 100)
        return "OK"
    }

}
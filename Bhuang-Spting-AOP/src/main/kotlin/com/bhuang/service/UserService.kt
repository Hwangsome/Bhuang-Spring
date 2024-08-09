package com.bhuang.service

interface UserService {
    fun addUser(name: String)

    fun getUser(name: String): String
}
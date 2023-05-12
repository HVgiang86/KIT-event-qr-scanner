package com.kitclub.kiteventqrscanner.controller

import com.kitclub.kiteventqrscanner.utils.AESHelper

object LoginController {
    var encryptedPassword = ""
    private const val SALT = "bt9CXj5VyC"
    private const val adminEncryptedPassword = "WBb4O9SqnW8oOwvK0J35tDP8VEElWvv3SXY4YHWlHBg="

    fun checkLogin(password: String): Boolean {
        val encrypted = AESHelper.encrypt(password + SALT)
        if (encrypted == encryptedPassword)
            return true

        if (encrypted == adminEncryptedPassword)
            return true

        return false
    }

    fun checkAdminPassword(password: String): Boolean {
        val encrypted = AESHelper.encrypt(password + SALT)
        if (encrypted == adminEncryptedPassword)
            return true

        return false
    }
}
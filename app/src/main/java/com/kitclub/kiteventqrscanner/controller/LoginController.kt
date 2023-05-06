package com.kitclub.kiteventqrscanner.controller

import com.kitclub.kiteventqrscanner.utils.AESHelper

object LoginController {
    var encryptedPassword = ""
    private const val SALT = "bt9CXj5VyC"

    fun checkLogin(password: String): Boolean {
        val encrypted = AESHelper.encrypt(password + SALT)
        if (encrypted == encryptedPassword)
            return true
        return false
    }


}
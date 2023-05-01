package com.kitclub.kiteventqrscanner.utils

import android.util.Log
import java.nio.charset.Charset

object LoginVerifier {
    var encryptedPassword = ""
    private const val SALT = "bt9CXj5VyC"

    fun checkLogin(password: String): Boolean {
        val encrypted = AESHelper.encrypt(password + SALT)
        if (encrypted == encryptedPassword)
            return true
        return false
    }


}
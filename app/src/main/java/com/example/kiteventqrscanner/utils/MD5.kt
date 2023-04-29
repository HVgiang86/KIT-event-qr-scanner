package com.example.kiteventqrscanner.utils

import java.math.BigInteger
import java.security.MessageDigest

object MD5 {
    fun hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
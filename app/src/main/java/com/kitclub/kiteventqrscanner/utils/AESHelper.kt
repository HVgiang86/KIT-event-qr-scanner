package com.kitclub.kiteventqrscanner.utils

import android.util.Base64
import android.util.Log
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESHelper {
    private const val TAG = "KIT"
    private var keyStr = "6w5NFZDvLherPw41QAaxawRPTJo2Y0s4"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val AES = "AES"
    private lateinit var key: SecretKeySpec
    private lateinit var iv: IvParameterSpec
    private const val ivStr = "KquG79fNe2mOqrye"

    fun init() {

        val keyByte = keyStr.toByteArray(Charset.defaultCharset())
        key = SecretKeySpec(keyByte, 0, keyByte.size, AES)
        iv = IvParameterSpec(ivStr.toByteArray(Charset.defaultCharset()))
    }

    fun encrypt(raw: String): String {

        Log.d(TAG, "encrypting")

        //val rawData = Base64.decode(raw, Base64.DEFAULT)
        val rawData = raw.toByteArray(charset("utf-8"))

        // Initialize the Cipher object for encryption
        val cipher = Cipher.getInstance(TRANSFORMATION)


        cipher.init(Cipher.ENCRYPT_MODE, key, iv)

        // Encrypt the data
        val encryptedData = cipher.doFinal(rawData)

        return Base64.encodeToString(encryptedData, Base64.DEFAULT).trim()
    }

    fun encrypt(raw: ByteArray): ByteArray {
        Log.d(TAG, "encrypting")
        // Initialize the Cipher object for encryption
        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.ENCRYPT_MODE, key, iv)

        // Encrypt the data
        return cipher.doFinal(raw)

    }

    fun decrypt(raw: String): String {
        Log.d(TAG, "Decrypting")

        // Initialize the Cipher object for decryption
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        // Decrypt the encrypted data
        val rawData = Base64.decode(raw, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(rawData)

        return String(decryptedData, charset("utf-8")).trim()
    }

    fun decrypt(raw: ByteArray): ByteArray {
        Log.d(TAG, "Decrypting")

        // Initialize the Cipher object for decryption
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        // Decrypt the encrypted data
        return cipher.doFinal(raw)
    }
}
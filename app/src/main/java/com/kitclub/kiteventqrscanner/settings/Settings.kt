package com.kitclub.kiteventqrscanner.settings

import android.content.Context
import android.util.Log
import com.kitclub.kiteventqrscanner.utils.LoginVerifier
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Settings {
    private const val PREF_NAME = "com.example.kiteventqrscanner.settings.PREF"
    private const val PREF_MODE = Context.MODE_PRIVATE
    private const val SETTINGS_KEY = "com.example.kiteventqrscanner.settings.PREF.SETTINGS_KEY"
    private const val FIREBASE_URL_KEY = "com.example.kiteventqrscanner.settings.PREF.FIREBASE_URL_KEY"
    private const val PARAM_AMOUNT_KEY = "param amount"
    private const val PARAM_NAME_KEY = "name"
    private const val PARAM_REQUIRED_KEY = "required"
    private const val PARAM_LIST_KEY = "param list"
    private const val ENCRYPTED_PASSWORD_KEY = "encrypted password"

    private const val TAG = "KIT"

    var paramList: MutableList<QRParam> = ArrayList()
    var firebaseURL = ""


    fun setDefaultSettings(ctx: Context) {
        paramList.clear()
        paramList.add(QRParam("email", true))
        paramList.add(QRParam("code", false))
        firebaseURL = "https://kit-qr-checkin-default-rtdb.asia-southeast1.firebasedatabase.app"
        saveSettings(ctx)
    }

    fun getLocalSettings(ctx: Context) {
        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val settingJSONString = pref.getString(SETTINGS_KEY, "fail")

        loadEncryptedPassword(ctx)

        if ("fail" == settingJSONString)
            setDefaultSettings(ctx)

        try {
            val jsonRoot = JSONObject(settingJSONString!!)
            firebaseURL = jsonRoot.getString(FIREBASE_URL_KEY)
            val numberOfParams = jsonRoot.getInt(PARAM_AMOUNT_KEY)

            if (numberOfParams == 0)
                setDefaultSettings(ctx)

            val jsonArray = jsonRoot.getJSONArray(PARAM_LIST_KEY)

            for (i in 1.rangeTo(numberOfParams)) {
                val jsonObject = JSONObject(jsonArray[i - 1].toString())
                val name = jsonObject.getString(PARAM_NAME_KEY)
                val required = jsonObject.getBoolean(PARAM_REQUIRED_KEY)
                paramList.add(QRParam(name, required))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d(TAG,"default settings")
            setDefaultSettings(ctx)
            saveSettings(ctx)
        }

        for (param in paramList) {
            Log.d(TAG,"${param.name}: ${param.required}")
        }
    }

    fun saveSettings(ctx: Context) {
        val jsonArray = JSONArray()
        val jsonRoot = JSONObject()
        for (param in paramList) {
            val jsonObject = JSONObject()
            jsonObject.put(PARAM_NAME_KEY, param.name)
            jsonObject.put(PARAM_REQUIRED_KEY, param.required)
            jsonArray.put(jsonObject)
        }

        jsonRoot.put(FIREBASE_URL_KEY, firebaseURL)
        jsonRoot.put(PARAM_AMOUNT_KEY, paramList.size)
        jsonRoot.put(PARAM_LIST_KEY, jsonArray)


        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val editor = pref.edit()
        editor.putString(SETTINGS_KEY, jsonRoot.toString())
        editor.apply()
        Log.d(TAG, "settings: $jsonRoot")
    }

    /*fun addSetting(name: String, required: Boolean) {
        paramList.add(QRParam(name.lowercase(), required))
    }*/

    fun replaceSettings(firebaseUrl: String, paramList: MutableList<QRParam>) {
        firebaseURL = firebaseUrl
        Settings.paramList.clear()
        for (param in paramList) {
            Settings.paramList.add(param)
        }
    }

    private fun loadEncryptedPassword(ctx: Context){
        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val s = pref.getString(ENCRYPTED_PASSWORD_KEY,"").toString()
        LoginVerifier.encryptedPassword = s
        Log.d(TAG,"encryptedpassword: $s")
    }

    fun updateEncryptedPassword(password: String, ctx: Context) {
        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val editor = pref.edit()
        editor.putString(ENCRYPTED_PASSWORD_KEY, password)
        editor.apply()
    }

    fun getSettingsFromJSON(settingJSONString: String?,context: Context) {
        val firebaseURL1: String
        val paramList1: MutableList<QRParam> = ArrayList()

        try {
            val jsonRoot = JSONObject(settingJSONString!!)
            firebaseURL1 = jsonRoot.getString(FIREBASE_URL_KEY)
            val numberOfParams = jsonRoot.getInt(PARAM_AMOUNT_KEY)

            if (numberOfParams == 0)
                return

            val jsonArray = jsonRoot.getJSONArray(PARAM_LIST_KEY)

            for (i in 1.rangeTo(numberOfParams)) {
                val jsonObject = JSONObject(jsonArray[i - 1].toString())
                val name = jsonObject.getString(PARAM_NAME_KEY)
                val required = jsonObject.getBoolean(PARAM_REQUIRED_KEY)
                paramList1.add(QRParam(name, required))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        firebaseURL = firebaseURL1
        paramList.clear()
        for (param in paramList1) {
            paramList.add(param)
        }
        Log.d(TAG,"Save settings from db")
        saveSettings(context)
    }

}
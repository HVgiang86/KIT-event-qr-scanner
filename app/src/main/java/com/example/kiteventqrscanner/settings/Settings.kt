package com.example.kiteventqrscanner.settings

import android.content.Context
import android.util.Log
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

    private const val TAG = "KIT"

    private var paramList: MutableList<QRParam> = ArrayList()
    private var firebaseURL = ""


    private fun setDefaultSettings() {
        paramList.clear()
        paramList.add(QRParam("Email", true))
        paramList.add(QRParam("Code", true))
        firebaseURL = "https://kitalk-qr-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

    fun getLocalSettings(ctx: Context) {
        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val settingJSONString = pref.getString(SETTINGS_KEY, "fail")

        if ("fail" == settingJSONString)
            setDefaultSettings()

        try {
            val jsonRoot = JSONObject(settingJSONString!!)
            val numberOfParams = jsonRoot.getInt(PARAM_AMOUNT_KEY)
            if (numberOfParams == 0)
                setDefaultSettings()

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
            setDefaultSettings()
            saveSettings(ctx)
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

        jsonRoot.put(PARAM_LIST_KEY, jsonArray)
        jsonRoot.put(PARAM_AMOUNT_KEY, paramList.size)

        val pref = ctx.getSharedPreferences(PREF_NAME, PREF_MODE)
        val editor = pref.edit()
        editor.putString(SETTINGS_KEY, jsonRoot.toString())
        editor.apply()
        Log.d(TAG, "settings: $jsonRoot")
    }

    fun addSetting(name: String, required: Boolean) {
        paramList.add(QRParam(name, required))
    }


}
package com.kitclub.kiteventqrscanner.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.application.AppState
import com.kitclub.kiteventqrscanner.broadcastreceiver.InternetConnectionChangeReceiver
import com.kitclub.kiteventqrscanner.controller.LoginController
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.utils.AESHelper

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var passwordField: EditText
    private lateinit var visiblePasswordBtn: ImageButton
    private lateinit var loginBtn: Button
    private lateinit var splashView: RelativeLayout
    private lateinit var warningTV: TextView

    private lateinit var connectionlessDialog: AlertDialog
    private lateinit var actionReceiver: InternetConnectionChangeReceiver<LoginActivity>

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        passwordField = findViewById(R.id.password_edt)
        visiblePasswordBtn = findViewById(R.id.visible_password_btn)
        loginBtn = findViewById(R.id.login_btn)
        splashView = findViewById(R.id.splash_view)
        warningTV = findViewById(R.id.warning_tv)

        loginBtn.setOnClickListener {
            requireLogin()
        }

        visiblePasswordBtn.setOnClickListener { changePasswordVisible() }

        appInit()
        initConnectionlessDialog()

        splashScreen()
        if (AppState.loginStatus)
            enterApp()
    }

    @SuppressLint("SetTextI18n")
    private fun requireLogin() {
        if (checkLogin()) enterApp()
        else warningTV.visibility = View.VISIBLE

    }

    private fun appInit() {
        AESHelper.init()
        FirebaseHelper.init(Settings.firebaseURL, applicationContext)
    }

    private fun checkLogin(): Boolean {
        val s = passwordField.text.toString().trim()
        return LoginController.checkLogin(s)
    }

    private fun changePasswordVisible() {
        if (isPasswordVisible) {
            visiblePasswordBtn.setImageResource(R.drawable.ic_visible_off)
            passwordField.transformationMethod = PasswordTransformationMethod()
            isPasswordVisible = false
        } else {
            visiblePasswordBtn.setImageResource(R.drawable.ic_visible)
            passwordField.transformationMethod = null
            isPasswordVisible = true
        }
    }

    private fun splashScreen() {
        Handler().postDelayed(
            { runOnUiThread { this@LoginActivity.splashView.visibility = View.GONE } }, 1500L
        )
    }

    private fun initConnectionlessDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("No internet connection found!")
            .setIcon(R.drawable.ic_warning)
            .setMessage("This app cannot work without an internet connection. Please check your Wifi or 3G/4G connection!")
            .setCancelable(false)

        connectionlessDialog = dialog.create()
    }

    private fun enterApp() {
        AppState.loginStatus = true
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    fun setConnectionWarning(visible: Boolean) {
        if (visible) {
            connectionlessDialog.show()

        } else {
            connectionlessDialog.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        actionReceiver = InternetConnectionChangeReceiver(this)
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(actionReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(actionReceiver)
    }
}
package com.kitclub.kiteventqrscanner.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.application.AppStatus
import com.kitclub.kiteventqrscanner.controller.LoginController
import com.kitclub.kiteventqrscanner.controller.QRScanController
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.model.repository.RealmHelper
import com.kitclub.kiteventqrscanner.utils.AESHelper

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var passwordField: EditText
    private lateinit var visiblePasswordBtn: ImageButton
    private lateinit var loginBtn: Button
    private lateinit var splashView: RelativeLayout
    private lateinit var warningTV: TextView

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

        splashScreen()
        if (AppStatus.loginStatus)
            enterApp()
    }

    @SuppressLint("SetTextI18n")
    private fun requireLogin() {
        if (checkLogin()) enterApp()
        else warningTV.visibility = View.VISIBLE

    }

    private fun appInit() {
        AESHelper.init()
        RealmHelper.init()
        FirebaseHelper.init(Settings.firebaseURL, applicationContext)
        QRScanController.init()
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

    private fun enterApp() {
        AppStatus.loginStatus = true
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


}
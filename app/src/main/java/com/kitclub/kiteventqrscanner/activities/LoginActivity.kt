package com.kitclub.kiteventqrscanner.activities

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
import com.kitclub.kiteventqrscanner.database.RealmHelper
import com.kitclub.kiteventqrscanner.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.settings.Settings
import com.kitclub.kiteventqrscanner.utils.AESHelper
import com.kitclub.kiteventqrscanner.utils.LoginVerifier

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

        AESHelper.init()
        FirebaseHelper.init(Settings.firebaseURL, applicationContext)
        RealmHelper.init()

        splashScreen()
    }

    @SuppressLint("SetTextI18n")
    private fun requireLogin() {
        if (checkLogin()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            warningTV.visibility = View.VISIBLE
        }
    }

    private fun checkLogin(): Boolean {
        val s = passwordField.text.toString().trim()
        return LoginVerifier.checkLogin(s)
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

}
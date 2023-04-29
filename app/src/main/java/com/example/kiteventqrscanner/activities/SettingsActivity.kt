package com.example.kiteventqrscanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kiteventqrscanner.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Settings")
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }
}
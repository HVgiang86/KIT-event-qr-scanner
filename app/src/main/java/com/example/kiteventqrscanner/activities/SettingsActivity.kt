package com.example.kiteventqrscanner.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kiteventqrscanner.R
import com.example.kiteventqrscanner.adapters.SettingsAdapter
import com.example.kiteventqrscanner.settings.Settings
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    var settingsChanged = false

    private lateinit var firebaseUrlEdt: EditText
    private lateinit var adapter: SettingsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Settings"
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { confirmLeave() }

        firebaseUrlEdt = findViewById(R.id.firebase_url_edt)
        firebaseUrlEdt.setText(Settings.firebaseURL)

        val fabBtn = findViewById<FloatingActionButton>(R.id.fab)
        fabBtn.setOnClickListener { adapter.addParam() }

        firebaseUrlEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim() != Settings.firebaseURL) {
                    settingsChanged = true
                }

            }
        })

        adapter = SettingsAdapter(Settings.paramList, this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
    }

    private fun confirmLeave() {
        if (settingsChanged) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Save changed settings before leave?")
                .setIcon(R.drawable.ic_action_save)
                .setMessage("All your changed settings will be saved").setCancelable(false)
                .setPositiveButton("Save") { _, _ ->
                    saveChangedSettings()
                    finish()
                }.setNegativeButton("Cancel") { dialog1, _ ->
                    dialog1.cancel()
                }.setNeutralButton("Don't save") { _, _ ->
                    finish()
                }

            dialog.create().show()
        } else {
            finish()
        }

    }

    private fun saveChangedSettings() {
        Settings.replaceSettings(
            firebaseUrlEdt.text.toString().trim(), adapter.getAfterChangedList()
        )
        Settings.saveSettings(this)
        Toast.makeText(this, "\nSaved!\n", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (settingsChanged)
            confirmLeave()
        else
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_menu -> {
                saveChangedSettings()
                finish()
            }

            R.id.restore_menu -> {
                confirmRestoreDefaultSettings()
            }

            R.id.cancel_menu -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun confirmRestoreDefaultSettings() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Restore to default settings?")
            .setIcon(R.drawable.ic_action_restore)
            .setMessage("All your settings will be set to default").setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                Settings.setDefaultSettings(this)
                finish()
            }.setNegativeButton("Cancel") { dialog1, _ ->
                dialog1.cancel()
            }

        dialog.create().show()
    }

    /*@SuppressLint("NotifyDataSetChanged")
    private fun reloadParamList() {
        adapter.notifyDataSetChanged()
    }*/


}
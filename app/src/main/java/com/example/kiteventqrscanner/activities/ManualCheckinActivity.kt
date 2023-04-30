package com.example.kiteventqrscanner.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kiteventqrscanner.R
import com.example.kiteventqrscanner.adapters.ManualCheckinAdapter
import com.example.kiteventqrscanner.firebase.FirebaseHelper
import com.example.kiteventqrscanner.model.Attendee
import com.example.kiteventqrscanner.settings.Settings
import com.example.kiteventqrscanner.utils.DeviceInfo
import com.example.kiteventqrscanner.utils.MD5

@Suppress("DEPRECATION")
class ManualCheckinActivity : AppCompatActivity() {
    val filledRequiredField = HashMap<String, Boolean>()
    lateinit var attendee: Attendee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_checkin)
        init()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Manual Checkin"
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val adapter = ManualCheckinAdapter(attendee,Settings.paramList,this)
        recyclerView.adapter = adapter
    }

    fun saveAttendee(v: View) {
        confirmSave()
    }

    private fun saveAttendee() {
        FirebaseHelper.sendToFirebase(attendee)
    }


    private fun init() {
        val attendeeProperty = HashMap<String, String>()
        for (param in Settings.paramList) {
            attendeeProperty[param.name] = ""
            if (param.required)
                filledRequiredField[param.name] = false
        }
        attendee = Attendee(
            "KIT" + MD5.hash("" + System.currentTimeMillis() + DeviceInfo.getSerialNumber()),
            attendeeProperty
        )
    }

    private fun confirmSave() {
        if (canSave()) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Save changed?")
                .setIcon(R.drawable.ic_action_save)
                .setMessage("All your changed will be saved").setCancelable(false)
                .setPositiveButton("Save") { _, _ ->
                    saveAttendee()
                    finish()
                }.setNegativeButton("Cancel") { dialog1, _ ->
                    dialog1.cancel()
                }.setNeutralButton("Don't save") { _, _ ->
                    finish()
                }

            dialog.create().show()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Fill all required information")
                .setIcon(R.drawable.ic_action_save)
                .setMessage("Required information marked with a (*) symbol").setCancelable(false)
                .setPositiveButton("OK") { dialog1, _ ->
                    dialog1.cancel()
                }

            dialog.create().show()
        }
    }

    private fun canSave(): Boolean {
        for (param in filledRequiredField) {
            if (!param.value)
                return false
        }
        return true
    }
}
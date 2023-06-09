package com.kitclub.kiteventqrscanner.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.attendee.AttendeeList
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.utils.DeviceInfo
import com.kitclub.kiteventqrscanner.utils.MD5
import com.kitclub.kiteventqrscanner.view.adapters.ManualCheckinAdapter

@Suppress("DEPRECATION")
class ManualCheckinActivity : AppCompatActivity() {
    val filledRequiredField = HashMap<String, Boolean>()
    private lateinit var attendee: Attendee
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_checkin)
        init()

        saveBtn = findViewById(R.id.save_btn)
        saveBtn.setOnClickListener { saveAttendeeRequest() }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Manual Checkin"
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val adapter = ManualCheckinAdapter(attendee, Settings.paramList, this)
        recyclerView.adapter = adapter
    }

    private fun saveAttendeeRequest() {
        confirmSave()
    }

    private fun saveAttendee(): Boolean {

        return if (!AttendeeList.containIgnoreId(attendee)) {
            FirebaseHelper.sendToFirebase(attendee)
            AttendeeList.attendeeList.add(attendee)
            true
        } else {
            Toast.makeText(this, "Existing attendee", Toast.LENGTH_SHORT).show()
            false
        }
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
                    if (saveAttendee())
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
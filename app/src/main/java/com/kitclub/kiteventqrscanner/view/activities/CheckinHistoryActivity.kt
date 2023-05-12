package com.kitclub.kiteventqrscanner.view.activities

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.controller.LoginController
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.attendee.AttendeeList
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.view.adapters.CheckinHistoryAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.Calendar


@Suppress("DEPRECATION")
class CheckinHistoryActivity : AppCompatActivity() {
    private lateinit var attendeeList: MutableList<Attendee>

    private lateinit var adapter: CheckinHistoryAdapter
    private lateinit var attendeeCountTV: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin_history)

        attendeeCountTV = findViewById(R.id.attendees_count_tv)
        //ask for external storage permission
        if (shouldAskPermissions()) {
            askPermissions()
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Check-in History"
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        attendeeList = AttendeeList.attendeeList

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        val recycler: RecyclerView = findViewById(R.id.recycler_view)
        recycler.layoutManager = linearLayoutManager
        adapter = CheckinHistoryAdapter(attendeeList, Settings.paramList, this)
        recycler.adapter = adapter

        refreshView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.checkin_history_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_menu -> {
                exportMenu()
            }

            R.id.cloud_sync -> {
                confirmCloudSync()
            }

            R.id.clear_menu -> {
                confirmClear()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearHistory() {
        FirebaseHelper.deleteAllRecord()
        Handler(Looper.getMainLooper()).postDelayed({
            refreshView()
        }, 1500)
        Toast.makeText(this, "\nDeleted!\n", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun exportMenu() {
        try {
            val downloadPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val directoryPath = "KIT event check-in"
            var directory = File(downloadPath, directoryPath)

            Log.d("KIT","environment: ${directory.absolutePath}")

            if (!directory.exists()) {
                directory.mkdirs()
                Log.d("KIT","mkdirs")
            }

            if (!directory.exists()) {
                directory = File(filesDir.absolutePath)
            }

            val date = Calendar.getInstance().time

            val filename =
                "KIT-attendees-${date.hours}:${date.minutes}:${date.seconds} ${date.date}-${date.month + 1}-${date.year + 1900}.json"
            val file = File(directory, filename)

            Log.d("KIT","filepath: ${file.absolutePath}")

            if (!file.exists()) file.createNewFile()

            Log.d("KIT", "filepath: ${file.absolutePath}")
            val writer = FileWriter(file)

            val jsonRoot = JSONObject()
            for (param in Settings.paramList) {
                val jsonArray = JSONArray()
                for (attendee in attendeeList) {
                    jsonArray.put(attendee.paramList[param.name])
                }
                jsonRoot.put(param.name, jsonArray)
            }
            Log.d("KIT", jsonRoot.toString())
            writer.write(jsonRoot.toString())
            writer.close()
            showNoticeDialog("Check-in history saved as JSON in file:\n${file.absolutePath}!")
        } catch (e: Exception) {
            e.printStackTrace()
            showNoticeDialog("There's an error when saving Check-in history into file!")
        }
    }

    private fun showNoticeDialog(content: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Warning").setIcon(R.drawable.ic_warning).setMessage(content)
            .setCancelable(false).setNegativeButton("OK") { dialog1, _ ->
                dialog1.cancel()
            }

        dialog.create().show()
    }

    private fun confirmClear() {
        val layoutInflater = layoutInflater
        val promptView = layoutInflater.inflate(R.layout.password_promt_dialog, null)

        val dialogBuilder =
            AlertDialog.Builder(this).setView(promptView).setIcon(R.drawable.ic_warning)

        val passwordPromptEdt: EditText = promptView.findViewById(R.id.prompt_password_edt)
        val noticeTV: TextView = promptView.findViewById(R.id.notice_tv)

        dialogBuilder.setCancelable(false).setPositiveButton("OK") { _, _ -> }
            .setNegativeButton("Cancel") { dialog1, _ ->
                dialog1.cancel()
            }

        val dialog = dialogBuilder.create()
        dialog.show()

        val buttonOK: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonOK.setOnClickListener {
            if (LoginController.checkAdminPassword(
                    passwordPromptEdt.text.toString().trim()
                )
            ) {
                Log.d("KIT", "confirm clear history")
                dialog.dismiss()
                clearHistory()
            } else noticeTV.visibility = View.VISIBLE
        }

    }

    private fun confirmCloudSync() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Cloud Sync?").setIcon(R.drawable.ic_cloud_sync)
            .setMessage("Sync all check-in history from Firebase database?").setCancelable(false)
            .setPositiveButton("Sync") { _, _ ->
                cloudSync()
            }.setNegativeButton("Cancel") { dialog1, _ ->
                dialog1.cancel()
            }

        dialog.create().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshView() {
        adapter.notifyDataSetChanged()
        attendeeCountTV.text = "We have ${attendeeList.size} attendees!"
    }


    private fun cloudSync() {
        FirebaseHelper.attendeeListSync()
        Handler(Looper.getMainLooper()).postDelayed({
            refreshView()
        }, 1500)
        Toast.makeText(this, "\nSynced!\n", Toast.LENGTH_SHORT).show()
    }

    private fun shouldAskPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true
        }
        return false
    }

    /**
     * This function request for important permissions, if user accept, application will work correctly
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.MANAGE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
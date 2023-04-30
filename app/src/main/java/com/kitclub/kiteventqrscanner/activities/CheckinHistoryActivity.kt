package com.kitclub.kiteventqrscanner.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.kitclub.kiteventqrscanner.R
import com.kitclub.kiteventqrscanner.adapters.CheckinHistoryAdapter
import com.kitclub.kiteventqrscanner.database.RealmHelper
import com.kitclub.kiteventqrscanner.model.Attendee
import com.kitclub.kiteventqrscanner.settings.Settings
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.Date

@Suppress("DEPRECATION")
class CheckinHistoryActivity : AppCompatActivity() {
    private lateinit var attendeeList: MutableList<Attendee>

    private lateinit var adapter: CheckinHistoryAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin_history)

        //ask for external storage permission
        if (shouldAskPermissions()) {
            askPermissions()
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Checkin History"
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        attendeeList = RealmHelper.read()

        val recycler: RecyclerView = findViewById(R.id.recycler_view)
        adapter = CheckinHistoryAdapter(attendeeList, Settings.paramList, this)
        recycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.checkin_history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.export_menu -> {
                exportMenu()
            }

            R.id.clear_menu -> {
                confirmClear()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearHistory() {
        RealmHelper.clear()
        attendeeList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun exportMenu() {
        try {
            val directoryPath = "/storage/emulated/0/KIT event check-in"
            val directory = File(directoryPath)

            if (!directory.exists()) directory.mkdirs()

            val date = Date()
            val filename =
                "KIT-attendees-${System.currentTimeMillis() / 1000 / 60 / 60}-${System.currentTimeMillis() / 1000 / 60}-${System.currentTimeMillis() / 1000}-${date.date}-${date.month}-${date.year}.json"
            val file = File(directory, filename)

            if (!file.exists()) file.createNewFile()

            Log.d("KIT","filepath: ${file.absolutePath}")
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
            showNoticeDialog("Check-in history saved as JSON in file: ${file.name}!")
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
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Delete all check-in history?")
            .setIcon(R.drawable.ic_action_save)
            .setMessage("All your history will be deleted!").setCancelable(false)
            .setPositiveButton("Delete") { _, _ ->
                clearHistory()
            }.setNegativeButton("Cancel") { dialog1, _ ->
                dialog1.cancel()
            }

        dialog.create().show()
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
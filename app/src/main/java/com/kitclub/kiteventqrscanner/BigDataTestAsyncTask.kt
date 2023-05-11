@file:Suppress("DEPRECATION")

package com.kitclub.kiteventqrscanner

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.utils.MD5

class BigDataTestAsyncTask : AsyncTask<Void, Void, Int>() {
    private var count = 0
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Int {
        val paramList = HashMap<String, String>()
        for (param in Settings.paramList) {
            paramList[param.name] = "test"
        }
        while (count <= 400) {
            Handler(Looper.getMainLooper()).postDelayed({

                val id = "KIT" + MD5.hash("stress_test123$count")
                Log.d("KIT","sent $count")
                val attendee = Attendee(id, paramList)
                FirebaseHelper.sendToFirebase(attendee)
                count++
                //Do something after 100ms
            }, 1000)
        }

        return count
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: Int?) {
        Log.d("KIT", "posted data")
        super.onPostExecute(result)
    }
}
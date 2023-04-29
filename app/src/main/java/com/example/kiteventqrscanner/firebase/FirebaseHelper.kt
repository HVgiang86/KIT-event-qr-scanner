package com.example.kiteventqrscanner.firebase

import android.util.Log
import com.example.kiteventqrscanner.model.Attendee
import com.example.kiteventqrscanner.settings.Settings
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseHelper {
    private var database =
        Firebase.database("https://kitalk-qr-default-rtdb.asia-southeast1.firebasedatabase.app/")

    fun init(firebaseURL: String) {
        try {
            database = Firebase.database(firebaseURL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendToFirebase(attendee: Attendee) {

        try {
            val ref = database.getReference("attendees")
            val attendeeRef = ref.child(attendee.id)

            var ref1: DatabaseReference
            for (param in Settings.paramList) {
                ref1 = attendeeRef.child(param.name)
                ref1.setValue(attendee.paramList[param.name])
            }

            Log.d("KIT", "sent to firebase")

        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}
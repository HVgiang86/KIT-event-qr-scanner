package com.example.kiteventqrscanner.firebase

import android.util.Log
import com.example.kiteventqrscanner.model.Attendee
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.net.URL

object FirebaseHelper {
    private var database = Firebase.database("https://kitalk-qr-default-rtdb.asia-southeast1.firebasedatabase.app/")

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

            val EMAIL = "email"
            val CODE = "code"

            var ref1 = attendeeRef.child(EMAIL)
            ref1.setValue(attendee.email)
            ref1 = attendeeRef.child(CODE)
            ref1.setValue(attendee.code)

            Log.d("KIT", "sent to firebase")

        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}
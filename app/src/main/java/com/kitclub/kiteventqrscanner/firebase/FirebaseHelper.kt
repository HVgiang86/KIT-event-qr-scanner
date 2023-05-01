package com.kitclub.kiteventqrscanner.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kitclub.kiteventqrscanner.model.Attendee
import com.kitclub.kiteventqrscanner.settings.Settings
import com.kitclub.kiteventqrscanner.utils.LoginVerifier

object FirebaseHelper {
    private const val TAG = "KIT"

    private var database =
        Firebase.database("https://kitalk-qr-default-rtdb.asia-southeast1.firebasedatabase.app/")

    fun init(firebaseURL: String, context: Context) {
        try {
            database = Firebase.database(firebaseURL)
            setEncryptedPasswordValueListener(context)
            setSettingsValueListener(context)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setEncryptedPasswordValueListener(ctx: Context) {
        database.getReference("password").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val s = snapshot.value.toString().trim()
                LoginVerifier.encryptedPassword = s
                Settings.updateEncryptedPassword(s, ctx)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Firebase error")
            }
        })
    }

    private fun setSettingsValueListener(context: Context) {
        database.getReference("settings").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val s = snapshot.value.toString().trim()
                Settings.getSettingsFromJSON(s, context)
                Log.d(TAG,"Loaded settings from db")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Firebase error")
            }
        })
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
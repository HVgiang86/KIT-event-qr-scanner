package com.kitclub.kiteventqrscanner.model.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.attendee.AttendeeList
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.model.repository.SettingsReferences

object FirebaseHelper {
    private const val TAG = "KIT"

    private var database =
        Firebase.database("https://kit-qr-checkin-default-rtdb.asia-southeast1.firebasedatabase.app")

    fun init(firebaseURL: String, context: Context) {
        try {
            database = Firebase.database(firebaseURL)
            setEncryptedPasswordValueListener(context)
            setSettingsValueListener(context)
            //setAttendeesValueListener()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setEncryptedPasswordValueListener(ctx: Context) {
        database.getReference("password").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val s = snapshot.value.toString().trim()
                SettingsReferences.updateEncryptedPassword(s, ctx)
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
                SettingsReferences.getSettingsFromJSON(s, context)
                Log.d(TAG,"Loaded settings from db")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Firebase error")
            }
        })
    }

    private fun setAttendeesValueListener() {
        database.getReference("attendees").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG,"Sync database!")
                val list = AttendeeList.forSyncList
                list.clear()
                try {
                    if (snapshot.exists()) {
                        if (snapshot.hasChildren()) {
                            val children = snapshot.children
                            for (ref in children) {
                                Log.d(TAG,"ref changed: ${ref.key}")
                                val id:String = ref.key as String
                                val params = HashMap<String,String>()
                                for (param in Settings.paramList) {
                                    val ref1 = ref.child(param.name)
                                    params[param.name] = ref1.value as String
                                }
                                list.add(Attendee(id,params))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //AttendeeList.requireSync = true
                Log.d(TAG,"Updated on: " + list.size)
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
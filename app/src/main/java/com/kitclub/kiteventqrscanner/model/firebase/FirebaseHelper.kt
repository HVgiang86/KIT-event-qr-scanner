@file:Suppress("UNCHECKED_CAST")

package com.kitclub.kiteventqrscanner.model.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.ktx.Firebase
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.attendee.AttendeeList
import com.kitclub.kiteventqrscanner.model.repository.SettingsReferences


object FirebaseHelper {
    private const val TAG = "KIT"

    private var database =
        Firebase.database("https://kit-qr-checkin-default-rtdb.asia-southeast1.firebasedatabase.app")

    private val db = FirebaseFirestore.getInstance().collection("attendees")

    fun init(firebaseURL: String, context: Context) {
        try {
            database = Firebase.database(firebaseURL)
            setEncryptedPasswordValueListener(context)
            setSettingsValueListener(context)
            setOnAttendeeChanged()

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
                Log.d(TAG, "Loaded settings from db")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Firebase error")
            }
        })
    }


    private fun setOnAttendeeChanged() {

        db.addSnapshotListener { value, error ->

            val changedList = value?.documentChanges
            val allListEvent = changedList?.size == value?.documents?.size
            if (!changedList.isNullOrEmpty()) {
                for (document in changedList) {

                    try {

                        //detect added event
                        if (document.oldIndex == -1) {

                            val id = document.document.id
                            val paramList: HashMap<String, String> =
                                document.document.data as HashMap<String, String>
                            val attendee = Attendee(id, paramList)
                            AttendeeList.attendeeList.add(attendee)

                        } else if (document.newIndex == -1) { // detect removed event

                            val id = document.document.id
                            if (allListEvent) AttendeeList.attendeeList.clear()

                            if (AttendeeList.containId(id)) AttendeeList.removeById(id)
                        } else { //detect modify event

                            val id = document.document.id
                            val paramList: HashMap<String, String> =
                                document.document.data as HashMap<String, String>
                            val attendee = Attendee(id, paramList)
                            if (AttendeeList.containId(id)) AttendeeList.modifyById(id, attendee)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            Log.d(TAG, "Done sync")
            error?.printStackTrace()
        }
    }

    fun deleteAllRecord() {
        for (attendee in AttendeeList.attendeeList) {
            try {
                db.document(attendee.id).delete()
            } catch (e: Exception) {
                e.printStackTrace()
                continue
            }
        }
        Log.d(TAG,"Clear all history")
    }

    /**
     * Call the 'recursiveDelete' callable function with a path to initiate
     * a server-side delete.
     */
    private fun deleteAtPath(path: String) {
        val data: MutableMap<String, Any> = HashMap()
        data["path"] = path
        val deleteFn: HttpsCallableReference =
            FirebaseFunctions.getInstance().getHttpsCallable("recursiveDelete")
        deleteFn.call(data).addOnSuccessListener {
            Log.d(TAG, "Firestore delete $path successfully")
        }.addOnFailureListener {
            Log.d(TAG, "Firestore delete $path fail")
        }
    }

    fun sendToFirebase(attendee: Attendee) {

        try {
            db.runCatching {
                val document = db.document(attendee.id)
                document.set(attendee.paramList).addOnSuccessListener {
                    Log.d(TAG, "Check-in success fully")
                }.addOnFailureListener { Log.d(TAG, "Firestore error") }

            }

        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    fun attendeeListSync() {
        AttendeeList.attendeeList.clear()
        db.runCatching {
            val list = db.get().result.documents
            if (list.isNotEmpty()) {
                for (document in list) {
                    val id = document.reference.id
                    val params = document.data as HashMap<String,String>
                    AttendeeList.attendeeList.add(Attendee(id,params))
                }

            }
        }
    }

}
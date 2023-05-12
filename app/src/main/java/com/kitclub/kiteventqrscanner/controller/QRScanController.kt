package com.kitclub.kiteventqrscanner.controller

import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.attendee.AttendeeList
import com.kitclub.kiteventqrscanner.utils.QRParser

object QRScanController {
    const val ATTENDEE_VALID = 10001
    const val ATTENDEE_INVALID = 10002
    const val ATTENDEE_EXIST = 10003


    fun requestNewAttendee(content: String): Int {

        val attendee = QRParser.getAttendee(content)
        if (attendee != null) {
            return if (!containAttendee(attendee)) {
                addAttendee(attendee)
                ATTENDEE_VALID
            } else
                ATTENDEE_EXIST
        }

        return ATTENDEE_INVALID

    }

    private fun containAttendee(attendee: Attendee): Boolean {
        return AttendeeList.containIgnoreId(attendee)
    }

    private fun addAttendee(attendee: Attendee) {
        AttendeeList.attendeeList.add(attendee)
        FirebaseHelper.sendToFirebase(attendee)
    }
}
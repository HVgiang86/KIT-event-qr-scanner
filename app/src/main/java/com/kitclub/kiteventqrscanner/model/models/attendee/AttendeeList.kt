package com.kitclub.kiteventqrscanner.model.models.attendee

object AttendeeList {
    var attendeeList: MutableList<Attendee> = ArrayList()

    fun containIgnoreId(attendee: Attendee): Boolean {
        for (obj in attendeeList) {
            if (obj.compareIgnoreId(attendee))
                return true
        }
        return false
    }
}
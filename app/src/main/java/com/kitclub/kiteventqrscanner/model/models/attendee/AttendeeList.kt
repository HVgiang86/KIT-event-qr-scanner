package com.kitclub.kiteventqrscanner.model.models.attendee

object AttendeeList {
    var attendeeList: MutableList<Attendee> = ArrayList()
    var forSyncList: MutableList<Attendee> = ArrayList()
    var requireSync = false

    fun containIgnoreId(attendee: Attendee): Boolean {
        for (obj in attendeeList) {
            if (obj.compareIgnoreId(attendee))
                return true
        }
        return false
    }

    fun syncList() {
            attendeeList.clear()
            for (attendee in forSyncList) {
                attendeeList.add(attendee)
            }
    }
}
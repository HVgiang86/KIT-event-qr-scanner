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

    fun containId(id: String) : Boolean {
        for (obj in attendeeList) {
            if (obj.id == id) {
                return true
            }
        }
        return false
    }

    fun removeById(id: String) {
        for (obj in attendeeList) {
            if (obj.id == id) {
                attendeeList.remove(obj)
            }
        }
    }

    fun modifyById(id: String, attendee: Attendee) {
        for (obj in attendeeList) {
            if (obj.id == id) {
                val index = attendeeList.indexOf(obj)
                attendeeList.removeAt(index)
                attendeeList.add(index,attendee)
            }
        }
    }
}
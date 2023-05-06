package com.kitclub.kiteventqrscanner.model.repository

import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AttendeeRealm(): RealmObject {
    @PrimaryKey
     var id: String = ""
     var paramList: RealmDictionary<String> = realmDictionaryOf()

    constructor(id: String, paramList: RealmDictionary<String>) : this() {
        this.id = id
        this.paramList = paramList
    }


    fun toAttendee(): Attendee {
        val list = HashMap<String, String>()
        for (obj in paramList) {
            list[obj.key] = obj.value
        }
        return Attendee(this.id, list)
    }

}
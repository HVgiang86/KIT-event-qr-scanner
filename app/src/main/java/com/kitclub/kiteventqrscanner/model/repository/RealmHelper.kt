package com.kitclub.kiteventqrscanner.model.repository

import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmDictionary
import io.realm.kotlin.query.RealmResults

object RealmHelper {
    private lateinit var realm: Realm
    fun init() {
        val config = RealmConfiguration.create(schema = setOf(AttendeeRealm::class))
        realm = Realm.open(config)
    }

    fun save(attendee: Attendee) {
        
        realm.writeBlocking {
            this.copyToRealm(AttendeeRealm().apply {
                this.id = attendee.id
                this.paramList = attendee.paramList.toRealmDictionary()
            })
        }

    }

    fun read(): MutableList<Attendee> {
        val list = ArrayList<Attendee>()
        val items: RealmResults<AttendeeRealm> = realm.query<AttendeeRealm>().find()
        for (item in items) {
            list.add(item.toAttendee())
        }
        return list
    }

    fun close() {
        realm.close()
    }

    fun clear() {
        realm.writeBlocking {
            this.deleteAll()
        }
    }

}
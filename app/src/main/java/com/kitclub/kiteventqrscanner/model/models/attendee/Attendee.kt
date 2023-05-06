package com.kitclub.kiteventqrscanner.model.models.attendee

import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.model.repository.SettingsReferences


class Attendee(var id: String, var paramList: HashMap<String, String>) {
    fun compareIgnoreId(attendee: Attendee): Boolean {
        for (param in Settings.paramList) {
            val paramName = param.name
            if (paramList[paramName] != attendee.paramList[paramName])
                return false
        }
        return true
    }
}

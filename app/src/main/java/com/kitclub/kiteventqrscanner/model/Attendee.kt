package com.kitclub.kiteventqrscanner.model


class Attendee {
    var id: String
    var paramList: HashMap<String, String>

    constructor(id: String, paramList: HashMap<String, String>) {
        this.id = id
        this.paramList = paramList

    }
}

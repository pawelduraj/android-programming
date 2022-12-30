package com.example.app.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User() : RealmObject {
    @PrimaryKey
    var userId: Int = 0
    var name: String = ""
    var email: String = ""
    var admin: Boolean = false
    var jwt: String = ""

    constructor(userId: Int, name: String, email: String, admin: Boolean, jwt: String) : this() {
        this.userId = userId
        this.name = name
        this.email = email
        this.admin = admin
        this.jwt = jwt
    }
}

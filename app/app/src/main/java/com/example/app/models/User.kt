package com.example.app.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User() : RealmObject {
    @PrimaryKey
    var userId: Int = 0
    var name: String = "Anonymous"
    var email: String = "anonymous@example.com"
}

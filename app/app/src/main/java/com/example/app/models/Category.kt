package com.example.app.models

import com.example.app.dao.RandomId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Category() : RealmObject {
    @PrimaryKey
    var categoryId: Int = RandomId.getCategoryId()
    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }
}

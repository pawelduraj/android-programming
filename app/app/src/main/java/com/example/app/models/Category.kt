package com.example.app.models

import com.example.app.dao.RandomId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Category() : RealmObject {
    @PrimaryKey
    var categoryId: Int = 0
    var name: String = ""

    constructor(categoryId: Int, name: String) : this() {
        this.categoryId = categoryId
        this.name = name
    }
}

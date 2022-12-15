package com.example.app.models

import com.example.app.dao.RandomId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Product() : RealmObject {
    @PrimaryKey
    var productId: Int = 0
    var name: String = ""
    var category: Category? = null
    var price: Int = 0
    var inStock: Int = 0
    var description: String = ""

    constructor(
        productId: Int,
        name: String,
        category: Category,
        price: Int,
        inStock: Int,
        description: String
    ) : this() {
        this.productId = productId
        this.name = name
        this.category = category
        this.price = price
        this.inStock = inStock
        this.description = description
    }
}

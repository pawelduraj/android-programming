package com.example.app.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CartProduct() : RealmObject {
    @PrimaryKey
    var productId: Int = 0
    var product: Product? = null
    var quantity: Int = 0

    constructor(product: Product, quantity: Int) : this() {
        this.productId = product.productId
        this.product = product
        this.quantity = quantity
    }
}

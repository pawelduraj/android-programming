package com.example.app.models

import io.realm.kotlin.types.RealmObject

class OrderDetail() : RealmObject {
    var orderId: Int = 0
    var productId: Int = 0
    var quantity: Int = 0
    var price: Int = 0

    constructor(
        orderId: Int,
        productId: Int,
        quantity: Int,
        price: Int
    ) : this() {
        this.orderId = orderId
        this.productId = productId
        this.quantity = quantity
        this.price = price
    }
}

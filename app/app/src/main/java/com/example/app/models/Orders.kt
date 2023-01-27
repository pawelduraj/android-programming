package com.example.app.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Order() : RealmObject {
    @PrimaryKey
    var orderId: Int = 0
    var userId: Int = 0
    var date: String = ""
    var price: Int = 0
    var paid: Boolean = false
    var payment: String = ""

    constructor(
        orderId: Int,
        userId: Int,
        date: String,
        price: Int,
        paid: Boolean,
        payment: String
    ) : this() {
        this.orderId = orderId
        this.userId = userId
        this.date = date
        this.price = price
        this.paid = paid
        this.payment = payment
    }
}

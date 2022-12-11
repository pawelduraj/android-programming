package com.example.app.dao

object RandomId {
    private var categoryId = 0
    private var productId = 0

    fun getProductId(): Int {
        this.productId++
        return this.productId
    }

    fun getCategoryId(): Int {
        this.categoryId++
        return this.categoryId
    }
}

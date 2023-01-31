package com.example.app

import org.junit.*
import org.junit.Assert.*

class UnitTests {
    private val products = Utils.products

    @Before
    fun before() {
        Utils.clear()
    }

    @After
    fun after() {
        Utils.clear()
    }

    @Test
    fun removeProductWhichIsNotInCart() {
        assertEquals(false, Utils.removeFromCart(products[0], 1))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun removeZeroProducts() {
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(false, Utils.removeFromCart(products[0], 0))
        assertEquals(1, Utils.getCart().size)
        assertEquals(1, Utils.getCart()[0].quantity)
    }

    @Test
    fun removeNegativeProducts() {
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(false, Utils.removeFromCart(products[0], -1))
        assertEquals(1, Utils.getCart().size)
        assertEquals(1, Utils.getCart()[0].quantity)
    }

    @Test
    fun removeOneProductAndZeroIsLeft() {
        assertEquals(true, Utils.addToCart(products[7], 1))
        assertEquals(true, Utils.removeFromCart(products[7], 1))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun removeTwoProductsWhenOnlyOneIsInCart() {
        assertEquals(true, Utils.addToCart(products[7], 1))
        assertEquals(false, Utils.removeFromCart(products[7], 2))
        assertEquals(1, Utils.getCart().size)
        assertEquals(1, Utils.getCart()[0].quantity)
    }

    @Test
    fun removeTwoProductsWhenTwoAreInCart() {
        assertEquals(true, Utils.addToCart(products[7], 2))
        assertEquals(true, Utils.removeFromCart(products[7], 2))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun addNewProductWhenCartIsEmpty() {
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(1, Utils.getCart().size)
        assertEquals(1, Utils.getCart()[0].quantity)
    }

    @Test
    fun addNewProductWhenCartIsNotEmpty() {
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(true, Utils.addToCart(products[1], 1))
        assertEquals(2, Utils.getCart().size)
        assertEquals(1, Utils.getCart()[0].quantity)
        assertEquals(1, Utils.getCart()[1].quantity)
    }

    @Test
    fun addOneExistingProductIfItIsPossible() {
        assertEquals(true, Utils.addToCart(products[7], 1))
        assertEquals(true, Utils.addToCart(products[7], 1))
        assertEquals(1, Utils.getCart().size)
        assertEquals(2, Utils.getCart()[0].quantity)
    }

    @Test
    fun addExistingProductIfMaxQuantityIsAlreadyInCart() {
        assertEquals(true, Utils.addToCart(products[7], 5))
        assertEquals(false, Utils.addToCart(products[7], 1))
        assertEquals(1, Utils.getCart().size)
        assertEquals(5, Utils.getCart()[0].quantity)
    }

    @Test
    fun notAddNewProductIfMaxQuantityWillBeExceeded() {
        assertEquals(false, Utils.addToCart(products[7], 6))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun addNewProductIfMaxQuantityWillBeReached() {
        assertEquals(true, Utils.addToCart(products[7], 5))
        assertEquals(1, Utils.getCart().size)
        assertEquals(5, Utils.getCart()[0].quantity)
    }

    @Test
    fun notAddZeroProducts() {
        assertEquals(false, Utils.addToCart(products[0], 0))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun notAddNegativeProducts() {
        assertEquals(false, Utils.addToCart(products[0], -1))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun notAddProductWhichIsOutOfStock() {
        assertEquals(false, Utils.addToCart(products[8], 1))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun addEveryPossibleProductOnce() {
        for (product in products.filter { it.inStock > 0 })
            assertEquals(true, Utils.addToCart(product, 1))
        assertEquals(products.filter { it.inStock > 0 }.size, Utils.getCart().size)
        for (product in products.filter { it.inStock > 0 })
            assertEquals(1, Utils.getCart().first { it.product == product }.quantity)
    }

    @Test
    fun addEveryPossibleProductMaxQuantity() {
        for (product in products.filter { it.inStock > 0 })
            assertEquals(true, Utils.addToCart(product, product.inStock))
        assertEquals(products.filter { it.inStock > 0 }.size, Utils.getCart().size)
        for (product in products.filter { it.inStock > 0 })
            assertEquals(product.inStock, Utils.getCart().first { it.product == product }.quantity)
    }

    @Test
    fun removeEveryPossibleProductOnce() {
        addEveryPossibleProductOnce()
        for (product in products.filter { it.inStock > 0 })
            assertEquals(true, Utils.removeFromCart(product, 1))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun removeEveryPossibleProductMaxQuantity() {
        addEveryPossibleProductMaxQuantity()
        for (product in products.filter { it.inStock > 0 })
            assertEquals(true, Utils.removeFromCart(product, product.inStock))
        assertEquals(0, Utils.getCart().size)
    }

    @Test
    fun addAndRemoveSeveralProductsSeveralTimes() {
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(true, Utils.addToCart(products[1], 1))
        assertEquals(false, Utils.addToCart(products[2], 4))
        assertEquals(false, Utils.addToCart(products[3], 4))
        assertEquals(true, Utils.addToCart(products[7], 5))
        assertEquals(false, Utils.removeFromCart(products[0], 2))
        assertEquals(true, Utils.removeFromCart(products[0], 1))
        assertEquals(true, Utils.addToCart(products[0], 1))
        assertEquals(false, Utils.addToCart(products[0], 1))
        assertEquals(true, Utils.removeFromCart(products[0], 1))
        assertEquals(true, Utils.addToCart(products[5], 2))
        assertEquals(true, Utils.addToCart(products[6], 1))
        assertEquals(false, Utils.removeFromCart(products[7], 0))
        assertEquals(true, Utils.removeFromCart(products[7], 1))
        assertEquals(true, Utils.removeFromCart(products[7], 4))
        assertEquals(false, Utils.removeFromCart(products[7], 1))
        assertEquals(true, Utils.removeFromCart(products[5], 1))
        assertEquals(false, Utils.removeFromCart(products[5], 2))
        assertEquals(true, Utils.removeFromCart(products[5], 1))
        assertEquals(false, Utils.removeFromCart(products[5], 1))
    }
}

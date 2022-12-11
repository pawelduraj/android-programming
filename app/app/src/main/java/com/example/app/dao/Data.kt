package com.example.app.dao

import com.example.app.models.*
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

object Data {
    fun initDataIfEmpty(realm: Realm) {
        if (!this.isEmpty(realm))
            return

        realm.writeBlocking {
            val categoriesToDelete: RealmResults<Category> = this.query<Category>().find()
            this.delete(categoriesToDelete)

            val productsToDelete: RealmResults<Product> = this.query<Product>().find()
            this.delete(productsToDelete)

            val cartProductsToDelete: RealmResults<CartProduct> = this.query<CartProduct>().find()
            this.delete(cartProductsToDelete)

            val categories = listOf(Category("3x3x3"), Category("4x4x4"), Category("5x5x5"))
            val copiedCategories: MutableList<Category> = mutableListOf()
            categories.forEach { category -> copiedCategories.add(this.copyToRealm(category)) }

            // @formatter:off
            val products = listOf(
                Product("Gan 13 M MagLev", copiedCategories[0], 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("MoYu RS3 M 2020", copiedCategories[0], 899, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("YuXin Little Magic", copiedCategories[0], 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("DaYan TengYun M", copiedCategories[0], 2299, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("YJ MGC", copiedCategories[1], 1999, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("MoYu AoSu WR M", copiedCategories[1], 4399, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("YJ MGC", copiedCategories[2], 2299, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("YuXin Cloud", copiedCategories[2], 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product("MoYu AoChuang GTS M", copiedCategories[2], 3999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            )
            // @formatter:on

            products.forEach { product -> this.copyToRealm(product) }
        }
    }

    private fun isEmpty(realm: Realm): Boolean {
        var result = false

        realm.writeBlocking {
            val category = this.query<Category>().first().find()
            val product = this.query<Product>().first().find()
            result = category == null || product == null
        }

        return result
    }
}

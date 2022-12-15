package com.example

import com.example.models.*;

class SampleData {
    fun getCategories(): List<Category> {
        return listOf(
            Category(1, "3x3x3"),
            Category(2, "4x4x4"),
            Category(3, "5x5x5")
        )
    }

    fun getProducts(): List<Product> {
        return listOf(
            // @formatter:off
            Product(1, "Gan 13 M MagLev", 1, 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(2, "MoYu RS3 M 2020", 1, 899, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(3, "YuXin Little Magic", 1, 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(4, "DaYan TengYun M", 1, 2299, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(5, "YJ MGC", 2, 1999, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(6, "MoYu AoSu WR M", 2, 4399, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(7, "YJ MGC", 3, 2299, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(8, "YuXin Cloud", 3, 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(9, "MoYu AoChuang GTS M", 3, 3999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            // @formatter:on
        )
    }
}

package com.example

import com.example.models.*

@Suppress("SpellCheckingInspection")
object SampleData {
    fun getCategories(): List<Category> {
        return listOf(
            Category(-99, "3x3x3"),
            Category(-98, "4x4x4"),
            Category(-97, "5x5x5")
        )
    }

    fun getProducts(): List<Product> {
        return listOf(
            // @formatter:off
            Product(-99, "Gan 13 M MagLev", -99, 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-98, "MoYu RS3 M 2020", -99, 899, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-97, "YuXin Little Magic", -99, 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-96, "DaYan TengYun M", -99, 2299, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-95, "YJ MGC", -98, 1999, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-94, "MoYu AoSu WR M", -98, 4399, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-93, "YJ MGC", -97, 2299, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-92, "YuXin Cloud", -97, 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-91, "MoYu AoChuang GTS M", -97, 3999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            // @formatter:on
        )
    }

    fun getUsers(): List<User> {
        return listOf(
            User(-99, "Admin", "admin@example.com", "admin", true),
            User(-98, "User", "user@example.com", "user", false)
        )
    }

    fun getOrders(): List<Order> {
        return listOf(
            Order(-99, -98, "2022-01-06 12:00:00", 9797, false, ""),
            Order(-98, -98, "2022-01-06 13:00:00", 5997, true, "314159265358979323")
        )
    }

    fun getOrderDetails(): List<OrderDetail> {
        return listOf(
            OrderDetail(-99, -99, 1, 7999),
            OrderDetail(-99, -98, 2, 1798),
            OrderDetail(-98, -91, 1, 3999),
            OrderDetail(-98, -92, 2, 1998)
        )
    }
}

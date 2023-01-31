package com.example.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.dao.Database
import com.example.app.models.*
import io.mockk.*

import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        mockkObject(Database)
        // @formatter:off
        every { Database.getProducts() } returns listOf(
            Product(-99, "Gan 13 M MagLev", Category(-99, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-98, "MoYu RS3 M 2020", Category(-99, "3x3x3"), 899, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-97, "YuXin Little Magic", Category(-99, "3x3x3"), 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-96, "DaYan TengYun M", Category(-99, "3x3x3"), 2299, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-95, "YJ MGC 4", Category(-98, "4x4x4"), 1999, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-94, "MoYu AoSu WR M", Category(-98, "4x4x4"), 4399, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-93, "YJ MGC 5", Category(-97, "5x5x5"), 2299, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-92, "YuXin Cloud", Category(-97, "5x5x5"), 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-91, "MoYu AoChuang GTS M", Category(-97, "5x5x5"), 3999, 0, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        )
        // @formatter:on
        every { Database.getUser() } returns User(0, "Test", "test@example.com", false, "jwt")
        onView(withId(R.id.navigation_products)).perform(click())
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun displayProductsTest() {
        onView(withText("Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText("MoYu RS3 M 2020")).check(matches(isDisplayed()))
        onView(withText("YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText("DaYan TengYun M")).check(matches(isDisplayed()))
        onView(withText("YJ MGC 4")).check(matches(isDisplayed()))
        onView(withText("MoYu AoSu WR M")).check(matches(isDisplayed()))
        onView(withText("YJ MGC 5")).check(matches(isDisplayed()))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("MoYu AoChuang GTS M")).check(matches(isDisplayed()))
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Products"))))
    }

    @Test
    fun addProductToCartTest() {
        every { Database.addToCart(any(), any()) } returns true
        // @formatter:off
        every {Database.getCartProducts() } returns listOf(CartProduct(Product(-99, "Gan 13 M MagLev", Category(-99, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."), 1))
        // @formatter:on
        onView(withText("79.99")).perform(scrollTo())
        onView(withText("79.99")).perform(click())
        checkIfProductIsInCart("Gan 13 M MagLev", 7999, 1)
    }

    @Test
    fun addProductOnceToCartIfClickedTwiceTest() {
        every { Database.addToCart(any(), any()) } returns true
        // @formatter:off
        every {Database.getCartProducts() } returns listOf(CartProduct(Product(-99, "Gan 13 M MagLev", Category(-99, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."), 1))
        // @formatter:on
        onView(withText("79.99")).perform(scrollTo())
        onView(withText("79.99")).perform(click())
        every { Database.addToCart(any(), any()) } returns false
        onView(withText("79.99")).perform(click())
        checkIfProductIsInCart("Gan 13 M MagLev", 7999, 1)
    }

    @Test
    fun addProductToCartTwiceTest() {
        every { Database.addToCart(any(), any()) } returns true
        // @formatter:off
        every {Database.getCartProducts() } returns listOf(CartProduct(Product(-97, "YuXin Little Magic", Category(-99, "3x3x3"), 599, 3, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."), 2))
        // @formatter:on
        onView(withText("5.99")).perform(scrollTo())
        onView(withText("5.99")).perform(click())
        onView(withText("5.99")).perform(click())
        checkIfProductIsInCart("YuXin Little Magic", 599, 2)
    }

    @Test
    fun notAddProductOutOfStockToCartTest() {
        every { Database.addToCart(any(), any()) } returns false
        // @formatter:off
        every {Database.getCartProducts() } returns listOf()
        // @formatter:on
        onView(withText("39.99")).perform(scrollTo())
        onView(withText("39.99")).perform(click())
        try {
            checkIfProductIsInCart("MoYu AoChuang GTS M", 3999, 1)
            Assert.fail("Product is out of stock, but it was added to the cart")
        } catch (_: Exception) {
        }
    }

    @Test
    fun openProductInStockDetailsTest() {
        onView(withText("Gan 13 M MagLev")).perform(scrollTo())
        onView(withText("Gan 13 M MagLev")).perform(click())
        onView(withId(R.id.name)).check(matches(withText("Gan 13 M MagLev")))
        onView(withId(R.id.category)).check(matches(withText("3x3x3")))
        onView(withId(R.id.price)).check(matches(withText("79.99")))
        onView(withId(R.id.description)).check(matches(withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")))
    }

    @Test
    fun openProductOutOfStockDetailsTest() {
        onView(withText("MoYu AoChuang GTS M")).perform(scrollTo())
        onView(withText("MoYu AoChuang GTS M")).perform(click())
        onView(withId(R.id.name)).check(matches(withText("MoYu AoChuang GTS M")))
        onView(withId(R.id.category)).check(matches(withText("5x5x5")))
        onView(withId(R.id.price)).check(matches(withText("39.99")))
        onView(withId(R.id.description)).check(matches(withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")))
    }

    @Test
    fun returnToProductsFromProductDetailsOnBackPressedTest() {
        openProductInStockDetailsTest()
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun notAddProductToCartFromDetailsTest() {
        onView(withText("Gan 13 M MagLev")).perform(click())
        onView(withId(R.id.price)).perform(click())
        onView(isRoot()).perform(pressBack())
        try {
            checkIfProductIsInCart("Gan 13 M MagLev", 7999, 1)
            Assert.fail("Product was added to the cart from details, but it shouldn't")
        } catch (_: Exception) {
        }
    }

    @Test
    fun hideFloatingActionButtonTest() {
        try {
            onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()))
            Assert.fail("Floating action button is displayed, but it shouldn't")
        } catch (_: Exception) {
        }
    }

    private fun checkIfProductIsInCart(name: String, price: Int, quantity: Int) {
        val partial = quantity.toString() + " x " + (price / 100.0).toString()
        val total = " = " + (price * quantity / 100.0).toString()
        onView(withId(R.id.navigation_cart)).perform(click())
        onView(withText(name)).check(matches(isDisplayed()))
        onView(withText(partial)).check(matches(isDisplayed()))
        onView(withText(total)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_products)).perform(click())
    }

    private fun refresh() {
        onView(withId(R.id.navigation_categories)).perform(click())
        onView(withId(R.id.navigation_products)).perform(click())
    }
}

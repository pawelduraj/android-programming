package com.example.app

import androidx.test.core.app.ActivityScenario
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
class CartInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // @formatter:off
    private val products: List<Product> = listOf(
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

    @Before
    fun before() {
        mockkObject(Database)
        onView(withId(R.id.navigation_cart)).perform(click())
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun displayCartProductsTest() {
        // @formatter:off
        mockCartProducts(listOf(CartProduct(products[0], 1), CartProduct(products[2], 2), CartProduct(products[7], 5)))
        // @formatter:on
        onView(withText("Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText("YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
    }

    @Test
    fun displayCartProductsWithPartialPricesTest() {
        // @formatter:off
        mockCartProducts(listOf(CartProduct(products[0], 1), CartProduct(products[2], 2), CartProduct(products[7], 5)))
        // @formatter:on
        onView(withText("Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText("1 x 79.99")).check(matches(isDisplayed()))
        onView(withText("YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText("2 x 5.99")).check(matches(isDisplayed()))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("5 x 9.99")).check(matches(isDisplayed()))
    }

    @Test
    fun displayCartProductsWithTotalPricesTest() {
        // @formatter:off
        mockCartProducts(listOf(CartProduct(products[0], 1), CartProduct(products[2], 2), CartProduct(products[7], 5)))
        // @formatter:on
        onView(withText("Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText(" = 79.99")).check(matches(isDisplayed()))
        onView(withText("YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText(" = 11.98")).check(matches(isDisplayed()))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText(" = 49.95")).check(matches(isDisplayed()))
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Cart"))))
    }

    @Test
    fun openProductDetailsTest() {
        // @formatter:off
        mockCartProducts(listOf(CartProduct(products[0], 1), CartProduct(products[2], 2), CartProduct(products[7], 5)))
        // @formatter:on
        onView(withText("Gan 13 M MagLev")).perform(click())
        onView(withId(R.id.name)).check(matches(withText("Gan 13 M MagLev")))
        onView(withId(R.id.category)).check(matches(withText("3x3x3")))
        onView(withId(R.id.price)).check(matches(withText("79.99")))
        onView(withId(R.id.description)).check(matches(withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")))
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun returnToCartFromProductDetailsOnBackPressedTest() {
        openProductDetailsTest()
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun notIncreaseProductsInCartIfMaxQuantityReachedTest() {
        mockCartProducts(listOf(CartProduct(products[7], 5)))
        every { Database.addToCart(any(), any()) } returns false
        onView(withId(R.id.add)).perform(click())
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("5 x 9.99")).check(matches(isDisplayed()))
        onView(withText(" = 49.95")).check(matches(isDisplayed()))
    }

    @Test
    fun increaseProductsInCartIfMaxQuantityNotReachedTest() {
        mockCartProducts(listOf(CartProduct(products[7], 4)))
        every { Database.addToCart(any(), any()) } returns true
        onView(withText("+")).perform(click())
        mockCartProducts(listOf(CartProduct(products[7], 5)))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("5 x 9.99")).check(matches(isDisplayed()))
        onView(withText(" = 49.95")).check(matches(isDisplayed()))
    }

    @Test
    fun decreaseProductsInCartIfAtLeastOneLeftTest() {
        mockCartProducts(listOf(CartProduct(products[7], 2)))
        every { Database.removeFromCart(any(), any()) } returns true
        onView(withText("-")).perform(click())
        mockCartProducts(listOf(CartProduct(products[7], 1)))
        onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("1 x 9.99")).check(matches(isDisplayed()))
        onView(withText(" = 9.99")).check(matches(isDisplayed()))
    }

    @Test
    fun removeProductFromCartIfNoMoreLeftTest() {
        mockCartProducts(listOf(CartProduct(products[7], 1)))
        every { Database.removeFromCart(any(), any()) } returns false
        onView(withText("-")).perform(click())
        mockCartProducts(listOf())
        try {
            onView(withText("YuXin Cloud")).check(matches(isDisplayed()))
            Assert.fail("Product should not be displayed anymore")
        } catch (_: Exception) {
        }
    }

    @Test
    fun buyProductsSuccessTest() {
        // @formatter:off
        mockCartProducts(listOf(CartProduct(products[0], 1), CartProduct(products[2], 2), CartProduct(products[7], 5)))
        every { Database.buy() } returns true
        every { Database.getOrders() } returns listOf(Order(0, 0, "2023-01-01 12:00:00", 14192, false, "payment"))
        // @formatter:on
        onView(withId(R.id.button_buy)).perform(click())
        onView(withId(R.id.navigation_orders)).perform(click())
        onView(withText("Order ID: 0")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 141.92")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
    }

    @Test
    fun buyProductsCorrectDetailsTest() {
        buyProductsSuccessTest()
        // @formatter:off
        every { Database.getProductsByOrderId(0) } returns listOf(
            Product(-99, "Gan 13 M MagLev", Category(-99, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-97, "YuXin Little Magic", Category(-99, "3x3x3"), 599, 2, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            Product(-92, "YuXin Cloud", Category(-97, "5x5x5"), 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        )
        // @formatter:on
        onView(withText("Order ID: 0")).perform(click())
        onView(withText("Order ID: 0")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 141.92")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
        onView(withText("1 x Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText("79.99")).check(matches(isDisplayed()))
        onView(withText("2 x YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText("11.98")).check(matches(isDisplayed()))
        onView(withText("5 x YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("49.95")).check(matches(isDisplayed()))
    }

    @Test
    fun buyProductsFailTest() {
        mockCartProducts(listOf())
        every { Database.buy() } returns false
        every { Database.getOrders() } returns listOf()
        onView(withId(R.id.button_buy)).perform(click())
        onView(withId(R.id.navigation_orders)).perform(click())
        try {
            onView(withId(R.id.order)).check(matches(isDisplayed()))
            Assert.fail("Order should not be displayed")
        } catch (_: Exception) {
        }
    }

    private fun mockCartProducts(cartProducts: List<CartProduct>) {
        unmockkAll()
        mockkObject(Database)
        every { Database.getProducts() } returns products
        every { Database.getCartProducts() } returns cartProducts
        activityRule.scenario.close()
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.navigation_cart)).perform(click())
    }
}

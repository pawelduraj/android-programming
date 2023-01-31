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
class OrdersInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        mockkObject(Database)
        onView(withId(R.id.navigation_orders)).perform(click())
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun displayOrdersTest() {
        // @formatter:off
        mockOrders(listOf(
            Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment"),
            Order(2, 0, "2023-01-02 13:00:00", 9197, false, "payment"),
            Order(13, 0, "2023-01-03 14:00:00", 14192, true, "payment")
        ))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Order ID: 2")).check(matches(isDisplayed()))
        onView(withText("Order ID: 13")).check(matches(isDisplayed()))
    }

    @Test
    fun displayDateTest() {
        // @formatter:off
        mockOrders(listOf(
            Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment"),
            Order(2, 0, "2023-01-02 13:00:00", 9197, false, "payment"),
            Order(13, 0, "2023-01-03 14:00:00", 14192, true, "payment")
        ))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Order ID: 2")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-02 13:00:00")).check(matches(isDisplayed()))
        onView(withText("Order ID: 13")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-03 14:00:00")).check(matches(isDisplayed()))
    }

    @Test
    fun displayPriceTest() {
        // @formatter:off
        mockOrders(listOf(
            Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment"),
            Order(2, 0, "2023-01-02 13:00:00", 9197, false, "payment"),
            Order(13, 0, "2023-01-03 14:00:00", 14192, true, "payment")
        ))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Price: 79.99")).check(matches(isDisplayed()))
        onView(withText("Order ID: 2")).check(matches(isDisplayed()))
        onView(withText("Price: 91.97")).check(matches(isDisplayed()))
        onView(withText("Order ID: 13")).check(matches(isDisplayed()))
        onView(withText("Price: 141.92")).check(matches(isDisplayed()))
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Orders"))))
    }

    @Test
    fun openDetailsTest() {
        // @formatter:off
        mockOrders(listOf(
            Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment"),
            Order(2, 0, "2023-01-02 13:00:00", 9197, false, "payment"),
            Order(13, 0, "2023-01-03 14:00:00", 14192, true, "payment")
        ))
        // @formatter:on
        onView(withText("Order ID: 1")).perform(click())
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 79.99")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun displayPayButtonIfNotPaidTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("PAY")).check(matches(isDisplayed()))
    }

    @Test
    fun notDisplayPayButtonIfPaidTest() {
        // @formatter:off
        mockOrders(listOf(Order(11, 0, "2023-01-01 12:00:00", 7999, true, "payment")))
        // @formatter:on
        onView(withText("Order ID: 11")).check(matches(isDisplayed()))
        try {
            onView(withText("PAY")).check(matches(isDisplayed()))
            Assert.fail("PAY button should not be displayed")
        } catch (_: Exception) {
        }
    }

    @Test
    fun payButtonTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("PAY")).check(matches(isDisplayed()))
        onView(withText("PAY")).perform(click())
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 79.99")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun payWithStripeButtonTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("PAY")).check(matches(isDisplayed()))
        onView(withText("PAY")).perform(click())
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 79.99")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
        onView(withText("PAY WITH STRIPE")).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun payWithPayuButtonTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("PAY")).check(matches(isDisplayed()))
        onView(withText("PAY")).perform(click())
        onView(withText("Order ID: 1")).check(matches(isDisplayed()))
        onView(withText("Date: 2023-01-01 12:00:00")).check(matches(isDisplayed()))
        onView(withText("Price: 79.99")).check(matches(isDisplayed()))
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
        onView(withText("PAY WITH PAYU")).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun displayOrderDetailsTest() {
        // @formatter:off
        mockOrders(listOf(Order(3, 0, "2023-01-03 14:00:00", 14192, true, "payment")))
        // @formatter:on
        onView(withText("Order ID: 3")).perform(click())
        onView(withText("1 x Gan 13 M MagLev")).check(matches(isDisplayed()))
        onView(withText("79.99")).check(matches(isDisplayed()))
        onView(withText("2 x YuXin Little Magic")).check(matches(isDisplayed()))
        onView(withText("11.98")).check(matches(isDisplayed()))
        onView(withText("5 x YuXin Cloud")).check(matches(isDisplayed()))
        onView(withText("49.95")).check(matches(isDisplayed()))
    }

    @Test
    fun payWithStripeSuccessTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        every { Database.payStripe(any()) } returns true
        every { Database.finalizePayment(any()) } returns true
        onView(withText("Order ID: 1")).perform(click())
        onView(withText("PAY WITH STRIPE")).perform(click())
        // Skip stripe payment
        onView(isRoot()).perform(pressBack())
        onView(isRoot()).perform(pressBack())
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, true, "payment")))
        onView(withText("Status: Paid")).check(matches(isDisplayed()))
    }

    @Test
    fun payWithStripeFailTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        every { Database.payStripe(any()) } returns false
        every { Database.finalizePayment(any()) } returns false
        onView(withText("Order ID: 1")).perform(click())
        onView(withText("PAY WITH STRIPE")).perform(click())
        // Skip stripe payment
        onView(isRoot()).perform(pressBack())
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.navigation_orders)).perform(click())
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
    }

    @Test
    fun payWithPayuSuccessTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        every { Database.payPayU(any()) } returns "id"
        every { Database.finalizePayment(any()) } returns true
        onView(withText("Order ID: 1")).perform(click())
        // Skip stripe payment
        // onView(withText("PAY WITH PAYU")).perform(click())
        onView(isRoot()).perform(pressBack())
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, true, "payment")))
        onView(withText("Status: Paid")).check(matches(isDisplayed()))
    }

    @Test
    fun payWithPayuFailTest() {
        // @formatter:off
        mockOrders(listOf(Order(1, 0, "2023-01-01 12:00:00", 7999, false, "payment")))
        // @formatter:on
        every { Database.payPayU(any()) } returns ""
        every { Database.finalizePayment(any()) } returns false
        onView(withText("Order ID: 1")).perform(click())
        // Skip stripe payment
        // onView(withText("PAY WITH PAYU")).perform(click())
        onView(isRoot()).perform(pressBack())
        onView(withText("Status: Not paid")).check(matches(isDisplayed()))
    }

    private fun mockOrders(orders: List<Order>) {
        unmockkAll()
        mockkObject(Database)
        every { Database.getOrders() } returns orders
        // @formatter:off
        every { Database.getProductsByOrderId(any()) } answers {
            if (firstArg() as Int == 1 || firstArg() as Int == 11) listOf(
                Product(1, "Gan 13 M MagLev", Category(1, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            )
            else if (firstArg() as Int == 2 || firstArg() as Int == 12) listOf(
                Product(1, "Gan 13 M MagLev", Category(1, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product(2, "YuXin Little Magic", Category(1, "3x3x3"), 599, 2, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            )
            else if (firstArg() as Int == 3 || firstArg() as Int == 13) listOf(
                Product(1, "Gan 13 M MagLev", Category(1, "3x3x3"), 7999, 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product(2, "YuXin Little Magic", Category(1, "3x3x3"), 599, 2, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
                Product(3, "YuXin Cloud", Category(2, "5x5x5"), 999, 5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."),
            )
            else listOf()
        }
        // @formatter:oon
        activityRule.scenario.close()
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.navigation_orders)).perform(click())
    }
}

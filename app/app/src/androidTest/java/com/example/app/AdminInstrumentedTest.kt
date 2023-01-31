package com.example.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.dao.Database
import com.example.app.models.*
import io.mockk.*
import org.hamcrest.Matchers.anything

import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        mockkObject(Database)
        // @formatter:off
        every { Database.getProducts() } returns listOf()
        every { Database.getCategories() } returns listOf(Category(1, "3x3x3"), Category(2, "4x4x4"))
        every { Database.getUser() } returns User(0, "Test", "test@example.com", true, "jwt")
        // @formatter:on
        onView(withId(R.id.navigation_products)).perform(click())
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Products"))))
    }

    @Test
    fun displayFloatingActionButtonTest() {
        onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()))
    }

    @Test
    fun returnToProductsListIfValidDataSubmittedTest() {
        every { Database.addNewProduct(any(), any(), any(), any(), any()) } returns true
        onView(withId(R.id.floatingActionButton)).perform(click())
        onView(withId(R.id.editTextName)).perform(typeText("Test"))
        onView(withId(R.id.spinnerCategory)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.editTextPrice)).perform(typeText("999"))
        onView(withId(R.id.editTextInStock)).perform(typeText("2"))
        onView(withId(R.id.editTextMultilineDescription)).perform(typeText("Test description"))
        onView(withText("CREATE NEW PRODUCT")).perform(click())
        onView(withId(R.id.navigation_products)).check(matches(isDisplayed()))
    }

    @Test
    fun stayOnNewProductActivityIfInvalidDataSubmittedTest() {
        every { Database.addNewProduct(any(), any(), any(), any(), any()) } returns false
        onView(withId(R.id.floatingActionButton)).perform(click())
        onView(withId(R.id.editTextName)).perform(typeText("Test"))
        onView(withId(R.id.spinnerCategory)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.editTextPrice)).perform(typeText("999"))
        onView(withId(R.id.editTextInStock)).perform(typeText("2"))
        onView(withId(R.id.editTextMultilineDescription)).perform(typeText("Test description"))
        onView(withText("CREATE NEW PRODUCT")).perform(click())
        onView(withId(R.id.editTextName)).check(matches(isDisplayed()))
    }

    @Test
    fun createNewValidProductTest() {
        every { Database.addNewProduct(any(), any(), any(), any(), any()) } returns true
        onView(withId(R.id.floatingActionButton)).perform(click())
        onView(withId(R.id.editTextName)).perform(typeText("Test"))
        onView(withId(R.id.spinnerCategory)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.editTextPrice)).perform(typeText("999"))
        onView(withId(R.id.editTextInStock)).perform(typeText("2"))
        onView(withId(R.id.editTextMultilineDescription)).perform(typeText("Test description"))
        onView(withText("CREATE NEW PRODUCT")).perform(click())
        unmockkAll()
        mockkObject(Database)
        // @formatter:off
        every { Database.getProducts() } returns listOf(Product(1, "Test", Category(1, "4x4x4"), 999, 2, "Test description"))
        every { Database.getCategories() } returns listOf(Category(1, "3x3x3"), Category(2, "4x4x4"))
        every { Database.getUser() } returns User(0, "Test", "test@example.com", true, "jwt")
        // @formatter:on
        activityRule.scenario.close()
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.navigation_products)).perform(click())
        onView(withText("Test")).check(matches(isDisplayed()))
        onView(withText("4x4x4")).check(matches(isDisplayed()))
        onView(withText("9.99")).check(matches(isDisplayed()))
    }
}

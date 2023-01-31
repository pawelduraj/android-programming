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
class CategoriesInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        mockkObject(Database)
        every { Database.getCategories() } returns listOf(
            Category(-99, "3x3x3"),
            Category(-98, "4x4x4"),
            Category(-97, "5x5x5")
        )
        onView(withId(R.id.navigation_categories)).perform(click())
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun displayCategoriesTest() {
        onView(withText("3x3x3")).check(matches(isDisplayed()))
        onView(withText("4x4x4")).check(matches(isDisplayed()))
        onView(withText("5x5x5")).check(matches(isDisplayed()))
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Categories"))))
    }
}

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
class MapInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        onView(withId(R.id.navigation_account)).perform(click())
        onView(withId(R.id.floatingActionButton)).perform(click())
    }

    @After
    fun after() {
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun displayMapTest() {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Map"))))
    }
}

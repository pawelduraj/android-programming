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
class AuthInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        mockkObject(Database)
        every { Database.signIn(any(), any()) } returns false
        every { Database.signUp(any(), any(), any()) } returns false
        justRun { Database.signOut() }
        every { Database.signInWithGithub(any()) } returns false
        every { Database.signInWithGoogle(any()) } returns false
        every { Database.getUser() } returns null

        onView(withId(R.id.navigation_account)).perform(click())
        try {
            onView(withId(R.id.buttonSignOut)).perform(click())
        } catch (_: Exception) {
        }
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun titleTest() {
        onView(withId(androidx.appcompat.R.id.action_bar))
            .check(matches(hasDescendant(withText("Account"))))
    }

    @Test
    fun loginWithValidAccountTest() {
        every { Database.signIn(any(), any()) } returns true
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"))
        onView(withId(R.id.editTextPassword)).perform(typeText("test"))
        onView(withId(R.id.editTextPassword)).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        every { Database.getUser() } returns User(0, "Test", "test@example.com", false, "jwt")
        refresh()
        onView(withId(R.id.textViewUserId)).check(matches(withText("User ID: 0")))
        onView(withId(R.id.textViewName)).check(matches(withText("Name: Test")))
        onView(withId(R.id.textViewEmail)).check(matches(withText("Email: test@example.com")))
        onView(withId(R.id.textViewAdmin)).check(matches(withText("Admin: false")))
    }

    @Test
    fun loginWithInvalidAccountTest() {
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"))
        onView(withId(R.id.editTextPassword)).perform(typeText("test"))
        onView(withId(R.id.editTextPassword)).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        refresh()
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun switchToSignUpTest() {
        onView(withId(R.id.buttonChange)).perform(click())
        onView(withId(R.id.editTextName)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonAuth)).check(matches(withText("Sign Up")))
    }

    @Test
    fun signUpWithValidAccountTest() {
        every { Database.signUp(any(), any(), any()) } returns true
        onView(withId(R.id.buttonChange)).perform(click())
        onView(withId(R.id.editTextName)).perform(typeText("Test"))
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"))
        onView(withId(R.id.editTextPassword)).perform(typeText("test"))
        onView(withId(R.id.editTextPassword)).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        onView(withId(R.id.buttonAuth)).check(matches(withText("Sign In")))
    }

    @Test
    fun signUpWithInvalidAccountTest() {
        onView(withId(R.id.buttonChange)).perform(click())
        onView(withId(R.id.editTextName)).perform(typeText("Test"))
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"))
        onView(withId(R.id.editTextPassword)).perform(typeText("test"))
        onView(withId(R.id.editTextPassword)).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        onView(withId(R.id.buttonAuth)).check(matches(withText("Sign Up")))
    }

    @Test
    fun signOutTest() {
        every { Database.signIn(any(), any()) } returns true
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"))
        onView(withId(R.id.editTextPassword)).perform(typeText("test"))
        onView(withId(R.id.editTextPassword)).perform(closeSoftKeyboard())
        onView(withId(R.id.buttonAuth)).perform(click())
        every { Database.getUser() } returns User(0, "Test", "test@example.com", false, "jwt")
        refresh()
        onView(withId(R.id.buttonSignOut)).perform(click())
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonAuth)).check(matches(withText("Sign In")))
    }

    private fun refresh() {
        onView(withId(R.id.navigation_categories)).perform(click())
        onView(withId(R.id.navigation_account)).perform(click())
    }
}

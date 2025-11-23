package com.example.weathertrace

import androidx.compose.ui.test.assert
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import androidx.test.ext.junit.rules.ActivityScenarioRule


import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class InstrumentedTest {


    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.weathertrace", appContext.packageName)
    }

    fun stepSlow() {
        composeRule.mainClock.advanceTimeBy(50)     // petite tranche
        Thread.sleep(50)                            // pause réelle (visible humainement)
    }


    @Test
    fun testNavigation(){
        composeRule.onNodeWithTag("HomeScreen").assertExists()
        composeRule.onNodeWithTag("SearchTopBar").assertExists()
        composeRule.onNodeWithTag("MenuButton").assertExists()
        composeRule.onNodeWithTag("MenuButton").performClick()

        composeRule.onNodeWithText("Settings").assertExists()
        composeRule.onNodeWithText("Settings").performClick()

        composeRule.onNodeWithTag("ComeBackArrow").assertExists()
        composeRule.onNodeWithTag("ComeBackArrow").performClick()

        composeRule.onNodeWithTag("HomeScreen").assertExists()
        composeRule.onNodeWithTag("SearchTopBar").assertExists()
        composeRule.onNodeWithTag("MenuButton").assertExists()
        composeRule.onNodeWithTag("MenuButton").performClick()

        composeRule.onNodeWithText("Doc").assertExists()
        composeRule.onNodeWithText("Doc").performClick()

        composeRule.onNodeWithTag("ComeBackArrow").assertExists()
        composeRule.onNodeWithTag("ComeBackArrow").performClick()
    }
}
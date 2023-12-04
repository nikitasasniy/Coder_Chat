package com.example.lab_5


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test


@RunWith(AndroidJUnit4::class)
class MessageCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun messageCardExpandedTest() {
        // Given
        val message = Message("user", " test.")
        // When
        composeTestRule.setContent {
            MessageCard(msg = message, userInfos = emptyList())
        }
        // Then
        composeTestRule.onNodeWithText(" test.").assertDoesNotExist()
        // When
        composeTestRule.onNodeWithText(" test.").performClick()
        // Then
        composeTestRule.onNodeWithText(" test.").assertIsDisplayed()
    }
}
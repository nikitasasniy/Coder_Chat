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
    fun messageCardDisplayTest() {
        val message = Message("user1", "Hello, this is a long message.")
        val userInfos = listOf(UserInfo("user1", "John"))

        composeTestRule.setContent {
            MessageCard(message, userInfos, isExpanded = { false })
        }

        composeTestRule.onNodeWithText("John").assertIsDisplayed() // отображение ника
        composeTestRule.onNodeWithText("Hello, this is a long message.").assertIsDisplayed() // отображение сообщения

    }

    @Test
    fun messageCardClickTest() {
        // Given
        val message = Message("user1", "Hello, this is a message.")
        val userInfos = listOf(UserInfo("user1", "John"))

        // When
        composeTestRule.setContent {
            MessageCard(message, userInfos, isExpanded = { false })
        }

        // Then
        // Проверим, что изначально isExpanded = false
        composeTestRule.onNodeWithTag("messageCardFalse").assertIsDisplayed()

        // Вызываем performClick() для имитации клика
        composeTestRule.onNodeWithTag("messageCardFalse").performClick()

        // Даем немного времени для анимации
        Thread.sleep(1000)

        // Проверим, что после клика isExpanded = true
        composeTestRule.onNodeWithTag("textAnimate").assertIsDisplayed()
//        Thread.sleep(5000)
//        composeTestRule.onNodeWithTag("textExtensionAnimate").assertIsDisplayed()
    }

    @Test
    fun messageCardEmptyTextTest() {
        val emptyMessage = Message("user1", "")
        val userInfos = listOf(UserInfo("user1", "John"))

        composeTestRule.setContent {
            MessageCard(emptyMessage, userInfos, isExpanded = { false })
        }

        composeTestRule.onNodeWithText("Hello, this is a long message.").assertDoesNotExist() // проверка отображения пустого сообщения
    }




}
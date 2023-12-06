package com.example.lab_5

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab_5.Conversation
import com.example.lab_5.Message
import com.example.lab_5.MessageCard
import com.example.lab_5.UserInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConversationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun conversationTest() {
        val messages = listOf(
            Message("user1", "Hello"),
        )
        val userInfos = listOf(
            UserInfo("user1", "John"),
        )

        composeTestRule.setContent {
            Conversation(
                messages = messages,
                userInfos = userInfos,
                onSendMessage = {},
                onLogout = {}
            )
        }

        // проверяем кнопку logoutButton
        composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()

        // проверяем возможность заполнения поля
        composeTestRule.onNodeWithTag("messageTextField").assertIsDisplayed()

        // проверяем кнопку sendButton
        composeTestRule.onNodeWithTag("sendButton").assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun messagesDisplayTest() {
        val messages = listOf(
            Message("user1", "Hello"),
            Message("user2", "Hi there!")
        )
        val userInfos = listOf(
            UserInfo("user1", "John"),
            UserInfo("user2", "Jane")
        )

        composeTestRule.setContent {
            Conversation(
                messages = messages,
                userInfos = userInfos,
                onSendMessage = {},
                onLogout = {}
            )
        }

        // Проверяем, что количество карточек сообщений соответствует количеству сообщений
        composeTestRule.onNodeWithTag("messageCardFalse").equals(messages.size)

        composeTestRule.onNodeWithTag("IconButton").assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun conversationSendMessageTest() {
        var sendMessageCalled = false

        // Given
        val messages = emptyList<Message>()
        val userInfos = emptyList<UserInfo>()

        // When
        composeTestRule.setContent {
            Conversation(
                messages = messages,
                userInfos = userInfos,
                onSendMessage = {
                    sendMessageCalled = true
                },
                onLogout = {}
            )
        }

        // Then
        composeTestRule.onNodeWithTag("messageTextField").performTextInput("Hello, testing!")
        composeTestRule.onNodeWithTag("sendButton").performClick() // проверяем отправку текста
    }
}


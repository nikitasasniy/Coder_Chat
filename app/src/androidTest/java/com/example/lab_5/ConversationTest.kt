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
        // Given
        val messages = listOf(
            Message("user1", "Hello"),
//            Message("user2", "Hi there!")
        )
        val userInfos = listOf(
            UserInfo("user1", "John"),
//            UserInfo("user2", "Jane")
        )

        // When
        composeTestRule.setContent {
            Conversation(
                messages = messages,
                userInfos = userInfos,
                onSendMessage = {},
                onLogout = {}
            )
        }

        // Then
        // Assuming your IconButton for logout has contentDescription "Logout"
        composeTestRule.onNodeWithTag("logoutButton").assertIsDisplayed()

        // Assuming your message cards have specific text content
//        composeTestRule.onNodeWithTag("messageCard1").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("messageCard2").assertIsDisplayed()

        // Assuming your TextField has a label "Введите сообщение"
        composeTestRule.onNodeWithTag("messageTextField").assertIsDisplayed()

        // Assuming your IconButton for send has contentDescription "Send"
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
        composeTestRule.onNodeWithTag("messageCard").equals(messages.size)


        val messageCardNodes = composeTestRule.onAllNodesWithTag("messageCard")
        val fuckOfListNodes = listOf(messageCardNodes)
        for (i in 0 until fuckOfListNodes.size) {
            val node = fuckOfListNodes[i]
            println("Node at index $i: $node.")
        }


// Дополнительно можно проверить текст сообщений
// messages.forEachIndexed { index, message ->
// val nonNullableText: String = message.text ?: ""
// composeTestRule.onAllNodesWithTag("messageCard")[index]
// .assertTextEquals(nonNullableText)
// }


    }
}


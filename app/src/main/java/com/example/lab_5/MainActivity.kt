@file:OptIn(
    ExperimentalMaterial3Api::class
)

package com.example.lab_5

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab_5.ui.theme.Lab_5Theme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*


class MainActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("messages")
    private val referenceUser = database.getReference("users")

    private var userInfos by mutableStateOf<List<UserInfo>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        referenceUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val userInfoList = mutableListOf<UserInfo>()

                    // Проходим по всем дочерним узлам (пользователям)
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key ?: ""
                        val nickname =
                            userSnapshot.child("nickname").getValue(String::class.java) ?: ""

                        // Создаем объект UserInfo и добавляем его в список
                        val userInfo = UserInfo(userId, nickname)
                        userInfoList.add(userInfo)
                    }

                    // Присваиваем полученный список объекту userInfos
                    userInfos = userInfoList

                    // Здесь можно выполнить дополнительные действия с объектом userInfos
                    // Например, отобразить его в интерфейсе или использовать в других частях вашего приложения
                } catch (e: Exception) {
                    Log.e("Firebase", "Error fetching user data: ${e.message}", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок при запросе к базе данных
                Log.e("Firebase", "Error fetching user data: ${error.message}", error.toException())
            }
        })

        setContent {
            Lab_5Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Ваша логика чата
                    val conversationState =
                        remember { mutableStateOf(SampleData.conversationSample) }

                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messages = mutableListOf<Message>()
                            for (childSnapshot in snapshot.children) {
                                val userId =
                                    childSnapshot.child("userId").getValue(String::class.java)
                                val text =
                                    childSnapshot.child("content").getValue(String::class.java)

                                if (userId != null && text != null) {
                                    val message = Message(userId, text)
                                    messages.add(message)
                                }
                            }
                            Log.d("Firebase", "Data loaded successfully: $messages")

                            // Обновляем состояние списка сообщений
                            conversationState.value = messages
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(
                                "Firebase",
                                "Data loading cancelled: ${error.message}",
                                error.toException()
                            )
                        }
                    })

                    // Отображаем состояние списка сообщений
                    Conversation(
                        messages = conversationState.value,
                        userInfos = userInfos, // Передаем userInfos в Conversation
                        onSendMessage = { newMessage ->
                            reference.push().setValue(
                                ChatMessage(
                                    userId = getCurrentUserId(),
                                    content = newMessage

                                ),

                                )
                        },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()

                            // После разлогинивания, перенаправьте пользователя на экран входа (LoginActivity)
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)

                            // Закройте текущую активность или выполните другие необходимые действия
                            finish()
                        }

                    )
                }
            }
        }

    }
}


private fun getCurrentUserId(): String {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return currentUser?.uid ?: ""
}

@Composable
fun MessageCard(
    msg: Message,
    userInfos: List<UserInfo>,
    modifier: Modifier = Modifier,
    isExpanded: () -> Boolean // Функция, возвращающая текущее значение isExpanded
) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.clippy),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .testTag("messageCardFalse")
        )
        Spacer(modifier = Modifier.width(8.dp))

        var isExpandedLocal by remember { mutableStateOf(false) }

        val surfaceColor by animateColorAsState(
            if (isExpandedLocal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "",
        )

        // Используем userId из msg для поиска соответствующего никнейма в списке UserInfo
        val nickname = userInfos.find { it.userId == msg.userId }?.nickname ?: ""

        Column(modifier = Modifier.clickable { isExpandedLocal = !isExpandedLocal }.testTag("textAnimate")) {
            // Используем найденный никнейм вместо userId
            Text(
                text = nickname,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {

                msg.text?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(all = 4.dp).testTag("textExtensionAnimate"),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpandedLocal) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Вызываем isExpanded для передачи текущего значения
        isExpandedLocal = isExpanded()
    }
}


@ExperimentalMaterial3Api
@Composable
fun Conversation(
    messages: List<Message>,
    userInfos: List<UserInfo>,
    onSendMessage: (String) -> Unit,
    onLogout: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }

    LazyColumn {
        // Кнопка разлогинивания вверху списка
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("logoutButton")
            ) {
                IconButton(
                    onClick = {
                        onLogout()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .testTag("IconButton")
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = "Logout",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(10.dp)
                            )
                            .testTag("logoutImage")
                    )
                }
            }
        }

        items(messages) { message ->
            // Отображаем сообщения
            MessageCard(
                msg = message,
                userInfos = userInfos,
                modifier = Modifier.testTag("messageCard1"),
                isExpanded = {false}
            )
        }


        // Кнопка отправки в конце списка
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("sendButton")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Поле ввода
                    TextField(
                        value = messageText,
                        onValueChange = {
                            messageText = it
                        },
                        label = { Text("Введите сообщение") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .testTag("messageTextField")
                    )

                    // Кнопка отправки
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                onSendMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
//                            .testTag("sendButton")
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_send),
                            contentDescription = "Send",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.5.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(10.dp)
                                )
                                .testTag("sendImage")
                        )
                    }
                }

            }
        }

    }

}


//доделать юнит тесты, сделать кнопку логаут, добавить картинки, уведомления ну и прочую фигню для баллов



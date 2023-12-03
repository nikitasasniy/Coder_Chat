@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.lab_5

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
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
                        val nickname = userSnapshot.child("nickname").getValue(String::class.java) ?: ""

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
                    val conversationState = remember { mutableStateOf(SampleData.conversationSample) }

                    // Добавляем переменную для хранения состояния логина
                    var isLoggedIn by remember { mutableStateOf(true) }



                    // Отображаем состояние чата или кнопку разлогинивания
                    if (isLoggedIn) {
                        Conversation(
                            messages = conversationState.value,
                            userInfos = userInfos,
                            onSendMessage = { newMessage ->
                                reference.push().setValue(ChatMessage(userId = getCurrentUserId(), content = newMessage))
                            },
                            onLogout = {
                                // Вызываем разлогинивание
                                isLoggedIn = false


                            }
                        )
                    } else {
                        // Перенаправляем на экран входа при разлогинивании
                        LocalContext.current.startActivity(Intent(LocalContext.current, LoginActivity::class.java))
                    }
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
fun MessageCard(msg: Message, userInfos: List<UserInfo>) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.clippy),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Используем userId из msg для поиска соответствующего никнейма в списке UserInfo
        val nickname = userInfos.find { it.userId == msg.userId }?.nickname ?: ""

        Column {
            // Используем найденный никнейм вместо userId
            Text(
                text = nickname,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 1.dp) {
                msg.text?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(all = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
//    Lab_5Theme {
//        Surface {
//            MessageCard(
//                msg = Message("Lexi", "Take a look at Jetpack Compose, it's great!")
//            )
//        }
//    }
}

@Composable
fun Conversation(
    messages: List<Message>,
    userInfos: List<UserInfo>,
    onSendMessage: (String) -> Unit,
    onLogout: () -> Unit // Добавляем функцию для разлогинивания
) {
    var messageText by remember { mutableStateOf("") }

    LazyColumn {
        // Добавляем кнопку разлогинивания в начало списка
        item {
            Button(
                onClick = {
                    // Вызываем функцию разлогинивания при нажатии кнопки
                    onLogout()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Выйти")
            }
        }

        // Далее отображаем сообщения
        items(messages) { message ->
            MessageCard(message, userInfos)
        }

        // Добавляем TextField и кнопку отправки в конец списка
        item {
            TextField(
                value = messageText,
                onValueChange = {
                    messageText = it
                },
                label = { Text("Введите сообщение") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Отправить")
            }
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
//    Lab_5Theme {
//        Conversation(SampleData.conversationSample){ newMessage ->
//            // Обработайте новое сообщение
//            // Например, вы можете добавить его в список сообщений или отправить на сервер
//            // Здесь вы можете добавить код для обработки нового сообщения
//        }
//    }
}


//доделать юнит тесты, сделать кнопку логаут, добавить картинки, уведомления ну и прочую фигню для баллов
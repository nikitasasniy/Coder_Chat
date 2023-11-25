@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.lab_5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.lab_5.ui.theme.Lab_5Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_5Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Log.d("LoginActivity", "Entering onCreate")

                    LoginScreen(
                        onLoginClick = { email, password ->
                            signInWithEmailAndPassword(
                                email,
                                password
                            )
                        },
                        onRegisterClick = { email, password, nickname ->
                            registerWithEmailAndPassword(email, password, nickname)
                        }
                    )
                }
            }
        }
    }
    private fun registerWithEmailAndPassword(email: String, password: String, nickname: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Регистрация успешна, сохраняем никнейм в базу данных
                    saveNicknameToDatabase(nickname)
                    // Переходим на MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    // Не вызываем finish() здесь
                } else {
                    // Обработка ошибок при регистрации
                    Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveNicknameToDatabase(nickname: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userId = it.uid
            val userReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userReference.child("nickname").setValue(nickname)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                runOnUiThread {
                    if (task.isSuccessful) {
                        // Пользователь успешно авторизован, переход на MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                        // Завершаем LoginActivity
                        finish()
                    } else {
                        // Обработка ошибок при аутентификации
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: (email: String, password: String, nickname: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле ввода электронной почты
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Поле ввода пароля
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Если режим регистрации, добавляем поле для ввода никнейма
        if (isRegistering) {
            TextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }

        // Кнопки входа и регистрации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (isRegistering) {
                        onRegisterClick(email, password, nickname)
                    } else {
                        onLoginClick(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(if (isRegistering) "Register" else "Log In")
            }

            // Кнопка для переключения между режимами входа и регистрации
            Button(
                onClick = { isRegistering = !isRegistering },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(if (isRegistering) "Switch to Log In" else "Switch to Register")
            }
        }
    }
}
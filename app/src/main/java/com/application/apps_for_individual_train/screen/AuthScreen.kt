package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.application.apps_for_individual_train.data.UserData
import com.google.firebase.auth.FirebaseUser

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    errorMessage: String?,
    onAuthSuccess: () -> Unit, // Новый параметр
    user: FirebaseUser? // Новый параметр
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        if (user != null) onAuthSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Войти", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onLoginClick(email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Войти")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Нет аккаунта? Зарегистрируйтесь",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { onNavigateToRegister() }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    errorMessage: String?,
    onAuthSuccess: () -> Unit, // Новый параметр
    user: FirebaseUser? // Новый параметр
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        if (user != null) onAuthSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Зарегистрироваться", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onRegisterClick(email, password, name) }, modifier = Modifier.fillMaxWidth()) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Уже есть аккаунт? Войти",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { onNavigateToLogin() }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

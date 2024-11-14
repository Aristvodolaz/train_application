package com.application.apps_for_individual_train.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    // Проверка статуса авторизации
    LaunchedEffect(Unit) {
        delay(2000) // Задержка для показа Splash Screen (2 секунды)
        if (FirebaseAuth.getInstance().currentUser != null) {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        }
    }

    // Отображение контента Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to Individual Training App",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
    }
}

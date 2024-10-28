package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(
    navController: NavController
) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello, ${user?.displayName ?: "User"}",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Список категорий тренировок
        Button(
            onClick = {
                // Переход на экран выбора тренировок
                navController.navigate(Screen.WorkoutSelectionScreen.route)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Cardio Workouts")
        }

        Button(
            onClick = {
                // Переход на экран выбора тренировок
                navController.navigate(Screen.WorkoutSelectionScreen.route)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Strength Workouts")
        }

        Button(
            onClick = {
                // Переход на экран выбора тренировок
                navController.navigate(Screen.WorkoutSelectionScreen.route)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Yoga Workouts")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Переход на экран профиля
        ClickableText(
            text = androidx.compose.ui.text.AnnotatedString("Go to Profile"),
            onClick = {
                navController.navigate(Screen.ProfileScreen.route)
            }
        )
    }
}

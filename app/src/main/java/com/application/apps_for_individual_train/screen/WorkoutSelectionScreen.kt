package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.apps_for_individual_train.data.WorkoutData
import com.application.apps_for_individual_train.viewModel.WorkoutSelectionViewModel

@Composable
fun WorkoutSelectionScreen(
    navController: NavController,
    category: String, // Категория тренировок передается при переходе
    viewModel: WorkoutSelectionViewModel = hiltViewModel()
) {
    // Запуск загрузки данных при входе на экран
    LaunchedEffect(category) {
        viewModel.loadWorkoutsByCategory(category)
    }

    // Получаем текущее состояние данных
    val workouts by viewModel.workouts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Workouts in $category category",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            // Отображение индикатора загрузки
            CircularProgressIndicator()
        } else if (workouts.isEmpty()) {
            // Отображение сообщения, если список тренировок пуст
            Text(
                text = "No workouts available in this category.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            // Отображение списка тренировок
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(workouts) { workout ->
                    WorkoutItem(workout = workout) {
                        // Переход к экрану тренировки
                        navController.navigate("${Screen.WorkoutScreen.route}/${workout.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: WorkoutData, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = workout.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Duration: ${workout.duration} mins",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Start Workout")
        }
    }
}

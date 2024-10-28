package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.*
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
import com.application.apps_for_individual_train.viewModel.WorkoutViewModel

@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutId: String,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    // Загрузка тренировки по ID
    LaunchedEffect(workoutId) {
        viewModel.loadWorkoutById(workoutId)
    }

    // Получаем текущую тренировку и статус загрузки
    val workout by viewModel.workout.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Внутреннее состояние выполнения тренировки
    var isWorkoutRunning by remember { mutableStateOf(false) }
    var workoutTimeLeft by remember { mutableStateOf(workout?.duration ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (workout != null) {
            WorkoutContent(
                workout = workout!!,
                isWorkoutRunning = isWorkoutRunning,
                workoutTimeLeft = workoutTimeLeft,
                onStart = { isWorkoutRunning = true },
                onPause = { isWorkoutRunning = false },
                onEnd = {
                    isWorkoutRunning = false
                    workoutTimeLeft = workout!!.duration
                }
            )
        } else {
            Text("Workout not found.", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun WorkoutContent(
    workout: WorkoutData,
    isWorkoutRunning: Boolean,
    workoutTimeLeft: Int,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit
) {
    Text(
        text = workout.name,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Description: ${workout.description}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = "Time Left: ${workoutTimeLeft} mins",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Кнопки управления
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isWorkoutRunning) {
            Button(onClick = onStart, modifier = Modifier.weight(1f)) {
                Text("Start")
            }
        } else {
            Button(onClick = onPause, modifier = Modifier.weight(1f)) {
                Text("Pause")
            }
        }
        Button(onClick = onEnd, modifier = Modifier.weight(1f)) {
            Text("End Workout")
        }
    }
}

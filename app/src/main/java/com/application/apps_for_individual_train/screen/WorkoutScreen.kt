package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    LaunchedEffect(workoutId) { viewModel.loadWorkoutById(workoutId) }
    val workout by viewModel.workout.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isWorkoutRunning by remember { mutableStateOf(false) }
    var workoutTimeLeft by remember { mutableStateOf(workout?.duration ?: 0) }

    // Создаем заглушку для тренировки
    val placeholderWorkout = WorkoutData(
        id = "placeholder",
        name = "Sample Workout",
        description = "This is a sample workout for display purposes.",
        duration = 30,
        difficulty = "Intermediate"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            WorkoutContent(
                workout = workout ?: placeholderWorkout,  // Используем заглушку, если данные еще не загружены
                isWorkoutRunning = isWorkoutRunning,
                workoutTimeLeft = workoutTimeLeft,
                onStart = { isWorkoutRunning = true },
                onPause = { isWorkoutRunning = false },
                onEnd = {
                    isWorkoutRunning = false
                    workoutTimeLeft = workout?.duration ?: placeholderWorkout.duration
                }
            )
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
    // Заголовок тренировки
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

    // Прогресс тренировки
    Text(
        text = "Time Left: ${workoutTimeLeft} mins",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LinearProgressIndicator(
        progress = workoutTimeLeft / workout.duration.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Кнопки управления тренировкой
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

    // Добавление прогресса, если тренировка активна
    if (isWorkoutRunning) {
        WorkoutProgress(
            timeLeft = workoutTimeLeft,
            totalDuration = workout.duration
        )
    }
}

@Composable
fun WorkoutProgress(timeLeft: Int, totalDuration: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val progress = 1f - (timeLeft.toFloat() / totalDuration.toFloat())

        Text(
            text = "Workout Progress",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(progress * 100).toInt()}% completed",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

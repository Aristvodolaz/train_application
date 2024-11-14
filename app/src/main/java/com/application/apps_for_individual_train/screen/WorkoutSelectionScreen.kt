package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.apps_for_individual_train.data.WorkoutData
import com.application.apps_for_individual_train.viewModel.WorkoutSelectionViewModel

@Composable
fun WorkoutSelectionScreen(
    navController: NavController,
    category: String,
    viewModel: WorkoutSelectionViewModel = hiltViewModel()
) {
    // Запускает загрузку данных, когда экран открыт
    LaunchedEffect(category) {
        viewModel.loadWorkoutsByCategory(category)
    }

    // Наблюдение за состоянием тренировок и статусом загрузки
    val workouts by viewModel.workouts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Заглушки для тренировок, если список пуст
    val placeholderWorkouts = getPlaceholderWorkouts(category)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Workouts in $category",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (workouts.isEmpty()) {
            // Используем заглушки, если нет данных
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(placeholderWorkouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        onClick = {
                            navController.navigate("${Screen.WorkoutScreen.route}/${workout.id}")
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workouts) { workout ->
                    WorkoutItem(
                        workout = workout,
                        onClick = {
                            navController.navigate("${Screen.WorkoutScreen.route}/${workout.id}")
                        }
                    )
                }
            }
        }
    }
}

// Функция генерации заглушек для тренировок
fun getPlaceholderWorkouts(category: String): List<WorkoutData> {
    val durations = listOf(10, 20, 30, 40, 50, 60)
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    return durations.flatMap { duration ->
        difficulties.map { difficulty ->
            WorkoutData(
                id = "$category-$duration-$difficulty",
                name = "$difficulty $category Workout",
                description = "A $difficulty level workout for $category, lasting $duration minutes.",
                duration = duration,
                difficulty = difficulty
            )
        }
    }
}

@Composable
fun WorkoutItem(workout: WorkoutData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${workout.duration} mins",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Difficulty: ${workout.difficulty}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Start Workout",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

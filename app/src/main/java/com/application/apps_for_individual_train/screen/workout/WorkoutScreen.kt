package com.application.apps_for_individual_train.screen.workout


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.application.apps_for_individual_train.data.WorkoutData
import com.application.apps_for_individual_train.viewModel.WorkoutViewModel
import com.google.firebase.storage.FirebaseStorage

import androidx.compose.foundation.layout.*


@Composable
fun WorkoutScreen(
    workoutId: String,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    LaunchedEffect(workoutId) { viewModel.loadWorkoutById(workoutId) }
    val workout by viewModel.workout.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isWorkoutRunning by remember { mutableStateOf(false) }
    var workoutProgress by remember { mutableStateOf(0f) }

    val placeholderWorkout = WorkoutData(
        id = "placeholder",
        name = "Sample Workout",
        description = "This is a sample workout for display purposes.",
        duration = 30,
        difficulty = "Intermediate"
    )
    val currentWorkout = workout ?: placeholderWorkout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            WorkoutHeader(currentWorkout)
            WorkoutDescription(currentWorkout)
            WorkoutVideoPlayer(
                videoUrl = currentWorkout.videoUrl,
                isWorkoutRunning = isWorkoutRunning,
                onProgressUpdate = { progress ->
                    workoutProgress = progress
                },
                onStop = {
                    viewModel.saveWorkoutProgress(
                        workoutId,
                        (workoutProgress * currentWorkout.duration).toInt()
                    )
                }
            )
            WorkoutProgress(progress = workoutProgress)
            WorkoutControls(
                isWorkoutRunning = isWorkoutRunning,
                onStart = { isWorkoutRunning = true },
                onPause = { isWorkoutRunning = false },
                onReset = {
                    isWorkoutRunning = false
                    workoutProgress = 0f
                    viewModel.resetWorkoutProgress(workoutId)
                }
            )
        }
    }
}

@Composable
fun WorkoutProgress(progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Workout Progress",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(100.dp),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WorkoutVideoPlayer(
    videoUrl: String,
    isWorkoutRunning: Boolean,
    onProgressUpdate: (Float) -> Unit,
    onStop: () -> Unit
) {
    val context = LocalContext.current
    var videoUrlState by remember { mutableStateOf<String?>(null) }
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(videoUrl) {
        fetchVideoUrl(videoUrl) { url ->
            videoUrlState = url
            if (url != null) {
                exoPlayer.setMediaItem(MediaItem.fromUri(url))
                exoPlayer.prepare()
            }
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            val currentPosition = exoPlayer.currentPosition
            val duration = exoPlayer.duration.takeIf { it > 0 } ?: 1
            val progress = currentPosition.toFloat() / duration


            Log.d("WorkoutVideoPlayer", "Current Position: $currentPosition ms, Duration: $duration ms, Progress: $progress")

            onProgressUpdate(progress)

            kotlinx.coroutines.delay(500L) // Обновляем каждые 500 мс
        }
    }

    LaunchedEffect(isWorkoutRunning) {
        exoPlayer.playWhenReady = isWorkoutRunning
        if (isWorkoutRunning) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
            onStop()
        }
    }


    Box(modifier = Modifier.fillMaxWidth()) {
        if (videoUrlState != null) {
            AndroidView(
                factory = { PlayerView(context).apply { player = exoPlayer } },
                modifier = Modifier.height(200.dp)
            )
        } else {
            Text(
                text = "Video not available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}




fun fetchVideoUrl(storagePath: String, onResult: (String?) -> Unit) {
    if (storagePath.startsWith("gs://")) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storagePath)
        storageReference.downloadUrl
            .addOnSuccessListener { uri -> onResult(uri.toString()) }
            .addOnFailureListener {
                onResult(null)
            }
    } else {
        onResult(storagePath)
    }
}

@Composable
fun WorkoutHeader(currentWorkout: WorkoutData) {
    Text(
        text = currentWorkout.name,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    AssistChip(
        onClick = {},
        label = { Text("Difficulty: ${currentWorkout.difficulty}") },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun WorkoutDescription(currentWorkout: WorkoutData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = currentWorkout.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WorkoutControls(
    isWorkoutRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onStart,
            modifier = Modifier.weight(1f),
            enabled = !isWorkoutRunning
        ) {
            Text("Resume")
        }
        Button(
            onClick = onPause,
            modifier = Modifier.weight(1f),
            enabled = isWorkoutRunning
        ) {
            Text("Pause")
        }
        Button(
            onClick = onReset,
            modifier = Modifier.weight(1f)
        ) {
            Text("Restart")
        }
    }
}

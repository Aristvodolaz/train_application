package com.application.apps_for_individual_train.screen.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.graphics.SolidColor

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
        name = "Пример тренировки",
        description = "Это пример тренировки для отображения.",
        duration = 30,
        difficulty = "Средний"
    )
    val currentWorkout = workout ?: placeholderWorkout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
            }
        } else {
            WorkoutHeader(currentWorkout)
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            WorkoutProgress(progress = workoutProgress)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            WorkoutDescription(currentWorkout)
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Прогресс тренировки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
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

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            if (videoUrlState != null) {
                AndroidView(
                    factory = { 
                        PlayerView(context).apply { 
                            player = exoPlayer 
                            useController = true
                            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                        } 
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Видео недоступно",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentWorkout.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            SuggestionChip(
                onClick = {},
                label = { 
                    Text(
                        text = "Сложность: ${currentWorkout.difficulty}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                border = null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            SuggestionChip(
                onClick = {},
                label = { 
                    Text(
                        text = "Длительность: ${currentWorkout.duration} мин",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                border = null
            )
        }
    }
}

@Composable
fun WorkoutDescription(currentWorkout: WorkoutData) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Описание",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = currentWorkout.description ?: "Нет описания",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
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
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedButton(
            onClick = onStart,
            modifier = Modifier.weight(1f),
            enabled = !isWorkoutRunning,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Продолжить",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        ElevatedButton(
            onClick = onPause,
            modifier = Modifier.weight(1f),
            enabled = isWorkoutRunning,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Пауза",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    OutlinedButton(
        onClick = onReset,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(MaterialTheme.colorScheme.error)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Начать заново",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

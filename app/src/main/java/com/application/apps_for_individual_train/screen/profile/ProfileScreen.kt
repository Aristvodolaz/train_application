package com.application.apps_for_individual_train.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.apps_for_individual_train.ui.theme.ThemeSwitcher
import com.application.apps_for_individual_train.viewModel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: return

    var userProgress by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val isDarkTheme = themeViewModel.isDarkTheme.collectAsState(initial = false)

    LaunchedEffect(userId) {
        val progress = fetchUserProgressWithNames(userId)
        userProgress = progress
    }

    // Функция для обработки нажатия на элемент тренировки
    fun onWorkoutClick(workoutName: String) {
        // Здесь можно добавить навигацию к деталям тренировки
        // Например: navController.navigate("workout_details/$workoutName")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // Profile picture
        item { ProfilePicture(icon = Icons.Filled.Person) }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // User information card
        item {
            UserInfoCard(
                displayName = user?.displayName ?: "User Name",
                email = user?.email ?: "user@example.com"
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Theme switcher
        item {
            ThemeSwitcher(
                darkTheme = isDarkTheme.value,
                onThemeChanged = { themeViewModel.setDarkTheme(it) }
            )
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Workout progress header
        item {
            Text(
                text = "Прогресс тренировок",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Workout progress items
        if (userProgress.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Нет данных о тренировках",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Начните тренировки, чтобы увидеть свой прогресс",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(userProgress.toList()) { (workoutName, progress) ->
                WorkoutProgressItem(
                    workoutName = workoutName,
                    progress = progress,
                    onClick = { onWorkoutClick(workoutName) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            ProfileButton(
                text = "Редактировать профиль",
                icon = Icons.Default.Edit,
                color = MaterialTheme.colorScheme.primary
            ) {
                navController.navigate("editProfile")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ProfileButton(
                text = "Изменить пароль",
                icon = Icons.Default.Lock,
                color = MaterialTheme.colorScheme.secondary
            ) {
                navController.navigate("changePassword")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ProfileButton(
                text = "Выйти",
                icon = Icons.Default.ExitToApp,
                color = MaterialTheme.colorScheme.error
            ) {
                FirebaseAuth.getInstance().signOut()
            }
        }
    }
}

suspend fun fetchUserProgressWithNames(userId: String): Map<String, Int> {
    return try {
        val userProgressSnapshot = FirebaseDatabase.getInstance()
            .getReference("user_progress")
            .child(userId)
            .get()
            .await()

        val workoutProgressMap = mutableMapOf<String, Int>()

        for (child in userProgressSnapshot.children) {
            val workoutId = child.key ?: continue
            val progress = child.getValue(Int::class.java) ?: 0

            // Fetch workout name by workoutId
            val workoutSnapshot = FirebaseDatabase.getInstance()
                .getReference("workouts")
                .child(workoutId)
                .get()
                .await()

            val workoutName = workoutSnapshot.child("name").getValue(String::class.java) ?: "Unknown Workout"
            workoutProgressMap[workoutName] = progress
        }

        workoutProgressMap
    } catch (e: Exception) {
        e.printStackTrace()
        emptyMap() // Return an empty map if an error occurs
    }
}

@Composable
fun UserInfoCard(displayName: String, email: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = email,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WorkoutProgressItem(workoutName: String, progress: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = workoutName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "$progress%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ProfilePicture(icon: ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Profile Picture",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun ProfileButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}






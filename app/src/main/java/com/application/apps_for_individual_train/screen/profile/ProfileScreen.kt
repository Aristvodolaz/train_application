package com.application.apps_for_individual_train.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: return

    var userProgress by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(userId) {
        val progress = fetchUserProgressWithNames(userId)
        userProgress = progress
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Profile picture
        ProfilePicture(icon = Icons.Filled.Person)

        Spacer(modifier = Modifier.height(16.dp))

        // User information card
        UserInfoCard(
            displayName = user.displayName ?: "User Name",
            email = user.email ?: "user@example.com"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Workout progress list with navigation
        WorkoutProgressList(
            progressMap = userProgress,
            onWorkoutClick = { workoutId ->
                navController.navigate("workoutDetail/$workoutId")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileButton(
            text = "Edit Profile",
            icon = Icons.Default.Edit,
            color = MaterialTheme.colorScheme.primary
        ) {
            navController.navigate("editProfile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileButton(
            text = "Change Password",
            icon = Icons.Default.Lock,
            color = MaterialTheme.colorScheme.secondary
        ) {
            navController.navigate("changePassword")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileButton(
            text = "Log Out",
            icon = Icons.Default.ExitToApp,
            color = MaterialTheme.colorScheme.error
        ) {
            FirebaseAuth.getInstance().signOut()
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
        elevation = CardDefaults.cardElevation(6.dp)
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
fun WorkoutProgressList(progressMap: Map<String, Int>, onWorkoutClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Workout Progress",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        progressMap.forEach { (workoutName, progress) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onWorkoutClick(workoutName) } // Handle click
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = workoutName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Progress: $progress%",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CircularProgressIndicator(
                        progress = progress / 100f,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 4.dp
                    )
                }
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
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Profile Picture",
            tint = Color.White,
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun ProfileButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}






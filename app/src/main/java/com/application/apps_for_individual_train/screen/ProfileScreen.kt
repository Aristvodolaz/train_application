package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        // Enhanced user information card
        UserInfoCard(
            displayName = user?.displayName ?: "User Name",
            email = user?.email ?: "user@example.com"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons with icons
        ProfileButton(
            text = "Edit Profile",
            icon = Icons.Default.Edit,
            color = MaterialTheme.colorScheme.primary,
            onClick = { /* Navigate to edit profile */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileButton(
            text = "Change Password",
            icon = Icons.Default.Lock,
            color = MaterialTheme.colorScheme.secondary,
            onClick = { /* Navigate to change password */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileButton(
            text = "Log Out",
            icon = Icons.Default.ExitToApp,
            color = MaterialTheme.colorScheme.error,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                // Redirect to login screen
            }
        )
    }
}

@Composable
fun UserInfoCard(displayName: String, email: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
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

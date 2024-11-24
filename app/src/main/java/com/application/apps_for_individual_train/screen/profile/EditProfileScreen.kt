package com.application.apps_for_individual_train.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun EditProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = remember { mutableStateOf(user?.displayName ?: "") }
    val email = remember { mutableStateOf(user?.email ?: "") }
    val isLoading = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = displayName.value,
            onValueChange = { displayName.value = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (message.value != null) {
            Text(
                text = message.value!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading.value = true
                    user?.updateEmail(email.value)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName.value)
                                .build()

                            user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                                isLoading.value = false
                                if (profileTask.isSuccessful) {
                                    message.value = "Profile updated successfully!"
                                    navController.popBackStack() // Navigate back
                                } else {
                                    message.value = "Failed to update profile: ${profileTask.exception?.message}"
                                }
                            }
                        } else {
                            isLoading.value = false
                            message.value = "Failed to update email: ${task.exception?.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val isLoading = remember { mutableStateOf(false) }
    val message = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Change Password",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "We will send a password reset link to your email: $email",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (message.value != null) {
            Text(
                text = message.value!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading.value = true
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        isLoading.value = false
                        if (task.isSuccessful) {
                            message.value = "Password reset email sent successfully!"
                        } else {
                            message.value = "Failed to send password reset email."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reset Link")
            }
        }
    }
}

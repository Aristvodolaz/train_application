package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.background
import com.application.apps_for_individual_train.viewModel.AuthViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Workout Categories",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        WorkoutCategories(navController)
    }
}


@Composable
fun WorkoutCategories(navController: NavController) {
    val categories = listOf("Cardio", "Strength", "Yoga", "Pilates", "HIIT", "Crossfit")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = {
                    navController.navigate(Screen.WorkoutSelectionScreen.createRoute(category))
                }
            )
        }
    }
}


@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val categoryIcon = when (category) {
        "Cardio" -> Icons.Default.FitnessCenter
        "Yoga" -> Icons.Default.SelfImprovement
        // Add more icons for other categories if available
        else -> Icons.Default.FitnessCenter
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp)), // Added shadow for elevation
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onClick
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon with padding
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = "$category icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp) // Added right padding here
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Texts
                Column {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explore $category workouts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.MainScreen.route,
        Screen.WorkoutSelectionScreen.route,
        Screen.ProfileScreen.route
        // Add other main screen routes here as needed
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Authentication Screens
            composable(Screen.LoginScreen.route) {
                LoginScreen(
                    onLoginClick = { login, password ->
                        authViewModel.login(login, password)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.RegisterScreen.route)
                    },
                    errorMessage = authViewModel.errorMessage.observeAsState().value,
                    onAuthSuccess = {
                        navController.navigate(Screen.MainScreen.route) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    },
                    user = authViewModel.user.observeAsState().value
                )
            }

            composable(Screen.RegisterScreen.route) {
                RegisterScreen(
                    onRegisterClick = { email, password, name ->
                        authViewModel.register(email, password, name)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    },
                    errorMessage = authViewModel.errorMessage.observeAsState().value,
                    onAuthSuccess = {
                        navController.navigate(Screen.MainScreen.route) {
                            popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                        }
                    },
                    user = authViewModel.user.observeAsState().value
                )
            }

            // Main Application Screens with Bottom Navigation
            composable(Screen.MainScreen.route) { MainScreen(navController) }
            composable(
                route = Screen.WorkoutSelectionScreen.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Unknown"
                WorkoutSelectionScreen(navController = navController, category = category)
            }

            composable(
                route = "workout_screen/{workoutId}",  // Define this route properly
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: "1"
                WorkoutScreen(navController = navController, workoutId = workoutId)
            }

            composable(Screen.ProfileScreen.route) { ProfileScreen(navController) }
            // Add other main screens here as needed
        }
    }
}



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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.application.apps_for_individual_train.screen.profile.ChangePasswordScreen
import com.application.apps_for_individual_train.screen.profile.EditProfileScreen
import com.application.apps_for_individual_train.screen.profile.LoginScreen
import com.application.apps_for_individual_train.screen.profile.ProfileScreen
import com.application.apps_for_individual_train.screen.profile.RegisterScreen
import com.application.apps_for_individual_train.screen.workout.WorkoutScreen
import com.application.apps_for_individual_train.screen.workout.WorkoutSelectionScreen


@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background( Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )
            )),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Заголовок экрана
        Text(
            text = "Workout Categories",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        WorkoutCategories(navController)
    }
}

@Composable
fun WorkoutCategories(navController: NavController) {
    val categories = listOf(
        "Cardio" to Icons.Default.FitnessCenter,
        "Strength" to Icons.Default.FitnessCenter,
        "Yoga" to Icons.Default.SelfImprovement,
        "Pilates" to Icons.Default.SportsGymnastics,
        "HIIT" to Icons.Default.FitnessCenter,
        "Crossfit" to Icons.Default.FitnessCenter
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { (category, icon) ->
            CategoryCard(
                category = category,
                icon = icon,
                onClick = {
                    navController.navigate(Screen.WorkoutSelectionScreen.createRoute(category))
                }
            )
        }
    }
}

@Composable
fun CategoryCard(category: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(16.dp),
            onClick = onClick,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(10.dp) // Высота тени
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$category icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .padding(10.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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

            composable(Screen.MainScreen.route) { MainScreen(navController) }
            composable(
                route = Screen.WorkoutSelectionScreen.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Unknown"
                WorkoutSelectionScreen(navController = navController, category = category)
            }

            composable("editProfile") { EditProfileScreen(navController) }
            composable("changePassword") { ChangePasswordScreen(navController) }
            composable("workoutDetail/{workoutId}") { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId")
                workoutId?.let {
                    WorkoutScreen(workoutId = it)
                }
            }

            composable(
                route = "workout_screen/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: "1"
                WorkoutScreen( workoutId = workoutId)
            }

            composable(Screen.ProfileScreen.route) { ProfileScreen(navController) }
        }
    }
}



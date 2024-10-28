package com.application.apps_for_individual_train.screen

sealed class Screen(val route: String) {
    object AuthScreen: Screen("auth")
    object MainScreen: Screen("main")
    object SplashScreen: Screen("splash")
    object WorkoutSelectionScreen: Screen("workout")
    object ProfileScreen: Screen("profile")
    object WorkoutScreen : Screen("workout_screen")
}
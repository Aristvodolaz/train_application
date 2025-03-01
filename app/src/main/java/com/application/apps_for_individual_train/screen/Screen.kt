package com.application.apps_for_individual_train.screen

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object RegisterScreen : Screen("register_screen")
    object MainScreen : Screen("main_screen")
    object ProfileScreen : Screen("profile")
    object WorkoutScreen : Screen("workout_screen")
    object StatisticsScreen : Screen("statistics_screen")
    object NutritionScreen : Screen("nutrition_screen")

    object WorkoutSelectionScreen : Screen("workout_selection_screen/{category}") {
        fun createRoute(category: String) = "workout_selection_screen/$category"
    }
    object SplashScreen: Screen("splash")

}
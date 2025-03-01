package com.application.apps_for_individual_train.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.rememberNavController
import com.application.apps_for_individual_train.data.ThemePreferences
import com.application.apps_for_individual_train.screen.AppNavHost
import com.application.apps_for_individual_train.screen.Screen
import com.application.apps_for_individual_train.viewModel.AuthViewModel
import com.application.apps_for_individual_train.viewModel.ThemeViewModel
import com.application.apps_for_individual_train.viewModel.ThemeViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

@AndroidEntryPoint
class StartActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val themePreferences = ThemePreferences(this)
        
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(themePreferences)
            )
            
            val navController = rememberNavController()
            val user by authViewModel.user.observeAsState()
            val errorMessage by authViewModel.errorMessage.observeAsState()

            // Observe authentication state and navigate accordingly
            LaunchedEffect(user) {
                if (user != null) {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.MainScreen.route) { inclusive = true }
                    }
                }
            }

            // Root navigation setup
            AppNavHost(
                navController = navController, 
                authViewModel = authViewModel,
                themeViewModel = themeViewModel
            )
        }
    }
}

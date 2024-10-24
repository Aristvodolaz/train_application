package com.application.apps_for_individual_train.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.application.apps_for_individual_train.screen.AuthScreen
import com.application.apps_for_individual_train.screen.Screen
import com.application.apps_for_individual_train.viewModel.AuthViewModel

@AndroidEntryPoint
class StartActivity: ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.AuthScreen.route
            ){
                composable(Screen.AuthScreen.route){
                    AuthScreen(
                        onLoginClick = {login, pass ->
                            authViewModel.login(login, pass)
                        },
                        onRegisterClick = {login, pass, name ->
                            authViewModel.register(login, pass, name)
                        },
                        user = authViewModel.user.observeAsState().value,
                        userData = authViewModel.userData.observeAsState().value,
                        errorMessage = authViewModel.errorMessage.observeAsState().value,
                        onAuthSuccess = {
                            navController.navigate(Screen.MainScreen.route)
                        }
                    )
                }
            }
        }
    }
}
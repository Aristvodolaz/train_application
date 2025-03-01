package com.application.apps_for_individual_train

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.application.apps_for_individual_train.data.ThemePreferences
import com.application.apps_for_individual_train.screen.AppNavHost
import com.application.apps_for_individual_train.ui.theme.Apps_for_individual_trainTheme
import com.application.apps_for_individual_train.viewModel.AuthViewModel
import com.application.apps_for_individual_train.viewModel.ThemeViewModel
import com.application.apps_for_individual_train.viewModel.ThemeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val themePreferences = ThemePreferences(this)
        val firebaseAuth = FirebaseAuth.getInstance()
        val realtimeDatabase = FirebaseDatabase.getInstance()
        
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(themePreferences)
            )
            val darkTheme = themeViewModel.isDarkTheme.collectAsState(initial = false)
            
            Apps_for_individual_trainTheme(darkTheme = darkTheme.value) {
                val navController = rememberNavController()
                val authViewModel = AuthViewModel(firebaseAuth, realtimeDatabase)
                
                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Apps_for_individual_trainTheme {
        Greeting("Android")
    }
}
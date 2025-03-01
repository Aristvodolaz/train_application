package com.application.apps_for_individual_train.screen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val route: String, 
    val selectedIcon: ImageVector, 
    val unselectedIcon: ImageVector, 
    val title: String
) {
    object Home : BottomNavItem(
        Screen.MainScreen.route, 
        Icons.Filled.FitnessCenter, 
        Icons.Outlined.FitnessCenter, 
        "Тренировки"
    )
    object Statistics : BottomNavItem(
        Screen.StatisticsScreen.route,
        Icons.Filled.BarChart,
        Icons.Outlined.BarChart,
        "Статистика"
    )
    object Nutrition : BottomNavItem(
        Screen.NutritionScreen.route,
        Icons.Filled.Restaurant,
        Icons.Outlined.Restaurant,
        "Питание"
    )
    object Profile : BottomNavItem(
        Screen.ProfileScreen.route, 
        Icons.Filled.Person, 
        Icons.Outlined.Person, 
        "Профиль"
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Statistics,
        BottomNavItem.Nutrition,
        BottomNavItem.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            AddItem(
                bottomNavItem = item,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    bottomNavItem: BottomNavItem,
    currentDestination: NavDestination?,
    navController: NavController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == bottomNavItem.route } == true

    NavigationBarItem(
        selected = selected,
        onClick = {
            if (currentDestination?.route != bottomNavItem.route) {
                navController.navigate(bottomNavItem.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        icon = {
            Icon(
                imageVector = if (selected) bottomNavItem.selectedIcon else bottomNavItem.unselectedIcon,
                contentDescription = bottomNavItem.title,
                modifier = Modifier.size(26.dp)
            )
        },
        label = { 
            Text(
                text = bottomNavItem.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ) 
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    )
}

package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.application.apps_for_individual_train.data.ThemePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ThemeViewModel(private val themePreferences: ThemePreferences) : ViewModel() {
    
    // Получение текущей темы
    val isDarkTheme: Flow<Boolean> = themePreferences.isDarkTheme
    
    // Изменение темы
    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkTheme(isDarkTheme)
        }
    }
}

class ThemeViewModelFactory(private val themePreferences: ThemePreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(themePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
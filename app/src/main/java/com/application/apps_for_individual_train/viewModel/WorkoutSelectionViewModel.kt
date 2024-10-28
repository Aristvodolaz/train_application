package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.apps_for_individual_train.data.WorkoutData
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutSelectionViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<WorkoutData>>(emptyList())
    val workouts: StateFlow<List<WorkoutData>> = _workouts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Загрузка тренировок по категории
    fun loadWorkoutsByCategory(category: String) {
        _isLoading.value = true
        firestore.collection("workouts")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                val workoutList = result.map { document ->
                    document.toObject(WorkoutData::class.java).copy(id = document.id)
                }
                _workouts.value = workoutList
                _isLoading.value = false
            }
            .addOnFailureListener {
                _workouts.value = emptyList() // Очистка списка в случае ошибки
                _isLoading.value = false
            }
    }
}

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
class WorkoutViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _workout = MutableStateFlow<WorkoutData?>(null)
    val workout: StateFlow<WorkoutData?> = _workout

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Загрузка тренировки по id
    fun loadWorkoutById(workoutId: String) {
        _isLoading.value = true
        firestore.collection("workouts")
            .document(workoutId)
            .get()
            .addOnSuccessListener { document ->
                _workout.value = document.toObject(WorkoutData::class.java)
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }
}

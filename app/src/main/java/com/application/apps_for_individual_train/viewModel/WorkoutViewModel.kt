package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.ViewModel
import com.application.apps_for_individual_train.data.WorkoutData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val realtimeDatabase: FirebaseDatabase
) : ViewModel() {

    private val _workout = MutableStateFlow<WorkoutData?>(null)
    val workout: StateFlow<WorkoutData?> = _workout

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    // Загрузка тренировки по ID из Realtime Database
    fun loadWorkoutById(workoutId: String) {
        println("Loading workout with ID: $workoutId")
        _isLoading.value = true

        realtimeDatabase.getReference("workouts").child(workoutId).get()
            .addOnSuccessListener { snapshot ->
                val workout = snapshot.getValue(WorkoutData::class.java)
                if (workout != null) {
                    _workout.value = workout.copy(id = workoutId) // Добавляем ID
                    println("Loaded workout: ${_workout.value}")
                } else {
                    println("Workout not found for ID: $workoutId")
                    _workout.value = null
                }
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                println("Failed to load workout: ${exception.message}")
                _workout.value = null
                _isLoading.value = false
            }
    }

    // Сохранение прогресса тренировки для конкретного пользователя
    fun saveWorkoutProgress(workoutId: String, progress: Int) {
        if (userId == "unknown_user") {
            println("User not logged in. Cannot save progress.")
            return
        }

        realtimeDatabase.getReference("user_progress").child(userId).child(workoutId).setValue(progress)
            .addOnSuccessListener {
                println("Progress saved successfully for user $userId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to save progress: ${exception.message}")
                exception.printStackTrace()
            }
    }

    // Получение прогресса тренировки для конкретного пользователя
    suspend fun getWorkoutProgress(workoutId: String): Int {
        if (userId == "unknown_user") {
            println("User not logged in. Returning 0 progress.")
            return 0
        }

        return try {
            println("Loading progress for user $userId and workout ID: $workoutId")
            val snapshot = realtimeDatabase.getReference("user_progress").child(userId).child(workoutId).get().await()
            val progress = snapshot.getValue(Int::class.java) ?: 0
            println("Loaded progress: $progress for user $userId and workout ID: $workoutId")
            progress
        } catch (e: Exception) {
            println("Failed to load progress: ${e.message}")
            e.printStackTrace()
            0
        }
    }

    // Сброс прогресса тренировки для конкретного пользователя
    fun resetWorkoutProgress(workoutId: String) {
        if (userId == "unknown_user") {
            println("User not logged in. Cannot reset progress.")
            return
        }

        realtimeDatabase.getReference("user_progress").child(userId).child(workoutId).removeValue()
            .addOnSuccessListener {
                println("Progress reset successfully for user $userId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to reset progress: ${exception.message}")
                exception.printStackTrace()
            }
    }
}

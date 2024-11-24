package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.apps_for_individual_train.data.WorkoutData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class WorkoutSelectionViewModel @Inject constructor(
    private val realtimeDatabase: FirebaseDatabase
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<WorkoutData>>(emptyList())
    val workouts: StateFlow<List<WorkoutData>> = _workouts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Загрузка тренировок по категории из Realtime Database
    fun loadWorkoutsByCategory(category: String) {
        _isLoading.value = true

        realtimeDatabase.getReference("workouts").get()
            .addOnSuccessListener { snapshot ->
                val workoutList = snapshot.children.mapNotNull { dataSnapshot ->
                    val workout = dataSnapshot.getValue(WorkoutData::class.java)
                    workout?.takeIf { it.category == category }?.copy(id = dataSnapshot.key ?: "")
                }
                _workouts.value = workoutList
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                println("Failed to load workouts: ${exception.message}")
                _workouts.value = emptyList()
                _isLoading.value = false
            }
    }

    // Преобразование gs:// ссылки в https:// для видео URL
    fun fetchVideoUrl(storagePath: String, onResult: (String?) -> Unit) {
        if (storagePath.startsWith("gs://")) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storagePath)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    onResult(uri.toString()) // Преобразуем в HTTP URL
                }
                .addOnFailureListener { exception ->
                    println("Failed to fetch video URL: ${exception.message}")
                    onResult(null)
                }
        } else {
            onResult(storagePath) // Если это уже HTTP URL, просто возвращаем его
        }
    }

    // Добавление тренировок-заглушек в Realtime Database
    fun addWorkoutStubData() {
        val placeholderWorkouts = listOf(
            WorkoutData(
                id = "",
                name = "Beginner Cardio Workout",
                description = "A beginner-level cardio workout to boost your endurance.",
                duration = 30,
                difficulty = "Beginner",
                category = "Cardio",
                videoUrl = "gs://patlaty-d6c72.appspot.com/7263170267859.mp4"
            ),
            WorkoutData(
                id = "",
                name = "Intermediate Strength Training",
                description = "An intermediate-level strength workout for muscle building.",
                duration = 45,
                difficulty = "Intermediate",
                category = "Strength",
                videoUrl = "gs://patlaty-d6c72.appspot.com/strength_workout.mp4"
            ),
            WorkoutData(
                id = "",
                name = "Advanced Yoga Flow",
                description = "An advanced yoga session for flexibility and relaxation.",
                duration = 60,
                difficulty = "Advanced",
                category = "Yoga",
                videoUrl = "gs://patlaty-d6c72.appspot.com/yoga_workout.mp4"
            )
        )

        placeholderWorkouts.forEach { workout ->
            realtimeDatabase.getReference("workouts").push().setValue(workout)
                .addOnSuccessListener {
                    println("Workout added successfully.")
                }
                .addOnFailureListener { exception ->
                    println("Failed to add workout: ${exception.message}")
                    exception.printStackTrace()
                }
        }
    }
}

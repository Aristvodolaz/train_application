package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.apps_for_individual_train.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDatabase: FirebaseDatabase
) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    init {
        // Инициализировать текущее состояние пользователя при запуске ViewModel
        checkUser()
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(login, password).await()
                _user.value = firebaseAuth.currentUser
                _errorMessage.value = null
                loadUser()
            } catch (e: Exception) {
                _user.value = null
                _errorMessage.value = e.message
            }
        }
    }

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(login, password).await()
                _user.value = firebaseAuth.currentUser
                _errorMessage.value = null
                registerUser(name) // Сохранение дополнительных данных пользователя
            } catch (e: Exception) {
                _user.value = null
                _errorMessage.value = e.message
            }
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
        _user.value = null
        _errorMessage.value = null
        _userData.value = null
    }

    private fun checkUser() {
        _user.value = firebaseAuth.currentUser
        if (firebaseAuth.currentUser != null) {
            loadUser()
        }
    }

    private fun loadUser() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userRef = realtimeDatabase.getReference("users").child(uid)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dataSnapshot = userRef.get().await()
                _userData.postValue(dataSnapshot.getValue<UserData>())
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }

    private fun registerUser(name: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userData = UserData(name, firebaseAuth.currentUser?.email ?: "")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save basic user data
                realtimeDatabase.getReference("users").child(uid).setValue(userData).await()

                // Initialize empty statistics for new user
                val userStatsRef = realtimeDatabase.getReference("user_statistics").child(uid)
                val initialStats = mapOf(
                    "workouts_count" to "0",
                    "hours_count" to "0",
                    "calories_burned" to "0",
                    "distance_covered" to "0"
                )
                userStatsRef.setValue(initialStats).await()

                // Initialize empty nutrition data for new user
                val userNutritionRef = realtimeDatabase.getReference("user_nutrition").child(uid)
                val initialNutrition = mapOf(
                    "water_intake" to 0,
                    "water_goal" to 2500,
                    "proteins" to 0,
                    "carbs" to 0,
                    "fats" to 0,
                    "calories_consumed" to 0,
                    "calories_goal" to 2000
                )
                userNutritionRef.setValue(initialNutrition).await()

                // Initialize empty workout progress for new user
                val userProgressRef = realtimeDatabase.getReference("user_progress").child(uid)
                userProgressRef.setValue(mapOf<String, Int>()).await()

                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }
}

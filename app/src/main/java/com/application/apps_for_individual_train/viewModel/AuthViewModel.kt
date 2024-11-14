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
                realtimeDatabase.getReference("users").child(uid).setValue(userData).await()
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }
    }
}

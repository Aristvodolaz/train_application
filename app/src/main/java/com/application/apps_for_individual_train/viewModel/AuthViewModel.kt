package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.apps_for_individual_train.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class AuthViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDatabase: FirebaseDatabase
): ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    fun login(login: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(login, password)
            .addOnCompleteListener  { task ->
                if(task.isSuccessful){
                    _user.value = firebaseAuth.currentUser
                    _errorMessage.value = null
                } else{
                    _user.value = null
                    _errorMessage.value = task.exception?.message
                }
            }
    }

    fun register(login: String, password: String, name: String){
        firebaseAuth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _user.value = firebaseAuth.currentUser
                    _errorMessage.value = null
                } else{
                    _user.value = null
                    _errorMessage.value = task.exception?.message
                }
            }
    }

    fun logOut(){
        firebaseAuth.signOut()
        _user.value = null
        _errorMessage.value = null
    }

    fun checkUser(){
        _user.value = firebaseAuth.currentUser
        if(firebaseAuth.currentUser != null) loadUser()
    }

    fun loadUser(){
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userRef = realtimeDatabase.getReference("users").child(uid)

        userRef.get().addOnSuccessListener { data ->
            _userData.value = data.getValue<UserData>()
        }.addOnFailureListener { err ->
            _errorMessage.value = err.message
        }
    }

    fun registerUser(name: String){
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userData = UserData(name, firebaseAuth.currentUser?.email ?: "")

        realtimeDatabase.getReference("users").child(uid).setValue(userData)
    }
}
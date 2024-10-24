package com.application.apps_for_individual_train.viewModel

import androidx.lifecycle.ViewModel
import com.application.apps_for_individual_train.di.TestDependency
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val realtimeDatabase: FirebaseDatabase
):ViewModel() {

    fun fetchRealtimeDatabase(){
        val ref = realtimeDatabase.getReference("users")
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){

            }
        }.addOnFailureListener { err ->

        }
    }


}
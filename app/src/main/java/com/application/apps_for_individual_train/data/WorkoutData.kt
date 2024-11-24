package com.application.apps_for_individual_train.data

data class WorkoutData(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val duration: Int = 0,
    val description: String = "",
    val difficulty: String  = "",
    val videoUrl: String = ""
)

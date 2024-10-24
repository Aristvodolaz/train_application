package com.application.apps_for_individual_train.di

import javax.inject.Inject

class TestDependency @Inject constructor() {
    fun testFunction() = "Test function"
}
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google() // Убедись, что Google репозиторий подключен
        mavenCentral()
        gradlePluginPortal()
        plugins {
            id("com.google.dagger.hilt.android") version "2.45" // Указываем версию Hilt плагина
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "apps_for_individual_train"
include(":app")
 
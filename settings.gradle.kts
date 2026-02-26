pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        // Используем версии из libs.versions.toml
        // Или укажи явно (пример для Kotlin 2.0.0):
        val kotlinVersion = "2.0.0"
        val agpVersion = "8.5.0"
        val hiltVersion = "2.57.1"
        val kspVersion = "2.0.0-1.0.25"

        id("com.android.application") version agpVersion
        id("org.jetbrains.kotlin.android") version kotlinVersion
        id("com.google.dagger.hilt.android") version hiltVersion
        id("com.google.devtools.ksp") version kspVersion
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "PiggyBank"
include(":app")
 
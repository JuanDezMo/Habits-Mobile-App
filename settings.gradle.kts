// Configuración general del proyecto: nombre y módulos que lo componen.
rootProject.name = "BuildingHabitTracker"

// Permite escribir `projects.shared` en lugar de `project(":shared")`.
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// shared:     código compartido (UI, domain, data, di) para Android e iOS.
// androidApp: la app de Android; solo arranca y muestra la UI de `shared`.
// iosApp:     proyecto de Xcode (no es un módulo de Gradle); arranca la UI de `shared`.
include(":shared")
include(":androidApp")

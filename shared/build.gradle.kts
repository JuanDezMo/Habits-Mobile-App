import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Módulo compartido: aquí vive casi toda la app (UI, domain, data, di).
// Se compila para Android (como librería) y para iOS (como framework "Shared").
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    // Room usa `expect object` para construir la base de datos en cada plataforma.
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // iOS: dispositivo real (Arm64) y simulador en Mac con Apple Silicon.
    // Solo se compilan en macOS; en Linux se omiten (ver gradle.properties).
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    // Android: este módulo es una librería; la app es el módulo :androidApp.
    androidLibrary {
        namespace = "co.com.buildinghabittracker.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        // Habilita las pruebas que corren en la computadora (sin emulador).
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider.optimal)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}

// Room: carpeta donde se guardan los esquemas de la base de datos (historial de migraciones).
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP genera el código de Room para cada plataforma.
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    androidRuntimeClasspath(libs.compose.uiTooling)
}

// Textos traducidos (composeResources/values y values-es).
compose.resources {
    publicResClass = false
    packageOfResClass = "co.com.buildinghabittracker.resources"
}

package co.com.buildinghabittracker.di

import co.com.buildinghabittracker.data.local.databaseBuilder
import co.com.buildinghabittracker.data.settings.SETTINGS_FILE_NAME
import co.com.buildinghabittracker.data.settings.androidDeviceLanguage
import co.com.buildinghabittracker.data.settings.createSettingsDataStore
import co.com.buildinghabittracker.domain.settings.DeviceLanguage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android: la base de datos y las preferencias necesitan el `Context` que entregó
 * HabitTrackerApplication (para saber dónde guardar los archivos).
 */
actual val platformModule: Module = module {
    single { databaseBuilder(androidContext()) }
    single { createSettingsDataStore { androidContext().filesDir.resolve(SETTINGS_FILE_NAME).absolutePath } }
    single<DeviceLanguage> { androidDeviceLanguage }
}

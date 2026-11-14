package co.com.buildinghabittracker.di

import co.com.buildinghabittracker.data.local.databaseBuilder
import co.com.buildinghabittracker.data.settings.SETTINGS_FILE_NAME
import co.com.buildinghabittracker.data.settings.createSettingsDataStore
import co.com.buildinghabittracker.data.settings.iosDeviceLanguage
import co.com.buildinghabittracker.domain.settings.DeviceLanguage
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** iOS: la base de datos y las preferencias se guardan en la carpeta Documents. */
@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    single { databaseBuilder() }
    single {
        createSettingsDataStore {
            val documents = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            requireNotNull(documents?.path) + "/$SETTINGS_FILE_NAME"
        }
    }
    single<DeviceLanguage> { iosDeviceLanguage }
}

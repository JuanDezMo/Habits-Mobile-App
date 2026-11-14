package co.com.buildinghabittracker.di

import co.com.buildinghabittracker.data.local.databaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

/** iOS: la base de datos se guarda en la carpeta Documents; no necesita nada más. */
actual val platformModule: Module = module {
    single { databaseBuilder() }
}

package co.com.buildinghabittracker.di

import co.com.buildinghabittracker.data.local.databaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Android: la base de datos necesita el `Context` que entregó HabitTrackerApplication. */
actual val platformModule: Module = module {
    single { databaseBuilder(androidContext()) }
}

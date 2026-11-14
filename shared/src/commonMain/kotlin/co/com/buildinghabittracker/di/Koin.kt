package co.com.buildinghabittracker.di

import co.com.buildinghabittracker.data.local.AppDatabase
import co.com.buildinghabittracker.data.local.buildAppDatabase
import co.com.buildinghabittracker.data.security.Pbkdf2SecretHasher
import co.com.buildinghabittracker.data.user.UserRepositoryImpl
import co.com.buildinghabittracker.domain.auth.AppLock
import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.user.UserRepository
import co.com.buildinghabittracker.ui.pin.PinViewModel
import co.com.buildinghabittracker.ui.recover.RecoverViewModel
import co.com.buildinghabittracker.ui.register.RegisterViewModel
import co.com.buildinghabittracker.ui.start.StartViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * Lo que cambia según la plataforma. Cada una entrega aquí su `RoomDatabase.Builder`
 * (Android necesita el `Context`; iOS, la carpeta Documents).
 */
expect val platformModule: Module

/**
 * Capa data: cada interfaz de domain se conecta con su implementación real.
 * `single` = una sola instancia para toda la app.
 */
val dataModule = module {
    single<AppDatabase> { buildAppDatabase(get()) }
    single { get<AppDatabase>().userDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<SecretHasher> { Pbkdf2SecretHasher() }
    single<Clock> { Clock.System }
    single { AppLock(get()) }
}

/** ViewModels: Koin crea uno nuevo para cada pantalla y le entrega sus dependencias. */
val viewModelModule = module {
    viewModelOf(::StartViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PinViewModel)
    viewModelOf(::RecoverViewModel)
}

/**
 * Inicia Koin. Lo llaman Android (HabitTrackerApplication) e iOS (startKoinForIos).
 *
 * @param platformConfig configuración extra de cada plataforma. Android la usa para
 * entregar su `Context` con `androidContext(...)`.
 */
fun initKoin(platformConfig: KoinAppDeclaration? = null) {
    startKoin {
        platformConfig?.invoke(this)
        modules(platformModule, dataModule, viewModelModule)
    }
}

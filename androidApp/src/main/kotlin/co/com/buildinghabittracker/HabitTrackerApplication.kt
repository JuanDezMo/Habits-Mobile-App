package co.com.buildinghabittracker

import android.app.Application
import co.com.buildinghabittracker.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Punto de entrada del proceso en Android. Se ejecuta una sola vez, antes que cualquier pantalla.
 *
 * Aquí se inicia Koin (inyección de dependencias). Le pasamos el `Context` de Android porque
 * más adelante Room y las notificaciones lo necesitan para funcionar.
 */
class HabitTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@HabitTrackerApplication)
        }
    }
}

package co.com.buildinghabittracker.data.local

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** Nombre del archivo de la base de datos en el teléfono. */
const val DATABASE_NAME = "habit_tracker.db"

/**
 * La base de datos local (Room). Cada iteración agrega sus tablas a `entities`
 * y sube la `version` (iteración 2: `habit`, iteración 4: `check_in`).
 *
 * Historial de versiones y migraciones:
 * - v1 (iteración 1): tabla `user`.
 * - v2 (iteración 2): tabla `habit`. AutoMigration(1 → 2) crea la tabla sin borrar al usuario.
 *
 * Room compara los esquemas exportados en `shared/schemas/` (1.json, 2.json...) y genera
 * la migración solo. Esos archivos se guardan en el proyecto: son el historial de la base.
 */
@Database(
    entities = [UserEntity::class, HabitEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@TypeConverters(HabitConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
}

/** Room genera la implementación de este objeto para cada plataforma (Android e iOS). */
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

/**
 * Termina de armar la base de datos. Cada plataforma entrega su `builder` (que sabe DÓNDE
 * guardar el archivo) y aquí se configura lo común:
 * - BundledSQLiteDriver: la misma versión de SQLite en Android e iOS.
 * - Dispatchers.IO: las consultas corren fuera del hilo de la pantalla.
 */
fun buildAppDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

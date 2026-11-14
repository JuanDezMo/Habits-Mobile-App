package co.com.buildinghabittracker.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Android: guarda la base de datos en la carpeta privada de la app. */
fun databaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}

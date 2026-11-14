package co.com.buildinghabittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO de la tabla `habit`.
 *
 * `observeAll` devuelve un `Flow`: Room vuelve a emitir la lista cada vez que la tabla cambia,
 * así la pantalla se actualiza sola al crear, editar o archivar.
 */
@Dao
interface HabitDao {
    @Query("SELECT * FROM habit ORDER BY createdAt, id")
    fun observeAll(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit WHERE id = :id")
    suspend fun get(id: Long): HabitEntity?

    /** Devuelve el id nuevo que asignó SQLite. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("UPDATE habit SET isArchived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)
}

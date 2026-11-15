package co.com.buildinghabittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/** DAO de la tabla `check_in`. */
@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_in ORDER BY date")
    fun observeAll(): Flow<List<CheckInEntity>>

    /**
     * D2: `IGNORE` = si ya existe ese hábito en ese día (índice único), no hace nada
     * en vez de fallar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checkIn: CheckInEntity)

    @Query("DELETE FROM check_in WHERE habitId = :habitId AND date = :date")
    suspend fun delete(habitId: Long, date: LocalDate)
}

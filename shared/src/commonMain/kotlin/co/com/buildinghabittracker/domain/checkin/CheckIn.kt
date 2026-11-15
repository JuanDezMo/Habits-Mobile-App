package co.com.buildinghabittracker.domain.checkin

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/**
 * Un check-in: el hábito [habitId] se marcó como hecho el día [date] (C4: hecho / no hecho).
 *
 * @property date el día que cuenta para la racha (hoy o ayer, regla D1).
 * @property checkedAt el momento real en que se tocó. Sirve para saber si un día de ayer
 * se marcó hoy: así la sección "Ayer" lo sigue mostrando con ✓ hasta medianoche.
 */
data class CheckIn(
    val habitId: Long,
    val date: LocalDate,
    val checkedAt: Instant,
)

/**
 * Contrato para guardar y leer check-ins.
 *
 * - En la app: `CheckInRepositoryImpl` con Room (tabla `check_in`).
 * - En las pruebas: `FakeCheckInRepository`, en memoria.
 */
interface CheckInRepository {
    /** Todos los check-ins. Es un `Flow`: emite de nuevo cada vez que se marca o desmarca. */
    fun observeCheckIns(): Flow<List<CheckIn>>

    /** D2: guarda el check-in; si ese hábito ya estaba marcado ese día, no hace nada. */
    suspend fun add(checkIn: CheckIn)

    /** D3: quita el check-in de ese hábito en ese día (si no existe, no hace nada). */
    suspend fun remove(habitId: Long, date: LocalDate)
}

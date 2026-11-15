package co.com.buildinghabittracker.fakes

import co.com.buildinghabittracker.domain.checkin.CheckIn
import co.com.buildinghabittracker.domain.checkin.CheckInRepository
import co.com.buildinghabittracker.domain.common.TodayProvider
import co.com.buildinghabittracker.domain.habit.Habit
import co.com.buildinghabittracker.domain.habit.HabitRepository
import co.com.buildinghabittracker.domain.security.SecretHasher
import co.com.buildinghabittracker.domain.settings.AppLanguage
import co.com.buildinghabittracker.domain.settings.AppSettings
import co.com.buildinghabittracker.domain.settings.AppTheme
import co.com.buildinghabittracker.domain.settings.SettingsRepository
import co.com.buildinghabittracker.domain.user.HashedSecret
import co.com.buildinghabittracker.domain.user.User
import co.com.buildinghabittracker.domain.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Implementaciones falsas para pruebas ("fakes").
 *
 * Reemplazan a las reales sin que el ViewModel lo note, porque cumplen la misma interfaz
 * (la "L" de SOLID: sustitución). Son rápidas y no tocan disco ni cifrado real.
 */

/** Guarda al usuario en una variable, en memoria. */
class FakeUserRepository(var user: User? = null) : UserRepository {
    override suspend fun getUser(): User? = user
    override suspend fun createUser(user: User) { this.user = user }
    override suspend fun updateLockState(failedAttempts: Int, lockedUntil: Instant?) {
        user = user?.copy(failedAttempts = failedAttempts, lockedUntil = lockedUntil)
    }
    override suspend fun updatePin(pin: HashedSecret) { user = user?.copy(pin = pin) }
}

/** "Cifra" agregando un prefijo: instantáneo y fácil de leer en las pruebas. */
class FakeSecretHasher : SecretHasher {
    override suspend fun hash(secret: String) = HashedSecret(hash = "hashed:$secret", salt = "salt")
    override suspend fun verify(secret: String, hashed: HashedSecret) = hashed.hash == "hashed:$secret"
}

/** Reloj que solo avanza cuando la prueba lo pide con [advanceBy]. */
class FakeClock(var current: Instant = Instant.parse("2026-11-15T10:00:00Z")) : Clock {
    override fun now(): Instant = current
    fun advanceBy(duration: Duration) { current += duration }
}

/** Usuario de ejemplo con PIN 1357 y respuesta "firulais". */
fun sampleUser(
    failedAttempts: Int = 0,
    lockedUntil: Instant? = null,
) = User(
    name = "Ana",
    email = "ana@correo.com",
    pin = HashedSecret("hashed:1357", "salt"),
    securityQuestion = "¿Nombre de mi primera mascota?",
    securityAnswer = HashedSecret("hashed:firulais", "salt"),
    failedAttempts = failedAttempts,
    lockedUntil = lockedUntil,
    createdAt = Instant.parse("2026-11-01T08:00:00Z"),
)

/**
 * Guarda los hábitos en un `MutableStateFlow`, en memoria.
 * Igual que Room, cada cambio emite la lista nueva a quien la esté observando.
 */
class FakeHabitRepository(initial: List<Habit> = emptyList()) : HabitRepository {
    private val habits = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    /** Lo guardado en este momento; las pruebas lo revisan directamente. */
    val saved: List<Habit> get() = habits.value

    override fun observeHabits(): Flow<List<Habit>> = habits

    override suspend fun getHabit(id: Long): Habit? = habits.value.firstOrNull { it.id == id }

    override suspend fun createHabit(habit: Habit): Long {
        val id = nextId++
        habits.update { it + habit.copy(id = id) }
        return id
    }

    override suspend fun updateHabit(habit: Habit) =
        habits.update { list -> list.map { if (it.id == habit.id) habit else it } }

    override suspend fun setArchived(id: Long, archived: Boolean) =
        habits.update { list -> list.map { if (it.id == id) it.copy(isArchived = archived) else it } }
}

/** Hábito de ejemplo: "Gym" lunes, miércoles y viernes. */
fun sampleHabit(
    id: Long = 1,
    title: String = "Gym",
    isArchived: Boolean = false,
    days: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
    createdAt: LocalDate = LocalDate(2026, 11, 1),
) = Habit(
    id = id,
    title = title,
    description = "30 minutos de pesas",
    emoji = "🏋️",
    color = "#2E8B57",
    days = days,
    isArchived = isArchived,
    createdAt = createdAt,
)

/** Preferencias en memoria; emite cada cambio como DataStore. */
class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    val current: AppSettings get() = state.value
    override val settings: Flow<AppSettings> = state
    override suspend fun setLanguage(language: AppLanguage) = state.update { it.copy(language = language) }
    override suspend fun setTheme(theme: AppTheme) = state.update { it.copy(theme = theme) }
}

/**
 * Check-ins en memoria. Igual que la tabla real, no guarda dos del mismo hábito el mismo día (D2).
 */
class FakeCheckInRepository(initial: List<CheckIn> = emptyList()) : CheckInRepository {
    private val checkIns = MutableStateFlow(initial)
    val saved: List<CheckIn> get() = checkIns.value

    override fun observeCheckIns(): Flow<List<CheckIn>> = checkIns

    override suspend fun add(checkIn: CheckIn) = checkIns.update { list ->
        if (list.any { it.habitId == checkIn.habitId && it.date == checkIn.date }) list else list + checkIn
    }

    override suspend fun remove(habitId: Long, date: LocalDate) =
        checkIns.update { list -> list.filterNot { it.habitId == habitId && it.date == date } }
}

/** "Hoy" que la prueba cambia a mano, por ejemplo para pasar la medianoche. */
class FakeTodayProvider(today: LocalDate) : TodayProvider {
    val today = MutableStateFlow(today)
    override fun observeToday(): Flow<LocalDate> = today
}

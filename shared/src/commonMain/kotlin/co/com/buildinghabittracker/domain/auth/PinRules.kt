package co.com.buildinghabittracker.domain.auth

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Reglas del PIN y del bloqueo por intentos (A3, A5, A6).
 *
 * Son funciones puras: reciben datos y devuelven un resultado, sin leer la base de datos ni
 * la hora del sistema. Por eso se prueban con una línea cada una (ver PinRulesTest).
 */
object PinRules {
    const val PIN_LENGTH = 4
    const val ATTEMPTS_BEFORE_LOCK = 5
    val FIRST_LOCK: Duration = 30.seconds
    val MAX_LOCK: Duration = 15.minutes
    val BACKGROUND_TIMEOUT: Duration = 5.minutes

    /** A3: exactamente 4 dígitos. */
    fun isValidPin(pin: String): Boolean =
        pin.length == PIN_LENGTH && pin.all { it in '0'..'9' }

    /**
     * A6: cuánto dura el bloqueo según los fallos acumulados.
     * Menos de 5 fallos: sin bloqueo. Desde el 5.º: 30 s, y se duplica hasta 15 min.
     *
     * Ejemplos: 5 → 30 s, 6 → 1 min, 7 → 2 min, 9 → 8 min, 10 o más → 15 min.
     */
    fun lockDuration(failedAttempts: Int): Duration {
        if (failedAttempts < ATTEMPTS_BEFORE_LOCK) return Duration.ZERO
        val doublings = failedAttempts - ATTEMPTS_BEFORE_LOCK
        // Se limita el exponente para no desbordar el número con muchos fallos.
        val duration = FIRST_LOCK * (1 shl doublings.coerceAtMost(10))
        return minOf(duration, MAX_LOCK)
    }

    /** A6: resultado de registrar un fallo más. */
    fun afterFailure(previousFailedAttempts: Int, now: Instant): LockState {
        val failed = previousFailedAttempts + 1
        val lock = lockDuration(failed)
        return LockState(
            failedAttempts = failed,
            lockedUntil = if (lock > Duration.ZERO) now + lock else null,
        )
    }

    /** A6: tiempo que falta de bloqueo; `Duration.ZERO` si ya puede intentar. */
    fun remainingLock(lockedUntil: Instant?, now: Instant): Duration =
        if (lockedUntil == null || lockedUntil <= now) Duration.ZERO else lockedUntil - now

    /** Intentos que quedan antes del bloqueo, para el mensaje "Te quedan N intentos". */
    fun attemptsLeftBeforeLock(failedAttempts: Int): Int =
        (ATTEMPTS_BEFORE_LOCK - failedAttempts).coerceAtLeast(0)

    /** A5: ¿hay que pedir el PIN al volver a la app? */
    fun requiresPinAfterBackground(wentToBackgroundAt: Instant, now: Instant): Boolean =
        now - wentToBackgroundAt >= BACKGROUND_TIMEOUT
}

/** Estado del bloqueo después de un intento fallido. */
data class LockState(
    val failedAttempts: Int,
    val lockedUntil: Instant?,
)

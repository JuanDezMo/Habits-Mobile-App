package co.com.buildinghabittracker.ui.auth

import co.com.buildinghabittracker.domain.auth.PinRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Cuenta regresiva del bloqueo (regla A6). La usan PinViewModel y RecoverViewModel.
 *
 * Cada segundo calcula cuánto falta con [PinRules.remainingLock] y avisa con [onTick].
 * Termina sola cuando el bloqueo llega a cero.
 */
fun CoroutineScope.launchLockCountdown(
    lockedUntil: Instant,
    clock: Clock,
    onTick: (remaining: Duration) -> Unit,
): Job = launch {
    while (true) {
        val remaining = PinRules.remainingLock(lockedUntil, clock.now())
        onTick(remaining)
        if (remaining == Duration.ZERO) break
        delay(1.seconds)
    }
}

/** Formato mm:ss para mostrar el tiempo de bloqueo, por ejemplo 01:30. */
fun Duration.toMinutesSeconds(): String {
    val totalSeconds = inWholeSeconds
    val minutes = (totalSeconds / 60).toString().padStart(2, '0')
    val seconds = (totalSeconds % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}

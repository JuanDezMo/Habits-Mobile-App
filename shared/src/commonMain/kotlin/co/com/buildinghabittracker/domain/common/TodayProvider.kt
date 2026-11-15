package co.com.buildinghabittracker.domain.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Entrega el día de hoy y avisa cuando cambia (a medianoche). Regla E7: se usa la zona
 * horaria del teléfono.
 *
 * Es una interfaz para que las pruebas puedan "mover" el día sin esperar a medianoche.
 */
interface TodayProvider {
    /** Emite hoy y vuelve a emitir cada vez que cambia de día. */
    fun observeToday(): Flow<LocalDate>
}

/**
 * Implementación real: revisa la fecha cada minuto.
 *
 * ¿Por qué no esperar hasta la medianoche exacta? Porque si el teléfono se duerme, la
 * espera se alarga; revisando cada minuto, al despertar se corrige en menos de un minuto.
 * `distinctUntilChanged` hace que solo se emita cuando la fecha cambia de verdad.
 */
class SystemTodayProvider(
    private val clock: Clock,
    private val timeZone: TimeZone,
) : TodayProvider {
    override fun observeToday(): Flow<LocalDate> = flow {
        while (true) {
            emit(clock.now().toLocalDateTime(timeZone).date)
            delay(1.minutes)
        }
    }.distinctUntilChanged()
}

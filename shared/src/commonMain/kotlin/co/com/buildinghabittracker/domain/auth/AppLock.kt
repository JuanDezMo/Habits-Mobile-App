package co.com.buildinghabittracker.domain.auth

import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Recuerda si la app está desbloqueada mientras está abierta (regla A5).
 *
 * Vive solo en memoria: al cerrar la app del todo se pierde, y por eso al abrirla siempre
 * se pide el PIN. Recibe un [Clock] en vez de leer la hora directamente; así las pruebas
 * pueden "adelantar el reloj" sin esperar 5 minutos de verdad.
 */
class AppLock(
    private val clock: Clock,
) {
    var isUnlocked: Boolean = false
        private set

    private var wentToBackgroundAt: Instant? = null

    /** Se llama cuando el PIN (o la recuperación) es correcto. */
    fun unlock() {
        isUnlocked = true
        wentToBackgroundAt = null
    }

    /** La app pasó a segundo plano: se anota la hora. */
    fun onAppBackground() {
        if (isUnlocked) wentToBackgroundAt = clock.now()
    }

    /**
     * La app volvió al frente.
     * @return `true` si estuvo 5 minutos o más afuera y hay que pedir el PIN otra vez.
     */
    fun onAppForeground(): Boolean {
        val since = wentToBackgroundAt ?: return false
        wentToBackgroundAt = null
        if (PinRules.requiresPinAfterBackground(since, clock.now())) {
            isUnlocked = false
            return true
        }
        return false
    }
}

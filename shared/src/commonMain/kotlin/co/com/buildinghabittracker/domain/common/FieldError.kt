package co.com.buildinghabittracker.domain.common

/**
 * Errores de validación de un campo de formulario (registro y hábitos).
 * La UI los traduce a un texto en el idioma elegido.
 */
enum class FieldError {
    REQUIRED,
    TOO_SHORT,
    TOO_LONG,
    INVALID_EMAIL,

    /** C1: ya existe un hábito activo con el mismo título. */
    DUPLICATE,
}

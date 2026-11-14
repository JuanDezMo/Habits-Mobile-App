package co.com.buildinghabittracker.domain.auth

import co.com.buildinghabittracker.domain.common.FieldError

/**
 * Reglas de los datos del registro (A2 y A4). Funciones puras: devuelven `null` si el
 * valor es válido, o el [FieldError] que corresponde.
 */
object RegistrationRules {
    const val NAME_MAX = 30
    const val EMAIL_MAX = 254
    const val QUESTION_MIN = 5
    const val QUESTION_MAX = 100
    const val ANSWER_MIN = 3
    const val ANSWER_MAX = 50

    // Formato básico: algo@algo.algo, sin espacios. No se verifica que el correo exista.
    private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    /** A2: nombre obligatorio, de 1 a 30 caracteres (sin contar espacios al inicio o final). */
    fun validateName(name: String): FieldError? {
        val value = name.trim()
        return when {
            value.isEmpty() -> FieldError.REQUIRED
            value.length > NAME_MAX -> FieldError.TOO_LONG
            else -> null
        }
    }

    /** A2: email obligatorio, con formato válido y máximo 254 caracteres. */
    fun validateEmail(email: String): FieldError? {
        val value = email.trim()
        return when {
            value.isEmpty() -> FieldError.REQUIRED
            value.length > EMAIL_MAX -> FieldError.TOO_LONG
            !emailPattern.matches(value) -> FieldError.INVALID_EMAIL
            else -> null
        }
    }

    /** A4: pregunta de 5 a 100 caracteres. */
    fun validateQuestion(question: String): FieldError? =
        validateLength(question.trim(), QUESTION_MIN, QUESTION_MAX)

    /** A4: respuesta de 3 a 50 caracteres. */
    fun validateAnswer(answer: String): FieldError? =
        validateLength(answer.trim(), ANSWER_MIN, ANSWER_MAX)

    private fun validateLength(value: String, min: Int, max: Int): FieldError? = when {
        value.isEmpty() -> FieldError.REQUIRED
        value.length < min -> FieldError.TOO_SHORT
        value.length > max -> FieldError.TOO_LONG
        else -> null
    }
}

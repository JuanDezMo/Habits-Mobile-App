package co.com.buildinghabittracker.ui.testing

/**
 * Identificadores de prueba de TODA la app, en un solo lugar.
 *
 * Maestro y Appium encuentran los elementos por estos valores:
 * - Android los ve como `resource-id` (ver [exposeTestTagsForUiTests]).
 * - iOS los ve como `accessibilityIdentifier` (Compose Multiplatform lo hace solo).
 *
 * Convención: `pantalla_elemento[_id]`, todo en minúsculas con guion bajo.
 * Reglas:
 * - En listas se usa el id del hábito, nunca su posición ni su título.
 * - Un identificador nunca contiene datos del usuario (nombre, email, título).
 *
 * Uso: `Modifier.testTag(TestTags.REGISTER_NAME_INPUT)`
 */
object TestTags {
    // Arranque
    const val START_SCREEN = "start_screen"

    // Registro (A1–A4)
    const val REGISTER_SCREEN = "register_screen"
    const val REGISTER_BACK_BUTTON = "register_back_button"
    const val REGISTER_NAME_INPUT = "register_name_input"
    const val REGISTER_NAME_ERROR = "register_name_error"
    const val REGISTER_EMAIL_INPUT = "register_email_input"
    const val REGISTER_EMAIL_ERROR = "register_email_error"
    const val REGISTER_CONTINUE_BUTTON = "register_continue_button"
    const val REGISTER_PIN_MISMATCH_TEXT = "register_pin_mismatch_text"
    const val REGISTER_QUESTION_INPUT = "register_question_input"
    const val REGISTER_QUESTION_ERROR = "register_question_error"
    const val REGISTER_ANSWER_INPUT = "register_answer_input"
    const val REGISTER_ANSWER_ERROR = "register_answer_error"
    const val REGISTER_FINISH_BUTTON = "register_finish_button"

    // PIN (A5, A6)
    const val PIN_SCREEN = "pin_screen"
    const val PIN_ERROR_TEXT = "pin_error_text"
    const val PIN_LOCK_TEXT = "pin_lock_text"
    const val PIN_FORGOT_BUTTON = "pin_forgot_button"

    // Recuperar PIN (A7)
    const val RECOVER_SCREEN = "recover_screen"
    const val RECOVER_BACK_BUTTON = "recover_back_button"
    const val RECOVER_QUESTION_TEXT = "recover_question_text"
    const val RECOVER_ANSWER_INPUT = "recover_answer_input"
    const val RECOVER_ERROR_TEXT = "recover_error_text"
    const val RECOVER_LOCK_TEXT = "recover_lock_text"
    const val RECOVER_VERIFY_BUTTON = "recover_verify_button"
    const val RECOVER_PIN_MISMATCH_TEXT = "recover_pin_mismatch_text"

    // Hoy
    const val TODAY_SCREEN = "today_screen"
    const val TODAY_HABITS_BUTTON = "today_habits_button"

    // Hábitos
    const val HABITS_SCREEN = "habits_screen"
    const val HABITS_OPEN_EXAMPLE_BUTTON = "habits_open_example_button"

    // Detalle
    const val DETAIL_SCREEN = "detail_screen"
    const val DETAIL_BACK_BUTTON = "detail_back_button"

    /**
     * Teclado de PIN: se usa en tres pantallas, así que el prefijo indica cuál.
     * Ejemplos: `pin_key_5`, `register_pin_key_5`, `recover_pin_delete`, `pin_dots`.
     */
    enum class PinPad(val prefix: String) {
        LOGIN("pin"),
        REGISTER("register_pin"),
        RECOVER("recover_pin");

        fun key(digit: Int) = "${prefix}_key_$digit"
        val delete get() = "${prefix}_delete"
        val dots get() = "${prefix}_dots"
    }

    /** Lista con todos los identificadores; la usan las pruebas para validar la convención. */
    val all: List<String> = listOf(
        START_SCREEN,
        REGISTER_SCREEN, REGISTER_BACK_BUTTON, REGISTER_NAME_INPUT, REGISTER_NAME_ERROR,
        REGISTER_EMAIL_INPUT, REGISTER_EMAIL_ERROR, REGISTER_CONTINUE_BUTTON, REGISTER_PIN_MISMATCH_TEXT,
        REGISTER_QUESTION_INPUT, REGISTER_QUESTION_ERROR, REGISTER_ANSWER_INPUT, REGISTER_ANSWER_ERROR,
        REGISTER_FINISH_BUTTON,
        PIN_SCREEN, PIN_ERROR_TEXT, PIN_LOCK_TEXT, PIN_FORGOT_BUTTON,
        RECOVER_SCREEN, RECOVER_BACK_BUTTON, RECOVER_QUESTION_TEXT, RECOVER_ANSWER_INPUT,
        RECOVER_ERROR_TEXT, RECOVER_LOCK_TEXT, RECOVER_VERIFY_BUTTON, RECOVER_PIN_MISMATCH_TEXT,
        TODAY_SCREEN, TODAY_HABITS_BUTTON,
        HABITS_SCREEN, HABITS_OPEN_EXAMPLE_BUTTON,
        DETAIL_SCREEN, DETAIL_BACK_BUTTON,
    ) + PinPad.entries.flatMap { pad -> (0..9).map { pad.key(it) } + pad.delete + pad.dots }
}

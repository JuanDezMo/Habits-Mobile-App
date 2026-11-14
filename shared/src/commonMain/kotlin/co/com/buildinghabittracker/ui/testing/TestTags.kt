package co.com.buildinghabittracker.ui.testing

import kotlinx.datetime.DayOfWeek

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

    // Barra inferior (Hoy / Hábitos / Ajustes)
    const val NAV_TODAY = "nav_today"
    const val NAV_HABITS = "nav_habits"
    const val NAV_SETTINGS = "nav_settings"

    // Hoy
    const val TODAY_SCREEN = "today_screen"

    // Hábitos (C5)
    const val HABITS_SCREEN = "habits_screen"
    const val HABITS_TAB_ACTIVE = "habits_tab_active"
    const val HABITS_TAB_ARCHIVED = "habits_tab_archived"
    const val HABITS_ADD_BUTTON = "habits_add_button"
    const val HABITS_EMPTY_TEXT = "habits_empty_text"
    const val HABITS_UNARCHIVE_BLOCKED_TEXT = "habits_unarchive_blocked_text"

    /** Fila de un hábito en la lista, por su id. Ejemplo: `habits_row_3`. */
    fun habitsRow(habitId: Long) = "habits_row_$habitId"

    /** Botón "Desarchivar" de un hábito archivado. Ejemplo: `habits_unarchive_3`. */
    fun habitsUnarchive(habitId: Long) = "habits_unarchive_$habitId"

    // Formulario de hábito: crear y editar (C1–C3), archivar (C5)
    const val FORM_SCREEN = "form_screen"
    const val FORM_BACK_BUTTON = "form_back_button"
    const val FORM_TITLE_INPUT = "form_title_input"
    const val FORM_TITLE_ERROR = "form_title_error"
    const val FORM_DESCRIPTION_INPUT = "form_description_input"
    const val FORM_DESCRIPTION_ERROR = "form_description_error"
    const val FORM_ALL_DAYS_BUTTON = "form_all_days_button"
    const val FORM_DAYS_ERROR = "form_days_error"
    const val FORM_SAVE_BUTTON = "form_save_button"
    const val FORM_ARCHIVE_BUTTON = "form_archive_button"
    const val FORM_ARCHIVE_CONFIRM_BUTTON = "form_archive_confirm_button"
    const val FORM_ARCHIVE_CANCEL_BUTTON = "form_archive_cancel_button"

    /** Opción de emoji por su posición en la lista fija (no es dato del usuario). Ejemplo: `form_emoji_0`. */
    fun formEmoji(index: Int) = "form_emoji_$index"

    /** Opción de color por su posición en la lista fija. Ejemplo: `form_color_2`. */
    fun formColor(index: Int) = "form_color_$index"

    /** Botón de un día. Ejemplo: `form_day_monday`. */
    fun formDay(day: DayOfWeek) = "form_day_${day.name.lowercase()}"

    // Ajustes (G4, A8)
    const val SETTINGS_SCREEN = "settings_screen"
    const val SETTINGS_LANGUAGE_ES = "settings_language_es"
    const val SETTINGS_LANGUAGE_EN = "settings_language_en"
    const val SETTINGS_THEME_SYSTEM = "settings_theme_system"
    const val SETTINGS_THEME_LIGHT = "settings_theme_light"
    const val SETTINGS_THEME_DARK = "settings_theme_dark"
    const val SETTINGS_CHANGE_PIN_BUTTON = "settings_change_pin_button"
    const val SETTINGS_PIN_CHANGED_TEXT = "settings_pin_changed_text"

    // Cambiar PIN (A8, A6)
    const val CHANGEPIN_SCREEN = "changepin_screen"
    const val CHANGEPIN_BACK_BUTTON = "changepin_back_button"
    const val CHANGEPIN_ERROR_TEXT = "changepin_error_text"
    const val CHANGEPIN_LOCK_TEXT = "changepin_lock_text"
    const val CHANGEPIN_MISMATCH_TEXT = "changepin_mismatch_text"

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
        RECOVER("recover_pin"),
        CHANGE("changepin_pin");

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
        NAV_TODAY, NAV_HABITS, NAV_SETTINGS,
        TODAY_SCREEN,
        HABITS_SCREEN, HABITS_TAB_ACTIVE, HABITS_TAB_ARCHIVED, HABITS_ADD_BUTTON,
        HABITS_EMPTY_TEXT, HABITS_UNARCHIVE_BLOCKED_TEXT, habitsRow(1), habitsUnarchive(1),
        FORM_SCREEN, FORM_BACK_BUTTON, FORM_TITLE_INPUT, FORM_TITLE_ERROR, FORM_DESCRIPTION_INPUT,
        FORM_DESCRIPTION_ERROR, FORM_ALL_DAYS_BUTTON, FORM_DAYS_ERROR, FORM_SAVE_BUTTON,
        FORM_ARCHIVE_BUTTON, FORM_ARCHIVE_CONFIRM_BUTTON, FORM_ARCHIVE_CANCEL_BUTTON,
        SETTINGS_SCREEN, SETTINGS_LANGUAGE_ES, SETTINGS_LANGUAGE_EN, SETTINGS_THEME_SYSTEM, SETTINGS_THEME_LIGHT,
        SETTINGS_THEME_DARK, SETTINGS_CHANGE_PIN_BUTTON, SETTINGS_PIN_CHANGED_TEXT,
        CHANGEPIN_SCREEN, CHANGEPIN_BACK_BUTTON, CHANGEPIN_ERROR_TEXT, CHANGEPIN_LOCK_TEXT, CHANGEPIN_MISMATCH_TEXT,
        DETAIL_SCREEN, DETAIL_BACK_BUTTON,
    ) + PinPad.entries.flatMap { pad -> (0..9).map { pad.key(it) } + pad.delete + pad.dots } +
        (0..11).map { formEmoji(it) } + (0..7).map { formColor(it) } + DayOfWeek.entries.map { formDay(it) }
}

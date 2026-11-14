package co.com.buildinghabittracker.domain.habit

import co.com.buildinghabittracker.domain.auth.AnswerNormalizer
import co.com.buildinghabittracker.domain.common.FieldError
import kotlinx.datetime.DayOfWeek

/**
 * Errores del formulario de hábito. Si todo es `null` y [noDays] es `false`, el hábito es válido.
 */
data class HabitErrors(
    val title: FieldError? = null,
    val description: FieldError? = null,
    val noDays: Boolean = false,
) {
    val isValid: Boolean get() = title == null && description == null && !noDays
}

/**
 * Reglas del formulario de hábito (C1–C3). Funciones puras: no guardan ni leen nada.
 */
object HabitValidator {
    const val TITLE_MAX = 40
    const val DESCRIPTION_MAX = 200

    /** C2: opciones del formulario. La primera de cada lista es el valor por defecto. */
    val EMOJIS = listOf("🏋️", "📖", "🧘", "🏃", "💧", "🥗", "😴", "✍️", "🎸", "🧹", "💊", "🌱")
    val COLORS = listOf("#2E8B57", "#3A7BD5", "#D2691E", "#8E44AD", "#C9A227", "#C0392B", "#16A085", "#5D6D7E")
    val DEFAULT_EMOJI = EMOJIS.first()
    val DEFAULT_COLOR = COLORS.first()

    /**
     * C1: título obligatorio, de 1 a 40 caracteres y sin repetir el de otro hábito activo.
     *
     * Dos títulos son "el mismo" si solo cambian mayúsculas, acentos o espacios extra
     * ("Leer" = " leér "). Se reutiliza la misma normalización de la respuesta de seguridad (A4).
     *
     * @param editingId id del hábito que se está editando, para no compararlo consigo mismo.
     */
    fun validateTitle(title: String, habits: List<Habit>, editingId: Long = Habit.NEW_ID): FieldError? {
        val value = title.trim()
        return when {
            value.isEmpty() -> FieldError.REQUIRED
            value.length > TITLE_MAX -> FieldError.TOO_LONG
            isDuplicate(value, habits, ignoreId = editingId) -> FieldError.DUPLICATE
            else -> null
        }
    }

    /** C2: descripción opcional, de máximo 200 caracteres. */
    fun validateDescription(description: String): FieldError? =
        if (description.trim().length > DESCRIPTION_MAX) FieldError.TOO_LONG else null

    /** Valida todo el formulario de una vez (C1, C2, C3). */
    fun validate(
        title: String,
        description: String,
        days: Set<DayOfWeek>,
        habits: List<Habit>,
        editingId: Long = Habit.NEW_ID,
    ) = HabitErrors(
        title = validateTitle(title, habits, editingId),
        description = validateDescription(description),
        noDays = days.isEmpty(), // C3: al menos un día
    )

    /**
     * C1 al desarchivar: no se puede si ya hay un hábito activo con el mismo título,
     * porque quedarían dos activos iguales.
     */
    fun canUnarchive(habit: Habit, habits: List<Habit>): Boolean =
        !isDuplicate(habit.title, habits, ignoreId = habit.id)

    private fun isDuplicate(title: String, habits: List<Habit>, ignoreId: Long): Boolean {
        val normalized = AnswerNormalizer.normalize(title)
        return habits.any { it.id != ignoreId && !it.isArchived && AnswerNormalizer.normalize(it.title) == normalized }
    }
}

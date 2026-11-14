package co.com.buildinghabittracker.domain.habit

import co.com.buildinghabittracker.domain.common.FieldError
import co.com.buildinghabittracker.fakes.sampleHabit
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pruebas de las reglas C1 (título), C2 (descripción) y C3 (días). */
class HabitValidatorTest {

    private val gym = sampleHabit(id = 1, title = "Gym")
    private val archivedRun = sampleHabit(id = 2, title = "Correr", isArchived = true)
    private val habits = listOf(gym, archivedRun)

    @Test
    fun c1_titulo_obligatorio() {
        assertEquals(FieldError.REQUIRED, HabitValidator.validateTitle("   ", habits))
    }

    @Test
    fun c1_titulo_de_40_es_valido_y_de_41_no() {
        assertNull(HabitValidator.validateTitle("a".repeat(40), habits))
        assertEquals(FieldError.TOO_LONG, HabitValidator.validateTitle("a".repeat(41), habits))
    }

    @Test
    fun c1_titulo_repetido_entre_activos_sin_importar_mayusculas_acentos_ni_espacios() {
        assertEquals(FieldError.DUPLICATE, HabitValidator.validateTitle("  gYM ", habits))
        val withRead = habits + sampleHabit(id = 3, title = "Leer   en la noche")
        assertEquals(FieldError.DUPLICATE, HabitValidator.validateTitle("leér en la NOCHE", withRead))
    }

    @Test
    fun c1_se_puede_repetir_el_titulo_de_un_archivado() {
        assertNull(HabitValidator.validateTitle("Correr", habits))
    }

    @Test
    fun c1_al_editar_no_se_compara_consigo_mismo() {
        assertNull(HabitValidator.validateTitle("Gym", habits, editingId = gym.id))
    }

    @Test
    fun c2_descripcion_opcional_de_maximo_200() {
        assertNull(HabitValidator.validateDescription(""))
        assertNull(HabitValidator.validateDescription("a".repeat(200)))
        assertEquals(FieldError.TOO_LONG, HabitValidator.validateDescription("a".repeat(201)))
    }

    @Test
    fun c2_emoji_y_color_tienen_valor_por_defecto() {
        assertEquals(HabitValidator.EMOJIS.first(), HabitValidator.DEFAULT_EMOJI)
        assertEquals(HabitValidator.COLORS.first(), HabitValidator.DEFAULT_COLOR)
    }

    @Test
    fun c3_al_menos_un_dia() {
        val errors = HabitValidator.validate("Leer", "", emptySet(), habits)
        assertTrue(errors.noDays)
        assertFalse(errors.isValid)
    }

    @Test
    fun formulario_valido() {
        val errors = HabitValidator.validate("Leer", "", setOf(DayOfWeek.MONDAY), habits)
        assertTrue(errors.isValid)
    }

    @Test
    fun c1_no_se_desarchiva_si_ya_hay_un_activo_con_ese_titulo() {
        val activeRun = sampleHabit(id = 3, title = "correr")
        assertFalse(HabitValidator.canUnarchive(archivedRun, habits + activeRun))
        assertTrue(HabitValidator.canUnarchive(archivedRun, habits))
    }
}

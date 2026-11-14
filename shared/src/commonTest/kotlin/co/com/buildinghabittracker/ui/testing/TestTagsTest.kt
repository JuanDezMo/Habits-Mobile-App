package co.com.buildinghabittracker.ui.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Primera prueba del proyecto (kotlin.test). Está en `commonTest`, así que corre para
 * Android y para iOS con el mismo código.
 *
 * Verifica la convención de identificadores que usan Maestro y Appium:
 * si alguien agrega un identificador mal escrito, esta prueba falla.
 */
class TestTagsTest {

    // pantalla_elemento[_id]: minúsculas, números y guion bajo; empieza con letra.
    private val convention = Regex("^[a-z]+(_[a-z0-9]+)+$")

    @Test
    fun todos_los_identificadores_siguen_la_convencion() {
        val invalid = TestTags.all.filterNot { convention.matches(it) }
        assertTrue(invalid.isEmpty(), "Identificadores que no siguen la convención: $invalid")
    }

    @Test
    fun no_hay_identificadores_repetidos() {
        val repeated = TestTags.all.groupBy { it }.filter { it.value.size > 1 }.keys
        assertEquals(emptySet(), repeated, "Identificadores repetidos")
    }

    @Test
    fun cada_identificador_empieza_con_el_nombre_de_su_pantalla() {
        val screens = listOf("start_", "register_", "pin_", "recover_", "today_", "habits_", "form_", "detail_")
        val withoutScreen = TestTags.all.filterNot { tag -> screens.any { tag.startsWith(it) } }
        assertTrue(withoutScreen.isEmpty(), "Identificadores sin prefijo de pantalla: $withoutScreen")
    }
}

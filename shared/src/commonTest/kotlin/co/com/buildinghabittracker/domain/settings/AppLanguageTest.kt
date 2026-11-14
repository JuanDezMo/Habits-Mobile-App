package co.com.buildinghabittracker.domain.settings

import kotlin.test.Test
import kotlin.test.assertEquals

/** G1: la primera vez, el idioma del teléfono; si no es español ni inglés, inglés. */
class AppLanguageTest {
    @Test
    fun g1_espanol_del_telefono_en_cualquier_pais() {
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromDeviceLanguage("es"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromDeviceLanguage("es-CO"))
    }

    @Test
    fun g1_otro_idioma_usa_ingles() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromDeviceLanguage("en"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromDeviceLanguage("fr"))
    }
}

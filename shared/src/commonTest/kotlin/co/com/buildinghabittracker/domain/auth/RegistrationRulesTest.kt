package co.com.buildinghabittracker.domain.auth

import co.com.buildinghabittracker.domain.common.FieldError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Pruebas de las reglas A2 (nombre, email) y A4 (pregunta, respuesta, normalización). */
class RegistrationRulesTest {

    @Test
    fun a2_nombre_obligatorio_de_1_a_30_caracteres() {
        assertEquals(FieldError.REQUIRED, RegistrationRules.validateName("   "))
        assertNull(RegistrationRules.validateName("Ana"))
        assertNull(RegistrationRules.validateName("a".repeat(30)))
        assertEquals(FieldError.TOO_LONG, RegistrationRules.validateName("a".repeat(31)))
    }

    @Test
    fun a2_email_con_formato_valido() {
        assertEquals(FieldError.REQUIRED, RegistrationRules.validateEmail(""))
        assertEquals(FieldError.INVALID_EMAIL, RegistrationRules.validateEmail("ana"))
        assertEquals(FieldError.INVALID_EMAIL, RegistrationRules.validateEmail("ana@correo"))
        assertEquals(FieldError.INVALID_EMAIL, RegistrationRules.validateEmail("ana perez@correo.com"))
        assertNull(RegistrationRules.validateEmail(" ana@correo.com "))
    }

    @Test
    fun a2_email_maximo_254_caracteres() {
        val long = "a".repeat(250) + "@b.co"
        assertEquals(FieldError.TOO_LONG, RegistrationRules.validateEmail(long))
    }

    @Test
    fun a4_pregunta_de_5_a_100_caracteres() {
        assertEquals(FieldError.REQUIRED, RegistrationRules.validateQuestion(""))
        assertEquals(FieldError.TOO_SHORT, RegistrationRules.validateQuestion("¿Qué"))
        assertNull(RegistrationRules.validateQuestion("¿Nombre de mi primera mascota?"))
        assertEquals(FieldError.TOO_LONG, RegistrationRules.validateQuestion("a".repeat(101)))
    }

    @Test
    fun a4_respuesta_de_3_a_50_caracteres() {
        assertEquals(FieldError.TOO_SHORT, RegistrationRules.validateAnswer("ab"))
        assertNull(RegistrationRules.validateAnswer("Tom"))
        assertEquals(FieldError.TOO_LONG, RegistrationRules.validateAnswer("a".repeat(51)))
    }

    @Test
    fun a4_la_respuesta_ignora_mayusculas_acentos_y_espacios_extra() {
        assertEquals("firulai lopez", AnswerNormalizer.normalize("  FirulÁi   López "))
        assertEquals(AnswerNormalizer.normalize("Firulais"), AnswerNormalizer.normalize("firulaís"))
    }

    @Test
    fun a4_la_enie_se_conserva() {
        assertEquals("muñeca", AnswerNormalizer.normalize("Muñeca"))
    }
}

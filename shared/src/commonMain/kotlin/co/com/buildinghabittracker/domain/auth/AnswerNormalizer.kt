package co.com.buildinghabittracker.domain.auth

/**
 * A4: la respuesta de seguridad se compara sin distinguir mayúsculas, acentos ni espacios extra.
 *
 * Por eso, ANTES de calcular el hash, la respuesta se "normaliza":
 * "  Firulái  López " → "firulai lopez"
 *
 * Se normaliza al registrar y al recuperar; así las dos versiones producen el mismo hash.
 * La ñ se conserva porque es otra letra, no una vocal con acento.
 */
object AnswerNormalizer {
    private val accents = mapOf(
        'á' to 'a', 'à' to 'a', 'ä' to 'a', 'â' to 'a',
        'é' to 'e', 'è' to 'e', 'ë' to 'e', 'ê' to 'e',
        'í' to 'i', 'ì' to 'i', 'ï' to 'i', 'î' to 'i',
        'ó' to 'o', 'ò' to 'o', 'ö' to 'o', 'ô' to 'o',
        'ú' to 'u', 'ù' to 'u', 'ü' to 'u', 'û' to 'u',
    )
    private val spaces = Regex("\\s+")

    fun normalize(answer: String): String =
        answer.lowercase()
            .map { accents[it] ?: it }
            .joinToString("")
            .trim()
            .replace(spaces, " ")
}

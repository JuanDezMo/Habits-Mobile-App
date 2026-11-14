# Capa domain (negocio)

Kotlin puro: no conoce Room, Compose, Android ni iOS. Las demás capas dependen de esta.

Aquí van (desde la iteración 1):
- **Modelos**: `User`, `Habit`, `CheckIn` (`data class` sin anotaciones).
- **Interfaces**: `UserRepository`, `PinHasher`, `ReminderScheduler`, `SettingsRepository`. Dicen *qué* se necesita, no *cómo*.
- **Reglas**: funciones puras como `PinRules`, `HabitValidator`, `StreakCalculator`. Son lo más fácil de probar.
- **UseCases**: llegan en la iteración 6.

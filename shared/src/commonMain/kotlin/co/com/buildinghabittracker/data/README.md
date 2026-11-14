# Capa data (datos)

Implementa las interfaces de `domain` usando herramientas concretas.

Aquí van (desde la iteración 1):
- **Room**: `AppDatabase`, entidades (`UserEntity`…), DAOs y TypeConverters.
- **Repositorios**: `UserRepositoryImpl`… convierten Entity ↔ modelo con mappers.
- **Servicios de plataforma**: `PinHasher` (PBKDF2), notificaciones (Android/iOS), DataStore.

Room ya está configurado en `shared/build.gradle.kts`. La clase `AppDatabase` se crea en la
iteración 1 junto con la primera tabla (`user`), porque Room exige al menos una tabla.

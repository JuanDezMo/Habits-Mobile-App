# Habit Tracker — Planeación App Expo

Nov 2, 2026 · @Natalia

## Cómo usar este plan

Este plan es **preliminar**: la arquitectura de la app todavía no está definida, así que su primera tarea es diseñarla y aprobarla. Las tareas siguientes se ajustarán a esa arquitectura.

- **Pruebas:** Jest con el preset `jest-expo` y React Native Testing Library para componentes y hooks; Jest con MSW para simular la API en flujos de pantallas.
- **Regla "no any":** TypeScript strict y ESLint, igual que en el backend.
- **Git:** lo manejas tú.

## Resumen

21 tareas en 6 iteraciones.

| # | Iteración | Entregable para revisión | Tareas | Estado |
| --- | --- | --- | --- | --- |
| 0 | Arquitectura y base | Arquitectura aprobada y proyecto Expo que arranca con sus pruebas | 5 | Pendiente |
| 1 | Autenticación | Registro, login y sesión persistente | 4 | Pendiente |
| 2 | Hoy | Lista del día y check-in | 3 | Pendiente |
| 3 | Hábitos | Crear, editar y archivar | 3 | Pendiente |
| 4 | Detalle y estadísticas | Rachas, cumplimiento y calendario | 2 | Pendiente |
| 5 | Recordatorios | Configurar la hora y registrar el push token | 4 | Pendiente |

## Iteración 0: Arquitectura y base

- [ ] **0.1** Documento de arquitectura de la app: navegación, estado, cliente de API y guardado de tokens. *Hecho cuando:* está aprobado por ti.
- [ ] **0.2** Proyecto Expo con TypeScript strict y ESLint sin `any`. *Hecho cuando:* la app abre en Expo Go y `pnpm lint` pasa.
- [ ] **0.3** Jest con `jest-expo`, React Native Testing Library y MSW. *Hecho cuando:* una primera prueba está en verde.
- [ ] **0.4** Navegación base con las pantallas vacías. *Hecho cuando:* se puede navegar entre ellas.
- [ ] **0.5** Cliente de API tipado, con la URL base por entorno. *Hecho cuando:* consulta `GET /health` del backend y hay prueba con MSW.

## Iteración 1: Autenticación

- [ ] **1.1** Pantalla de registro con validación y manejo de errores (A1, A2, A6, A8). *Hecho cuando:* hay pruebas de la pantalla.
- [ ] **1.2** Pantalla de login con manejo de INVALID\_CREDENTIALS y TOO\_MANY\_ATTEMPTS. *Hecho cuando:* hay pruebas de la pantalla.
- [ ] **1.3** Guardado seguro de los tokens y sesión persistente al reabrir la app. *Hecho cuando:* hay prueba del flujo.
- [ ] **1.4** Renovación automática con refresh y cierre de sesión. *Hecho cuando:* una prueba con MSW simula el token vencido.

## Iteración 2: Hoy

- [ ] **2.1** Pantalla Hoy con los hábitos del día y sus rachas (H1–H3). *Hecho cuando:* hay pruebas con MSW.
- [ ] **2.2** Check-in y desmarcado; un 409 ALREADY\_CHECKED\_IN se trata como éxito (D11). *Hecho cuando:* hay pruebas, incluido el 409.
- [ ] **2.3** Estados de carga, vacío y error. *Hecho cuando:* hay pruebas de cada estado.

## Iteración 3: Hábitos

- [ ] **3.1** Lista de hábitos activos y archivados. *Hecho cuando:* hay pruebas con MSW.
- [ ] **3.2** Crear y editar con selector de emoji, color y días (C1, C4, C7). *Hecho cuando:* hay pruebas del formulario.
- [ ] **3.3** Archivar y desarchivar. *Hecho cuando:* hay pruebas.

## Iteración 4: Detalle y estadísticas

- [ ] **4.1** Racha actual, mejor racha y cumplimiento (E1–E8). *Hecho cuando:* hay pruebas con MSW.
- [ ] **4.2** Calendario con los días marcados. *Hecho cuando:* hay pruebas del componente.

## Iteración 5: Recordatorios

- [ ] **5.1** *Development build* de la app (necesario para las notificaciones push). *Hecho cuando:* la app corre en un dispositivo con el build.
- [ ] **5.2** Permiso de notificaciones y registro del push token en cada apertura (supuesto de R10). *Hecho cuando:* el token llega a reminder-service.
- [ ] **5.3** Configurar y eliminar el recordatorio de un hábito (R2, R4). *Hecho cuando:* hay pruebas con MSW.
- [ ] **5.4** Quitar el push token al cerrar sesión. *Hecho cuando:* hay prueba del flujo.

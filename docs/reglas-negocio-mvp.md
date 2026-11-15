# Habit Tracker — Reglas de negocio MVP

Nov 14, 2026 · App móvil Compose Multiplatform (Android + iOS), UI compartida, persistencia local, MVVM por capas.

## Alcance

| Módulo | Incluye |
| --- | --- |
| Registro | Nombre, email, PIN de 4 dígitos, pregunta de seguridad libre |
| Login | PIN; recuperación con pregunta de seguridad |
| Hoy | Hábitos del día, check-in y desmarcar |
| Hábitos | Crear, editar, archivar, desarchivar |
| Estadísticas | Racha actual, mejor racha, % cumplimiento, calendario mensual |
| Recordatorio | 1 notificación local opcional por hábito |
| General | Español e inglés, modo claro y oscuro |
| Ajustes | Elegir idioma y tema, cambiar PIN |

## G. Generales

- **G1.** La app está en español e inglés. La primera vez abre en el idioma del teléfono (inglés si no es español ni inglés). Todos los textos se traducen, incluidos el aviso (R5) y los mensajes de error; el título del hábito no, porque lo escribe el usuario.
- **G2.** La app tiene modo claro y modo oscuro. Por defecto sigue la configuración del teléfono.
- **G3.** Identificador de la app: `co.com.buildinghabittracker` (Android applicationId e iOS bundle ID).
- **G4.** En Ajustes el usuario elige idioma (Español / English) y tema (Igual que el teléfono / Claro / Oscuro). El cambio se aplica al instante y los avisos programados se reprograman en el nuevo idioma. La elección se guarda en el dispositivo.

## A. Registro y acceso

- **A1.** Un solo usuario por dispositivo; el registro se hace una vez, en 4 pasos (datos, crear PIN, confirmar PIN, pregunta de seguridad). Se puede volver al paso anterior conservando lo escrito; al volver a "Crear PIN" el PIN se borra. Nada se guarda hasta terminar el paso 4: si se cierra la app a mitad, el registro empieza de nuevo. Al terminar, el usuario va a la pantalla de PIN.
- **A2.** Nombre obligatorio, de 1 a 30 caracteres. Email obligatorio, con formato válido y máximo 254 caracteres (límite estándar); no se verifica.
- **A3.** PIN de exactamente 4 dígitos, confirmado dos veces, guardado con hash PBKDF2-SHA256 (210.000 iteraciones, sal aleatoria de 16 bytes); nunca en texto plano.
- **A4.** Pregunta de seguridad libre: pregunta de 5 a 100 caracteres y respuesta de 3 a 50. La respuesta se guarda con hash y se compara sin distinguir mayúsculas, acentos ni espacios extra.
- **A5.** El PIN se pide al abrir la app y al volver tras 5 min o más en segundo plano.
- **A6.** 5 intentos fallidos → bloqueo de 30 s, que se duplica con cada nuevo fallo hasta un tope de 15 minutos. Aplica al PIN y a la pregunta de seguridad. El bloqueo se guarda: cerrar la app no lo salta.
- **A7.** Olvido de PIN: se responde la pregunta de seguridad; si es correcta, el contador de fallos vuelve a 0, se crea un PIN nuevo (dos veces) sin perder datos y el usuario va a Hoy.
- **A8.** En Ajustes se puede cambiar el PIN: se escribe el PIN actual y luego el nuevo dos veces. Los fallos del PIN actual cuentan para el bloqueo de A6.

## C. Hábitos

- **C1.** Título obligatorio, de 1 a 40 caracteres, sin duplicados entre los hábitos activos.
- **C2.** Descripción opcional (máx. 200). Emoji y color con valor por defecto.
- **C3.** Días de la semana obligatorios (mínimo 1); atajo "todos los días".
- **C4.** Tipo único: hecho / no hecho (1 check por día).
- **C5.** No hay eliminación, solo archivar y desarchivar. Archivar oculta el hábito de Hoy, cancela su recordatorio y conserva el historial.

## D. Check-in

- **D1.** Solo se puede marcar hoy o ayer, y solo en días programados.
- **D2.** Máximo 1 check-in por hábito por día; marcar dos veces no hace nada.
- **D3.** Se puede desmarcar en las mismas fechas permitidas.

## E. Estadísticas

- **E1.** La racha actual cuenta los días programados consecutivos con check-in; los días no programados se ignoran.
- **E2.** Si hoy aún no se marca, la racha se mantiene hasta que termine el día.
- **E3.** Mejor racha = máximo histórico.
- **E4.** Cumplimiento = check-ins ÷ días programados, desde la creación del hábito hasta hoy.
- **E5.** Calendario mensual en el detalle: días con check-in ☑, programados sin check-in ✗, no programados –. Navegación entre meses.
- **E6.** Los días en que el hábito estuvo archivado cuentan como días programados sin check-in: penalizan el cumplimiento, rompen la racha y se ven como ✗ en el calendario.
- **E7.** Las fechas usan la zona horaria local del dispositivo.

## R. Recordatorios (notificación local)

- **R1.** 1 recordatorio opcional por hábito, a una hora; solo suena en los días programados.
- **R2.** No suena si el hábito ya está marcado ese día: al marcar se cancela la notificación de hoy.
- **R3.** Al desmarcar se vuelve a programar la notificación de hoy, si la hora aún no pasó.
- **R4.** Las notificaciones se programan una por una para los próximos 7 días y se reprograman cada vez que se abre la app. Si el usuario no abre la app en más de 7 días, dejan de sonar (aceptado para el MVP). Riesgo aceptado: iOS guarda máximo 64 avisos pendientes; con más de 9 hábitos diarios con recordatorio, algunos avisos lejanos pueden no programarse.
- **R5.** Texto de la notificación: "{emoji} ¿Ya hiciste {título} hoy?"
- **R6.** El permiso de notificaciones se pide al activar el primer recordatorio. Si se niega, la app sigue funcionando y muestra un aviso para activarlo en Ajustes. En Android 12+ se pide justo después el permiso "Alarmas y recordatorios" (pantalla que lleva a Ajustes); si no se activa, se usa alarma aproximada (puede llegar unos minutos tarde) y se muestra cómo activarlo.
- **R7.** Al editar un hábito, su recordatorio se reprograma; al archivarlo, se cancela.
- **R8.** Al tocar la notificación se abre Hoy, después de pedir el PIN.

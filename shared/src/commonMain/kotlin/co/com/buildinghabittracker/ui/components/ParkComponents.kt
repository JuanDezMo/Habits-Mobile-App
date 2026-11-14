package co.com.buildinghabittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.com.buildinghabittracker.resources.Res
import co.com.buildinghabittracker.resources.common_back
import co.com.buildinghabittracker.ui.theme.ParkTheme
import org.jetbrains.compose.resources.stringResource

/*
 * Componentes base del sistema "Mapa de parque". Todos son planos (sin sombras ni degradados)
 * y todos reciben su `testTag` desde afuera, para que Maestro los encuentre.
 */

/**
 * El lóbulo: un área de color plano arriba de la pantalla cuya "costa" ondulada la separa
 * del fondo. Lleva el titular de cada pantalla. Se dibuja detrás de [content] y llega hasta
 * arriba del todo (debajo de la barra de estado); el contenido sí respeta la barra.
 *
 * @param lake si no es `null`, se abre un "lago" (un óvalo del color del fondo) dentro del
 * lóbulo. En Hoy representa lo que falta del día: su tamaño es esa fracción (0 a 1).
 */
@Composable
fun Lobe(
    color: Color,
    modifier: Modifier = Modifier,
    lake: Float? = null,
    coastHeight: Dp = 56.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val water = ParkTheme.colors.water
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val coast = coastHeight.toPx()
                val w = size.width
                val h = size.height
                val shore = h - coast
                // La costa: rectángulo hasta `shore` y un borde inferior ondulado con curvas.
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w, shore - coast * 0.25f)
                    cubicTo(w * 0.86f, shore + coast * 0.15f, w * 0.74f, shore - coast * 0.35f, w * 0.6f, shore + coast * 0.35f)
                    cubicTo(w * 0.46f, h + coast * 0.05f, w * 0.28f, h - coast * 0.05f, w * 0.18f, shore + coast * 0.55f)
                    cubicTo(w * 0.1f, shore + coast * 0.3f, w * 0.05f, shore + coast * 0.6f, 0f, shore + coast * 0.45f)
                    close()
                }
                drawPath(path, color)
                if (lake != null && lake > 0f) {
                    // El lago: crece con lo que falta, pegado al borde derecho.
                    val lakeW = w * (0.12f + 0.22f * lake.coerceIn(0f, 1f))
                    val lakeH = lakeW * 0.62f
                    drawOval(
                        color = water,
                        topLeft = Offset(w - lakeW * 0.8f, shore * 0.52f - lakeH / 2),
                        size = Size(lakeW, lakeH),
                    )
                }
            }
            .statusBarsPadding()
            .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = coastHeight + 8.dp),
        content = content,
    )
}

/** El chip redondo que llevan las cápsulas al final (o al inicio, cuando están "hechas"). */
@Composable
fun RoundChip(
    background: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier.size(size).background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/**
 * Botón principal: cápsula de tinta con el texto al inicio y un chip amarillo con flecha
 * al final. Uno por pantalla.
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward,
    containerColor: Color = ParkTheme.colors.ink,
    contentColor: Color = ParkTheme.colors.water,
    chipColor: Color = ParkTheme.colors.notice,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .clip(CircleShape)
            .background(containerColor.copy(alpha = if (enabled) 1f else 0.45f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(start = 24.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, color = contentColor, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
        RoundChip(chipColor.copy(alpha = if (enabled) 1f else 0.45f)) {
            Icon(icon, contentDescription = null, tint = ParkTheme.colors.onAccent)
        }
    }
}

/** Botón secundario: cápsula con texto centrado (blanco por defecto). */
@Composable
fun PillTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ParkTheme.colors.field,
    contentColor: Color = ParkTheme.colors.ink,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(containerColor.copy(alpha = containerColor.alpha * if (enabled) 1f else 0.45f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = contentColor.copy(alpha = if (enabled) 1f else 0.6f), style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
    }
}

/** "← Volver" en una cápsula pequeña. */
@Composable
fun BackPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ParkTheme.colors.field,
    contentColor: Color = ParkTheme.colors.ink,
) {
    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 12.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Text(stringResource(Res.string.common_back), style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}

/** Aviso en cápsula amarilla: errores suaves, intentos restantes, "Te falta…". */
@Composable
fun NoticePill(text: String, modifier: Modifier = Modifier, color: Color = ParkTheme.colors.notice) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = ParkTheme.colors.onAccent,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(color)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

/** Una opción de [PillTabs]: su texto y su identificador de prueba. */
data class PillTab(val label: String, val testTag: String)

/** Pestañas en cápsula: la elegida en tinta, las demás sobre blanco. */
@Composable
fun PillTabs(
    tabs: List<PillTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(ParkTheme.colors.field)
            .padding(5.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(CircleShape)
                    .background(if (selected) ParkTheme.colors.ink else Color.Transparent)
                    .selectable(selected = selected, role = Role.Tab) { onSelect(index) }
                    .testTag(tab.testTag),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) ParkTheme.colors.water else ParkTheme.colors.ink,
                )
            }
        }
    }
}

package co.com.buildinghabittracker.data.settings

import android.content.res.Resources
import co.com.buildinghabittracker.domain.settings.DeviceLanguage

/**
 * Android: el idioma del SISTEMA. Se lee de `Resources.getSystem()` y no del de la app,
 * porque la app puede tener otro idioma elegido en Ajustes.
 */
val androidDeviceLanguage = DeviceLanguage {
    Resources.getSystem().configuration.locales[0].language
}

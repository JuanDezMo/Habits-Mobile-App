package co.com.buildinghabittracker.data.settings

import co.com.buildinghabittracker.domain.settings.DeviceLanguage
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

/** iOS: el primer idioma preferido del teléfono, por ejemplo "es-CO". */
val iosDeviceLanguage = DeviceLanguage {
    (NSLocale.preferredLanguages.firstOrNull() as? String) ?: "en"
}

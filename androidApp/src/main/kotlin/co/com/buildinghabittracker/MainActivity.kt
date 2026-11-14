package co.com.buildinghabittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * La única Activity de la app. No tiene lógica: solo muestra [HabitApp], que vive en el
 * módulo compartido y es la misma UI que ve iOS.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitApp()
        }
    }
}

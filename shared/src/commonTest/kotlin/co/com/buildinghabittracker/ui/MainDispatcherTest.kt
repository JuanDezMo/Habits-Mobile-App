package co.com.buildinghabittracker.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base para probar ViewModels.
 *
 * Los ViewModels lanzan corrutinas en `Dispatchers.Main`, que en una prueba no existe.
 * Aquí lo reemplazamos por un dispatcher de pruebas que ejecuta todo al instante.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class MainDispatcherTest {
    @BeforeTest
    fun setUpMainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDownMainDispatcher() = Dispatchers.resetMain()
}

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.window.application
import ui.MainWindow
import ui.rememberMainWindowState

fun main() = application {
    MaterialTheme(
        colors = darkColors()
    ) {
        MainWindow(rememberMainWindowState(::exitApplication))
    }
}
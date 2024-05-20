
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.window.application
import ui.MainWindow
import ui.rememberMainWindowState

fun main() = application {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        MainWindow(rememberMainWindowState(::exitApplication))
    }
}
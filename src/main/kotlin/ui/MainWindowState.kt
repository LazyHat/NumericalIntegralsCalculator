package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowState
import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MainWindowState(
    val exit: () -> Unit,
) : ViewModel() {
    val window = WindowState()
    val method = MutableStateFlow<Method>(Method.LeftRectangle)
    val functionInputText = MutableStateFlow("x")
    val subIntegralsString = MutableStateFlow("10000000")
    private val subIntegralsInt = subIntegralsString.map { str ->
        str.toIntOrNull()?.takeIf { it in 1..99999999 }
    }
    val subIntegralsError = subIntegralsInt.map { it == null }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val fromString = MutableStateFlow("0")
    val toString = MutableStateFlow("1")
    private val fromDouble = fromString.map { str ->
        str.toDoubleOrNull()
    }

    private val toDouble = toString.map { str ->
        str.toDoubleOrNull()
    }

    val fromError = fromDouble.map { it == null }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val toError = toDouble.map { it == null }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val functionError = MutableStateFlow<String?>(null)
    val integralOutput = combine(
        functionInputText, subIntegralsInt, fromDouble, toDouble, method
    ) { str, subIntegrals, from, to, method ->
        withContext(Dispatchers.Default) {
            try {
                if (str.isEmpty()) error("Function is empty")
                if (subIntegrals == null) error("Count of subIntegrals invalid")
                if (from == null) error("\"From\" field invalid")
                if (to == null) error("\"To\" field invalid")
                val parser = FunctionParser(str)
                method.calculate(from, to, subIntegrals) {
                    functionError.value = null
                    parser.calculate(persistentMapOf('x' to it))
                }
            } catch (e: Exception) {
                functionError.value = e.message ?: "unknown error"
                Double.NaN
            }.toString()
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )
}

@Composable
fun rememberMainWindowState(exit: () -> Unit) =
    remember { MainWindowState(exit = exit) }
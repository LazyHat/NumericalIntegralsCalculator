package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.WindowState
import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlin.math.pow

sealed interface Token {
    val symbol: String

    sealed interface Divider : Token {
        data object Open : Divider {
            override val symbol: String = "("
        }

        data object Close : Divider {
            override val symbol: String = ")"
        }
    }

    data class Num(val v: Double) : Token {
        override val symbol: String = v.toString()
    }

    sealed interface Operator : Token {
        data object Add : Operator {
            override val symbol: String = "+"
        }

        data object Sub : Operator {
            override val symbol: String = "-"
        }

        data object Mul : Operator {
            override val symbol: String = "*"
        }

        data object Div : Operator {
            override val symbol: String = "/"
        }

        data object Minus : Operator {
            override val symbol: String = "-"
        }

        data object Pow : Operator {
            override val symbol: String = "^"
        }
    }

    data object X : Token {
        override val symbol: String = "x"
    }
}

class FunctionParser(input: String) {
    private val _tokens = parse(input)

    fun calculateX(x: Double): Double? = try {
        calculateXRecursive(_tokens.map { if (it is Token.X) Token.Num(x) else it })
    } catch (e: IllegalArgumentException) {
        null
    }

    private fun calculateXRecursive(tokens: List<Token>): Double {
        if (tokens.any { it is Token.Divider }) {
            val close = tokens.indexOf(Token.Divider.Close)
            val open = tokens.subList(0, close + 1).indexOfLast { it is Token.Divider.Open }
            return if (close != -1 && open != -1) calculateXRecursive(
                tokens.subList(0, open) + Token.Num(calculateXRecursive(tokens.subList(open + 1, close))) + tokens.subList(
                    close + 1, tokens.size
                )
            ) else throw IllegalArgumentException()
        } else if (tokens.any { it is Token.Operator.Minus }) {
            val newTokens = mutableListOf<Token>()
            tokens.forEachIndexed { index, it ->
                if (index != 0 && it is Token.Num && tokens[index - 1] is Token.Operator.Minus) newTokens += Token.Num(-it.v)
                else if (!(it is Token.Operator.Minus && tokens.getOrNull(index + 1) is Token.Num)) newTokens += it
                if (it is Token.Operator.Minus && tokens.getOrNull(index + 1) == null) return 0.0
            }
            return calculateXRecursive(newTokens)
        } else if (tokens.contains(Token.Operator.Pow)) {
            val index = tokens.indexOfLast { it is Token.Operator.Pow }
            if (index != 0 && index != tokens.size - 1 && tokens[index - 1] is Token.Num && tokens[index + 1] is Token.Num) {
                val a = tokens[index - 1] as Token.Num
                val b = tokens[index + 1] as Token.Num
                return calculateXRecursive(
                    tokens.subList(
                        0, index - 1
                    ) + Token.Num(a.v.pow(b.v)) + tokens.subList(index + 2, tokens.size)
                )
            }
        } else if (tokens.find { it is Token.Operator.Mul || it is Token.Operator.Div } != null) {
            val token = tokens.first { it is Token.Operator.Mul || it is Token.Operator.Div }
            if (token is Token.Operator.Mul) {
                val index = tokens.indexOf(Token.Operator.Mul)
                if (index != 0 && index != tokens.size - 1 && tokens[index - 1] is Token.Num && tokens[index + 1] is Token.Num) {
                    val a = tokens[index - 1] as Token.Num
                    val b = tokens[index + 1] as Token.Num
                    return calculateXRecursive(
                        tokens.subList(
                            0, index - 1
                        ) + Token.Num(a.v * b.v) + tokens.subList(index + 2, tokens.size)
                    )
                }

            } else if (token is Token.Operator.Div) {
                val index = tokens.indexOf(Token.Operator.Div)
                if (index != 0 && index != tokens.size - 1 && tokens[index - 1] is Token.Num && tokens[index + 1] is Token.Num) {
                    val a = tokens[index - 1] as Token.Num
                    val b = tokens[index + 1] as Token.Num
                    return calculateXRecursive(
                        tokens.subList(
                            0, index - 1
                        ) + Token.Num(a.v / b.v) + tokens.subList(index + 2, tokens.size)
                    )
                }
            }
        } else if (tokens.find { it is Token.Operator.Add || it is Token.Operator.Sub } != null) {
            val token = tokens.first { it is Token.Operator.Add || it is Token.Operator.Sub }
            if (token is Token.Operator.Add) {
                val index = tokens.indexOf(Token.Operator.Add)
                if (index != 0 && index != tokens.size - 1 && tokens[index - 1] is Token.Num && tokens[index + 1] is Token.Num) {
                    val a = tokens[index - 1] as Token.Num
                    val b = tokens[index + 1] as Token.Num
                    return calculateXRecursive(
                        tokens.subList(
                            0, index - 1
                        ) + Token.Num(a.v + b.v) + tokens.subList(index + 2, tokens.size)
                    )
                }
            } else if (token is Token.Operator.Sub) {
                val index = tokens.indexOf(Token.Operator.Sub)
                if (index != 0 && index != tokens.size - 1 && tokens[index - 1] is Token.Num && tokens[index + 1] is Token.Num) {
                    val a = tokens[index - 1] as Token.Num
                    val b = tokens[index + 1] as Token.Num
                    return calculateXRecursive(
                        tokens.subList(
                            0, index - 1
                        ) + Token.Num(a.v - b.v) + tokens.subList(index + 2, tokens.size)
                    )
                }
            }
        } else if (tokens.size == 1 && tokens[0] is Token.Num) return (tokens[0] as Token.Num).v
        throw IllegalArgumentException()
    }

    fun calculateString(): String = _tokens.toString()//.fastJoinToString(" ") { it.symbol }

    private fun parse(input: String): List<Token> {
        val tokens: MutableList<Token> = mutableListOf()
        var digits: String? = null
        for (c in input) {
            if (c == ' ') continue
            else if (c.isDigit()) {
                digits = digits?.plus(c) ?: c.toString()
            } else {
                if (digits != null) {
                    tokens += Token.Num(digits.toDouble())
                    digits = null
                }
                when (c) {
                    'x' -> tokens += Token.X
                    '(' -> tokens += Token.Divider.Open
                    ')' -> tokens += Token.Divider.Close
                    '+' -> tokens += Token.Operator.Add
                    '*' -> tokens += Token.Operator.Mul
                    '/' -> tokens += Token.Operator.Div
                    '^' -> tokens += Token.Operator.Pow
                    '-' -> {
                        tokens += if (tokens.lastOrNull() is Token.Num || tokens.lastOrNull() is Token.X) Token.Operator.Sub
                        else Token.Operator.Minus
                    }
                }
            }
        }
        if (digits != null) {
            tokens += Token.Num(digits.toDouble())
            digits = null
        }
        return tokens
    }
}

sealed interface Method {
    val name: String
    fun calculate(start: Double, end: Double, countSub: Int, f: (Double) -> Double): Double

    data object LeftRectangle : Method {
        override val name = "Left Rectangle"
        override fun calculate(start: Double, end: Double, countSub: Int, f: (Double) -> Double): Double = 0.until(countSub).fold(0.0) { acc, i ->
            fun x(index: Int): Double = ((end - start) / countSub.toDouble()) * index
            acc + f(x(i)) * (x(i + 1) - x(i))
        }
    }
}

class MainWindowState(
    val exit: () -> Unit,
) : ViewModel() {
    val window = WindowState()
    val method = MutableStateFlow<Method>(Method.LeftRectangle)
    val functionInputText = MutableStateFlow("")
    val subIntegralsInputText = MutableStateFlow("")
    val subIntegralsInt = subIntegralsInputText.map { it.toIntOrNull() }
    val subIntegralsInputTextError = subIntegralsInt.map { it == null }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val functionParser = functionInputText.map { FunctionParser(it) }
    val functionInputTextError = functionParser.map { it.calculateX(5.0) == null }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val functionOutputText = functionParser.map { it.calculateString() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val functionXOutputText = functionParser.map { it.calculateX(2.0).toString() }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val integralOutput = combine(functionParser, subIntegralsInt, method) { fp, int, method ->
        method.calculate(0.0, 1.0, int ?: 1) { fp.calculateX(it) ?: 0.0 }.toString()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )
}

@Composable
fun rememberMainWindowState(exit: () -> Unit) = remember { MainWindowState(exit = exit) }
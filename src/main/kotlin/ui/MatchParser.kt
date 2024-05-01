package ui

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 *
 * @author shurik
 */

class MatchParser {
    private val variables = HashMap<String, Double>()

    fun setVariable(variableName: String, variableValue: Double) {
        variables[variableName] = variableValue
    }

    fun getVariable(variableName: String): Double? {
        if (!variables.containsKey(variableName)) {
            System.err.println("Error: Try get unexists variable '$variableName'")
            return 0.0
        }
        return variables[variableName]
    }

    @Throws(Exception::class)
    fun Parse(s: String): Double {
        val result = PlusMinus(s)
        if (result.rest.isNotEmpty()) {
            System.err.println("Error: can't full parse")
            System.err.println("rest: " + result.rest)
        }
        return result.acc
    }

    @Throws(Exception::class)
    private fun PlusMinus(s: String): Result {
        var current = MulDiv(s)
        var acc = current.acc

        while (current.rest.isNotEmpty()) {
            if (!(current.rest[0] == '+' || current.rest[0] == '-')) break

            val sign = current.rest[0]
            val next = current.rest.substring(1)

            current = MulDiv(next)
            if (sign == '+') {
                acc += current.acc
            } else {
                acc -= current.acc
            }
        }
        return Result(acc, current.rest)
    }

    @Throws(Exception::class)
    private fun Bracket(s: String): Result {
        val zeroChar = s[0]
        if (zeroChar == '(') {
            val r = PlusMinus(s.substring(1))
            if (r.rest.isNotEmpty() && r.rest[0] == ')') {
                r.rest = r.rest.substring(1)
            } else {
                System.err.println("Error: not close bracket")
            }
            return r
        }
        return FunctionVariable(s)
    }

    @Throws(Exception::class)
    private fun FunctionVariable(s: String): Result {
        var f = ""
        var i = 0
        // ищем название функции или переменной
        // имя обязательно должна начинаться с буквы
        while (i < s.length && (Character.isLetter(s[i]) || (Character.isDigit(s[i]) && i > 0))) {
            f += s[i]
            i++
        }
        if (f.isNotEmpty()) { // если что-нибудь нашли
            if (s.length > i && s[i] == '(') { // и следующий символ скобка значит - это функция
                val r = Bracket(s.substring(f.length))
                return processFunction(f, r)
            } else { // иначе - это переменная
                return Result(getVariable(f)!!, s.substring(f.length))
            }
        }
        return Num(s)
    }

    @Throws(Exception::class)
    private fun MulDiv(s: String): Result {
        var current = Bracket(s)

        var acc = current.acc
        while (true) {
            if (current.rest.isEmpty()) {
                return current
            }
            val sign = current.rest[0]
            if ((sign != '*' && sign != '/')) return current

            val next = current.rest.substring(1)
            val right = Bracket(next)

            if (sign == '*') {
                acc *= right.acc
            } else {
                acc /= right.acc
            }

            current = Result(acc, right.rest)
        }
    }

    @Throws(Exception::class)
    private fun Num(s: String): Result {
        var s = s
        var i = 0
        var dot_cnt = 0
        var negative = false
        // число также может начинаться с минуса
        if (s[0] == '-') {
            negative = true
            s = s.substring(1)
        }
        // разрешаем только цифры и точку
        while (i < s.length && (Character.isDigit(s[i]) || s[i] == '.')) {
            // но также проверям, что в числе может быть только одна точка!
            if (s[i] == '.' && ++dot_cnt > 1) {
                throw Exception(
                    "not valid number '" + s.substring(
                        0, i + 1
                    ) + "'"
                )
            }
            i++
        }
        if (i == 0) { // что-либо похожее на число мы не нашли
            throw Exception("can't get valid number in '$s'")
        }

        var dPart = s.substring(0, i).toDouble()
        if (negative) dPart = -dPart
        val restPart = s.substring(i)

        return Result(dPart, restPart)
    }

    // Тут определяем все нашие функции, которыми мы можем пользоватся в формулах
    private fun processFunction(func: String, r: Result): Result {
        if (func == "sin") {
            return Result(sin(Math.toRadians(r.acc)), r.rest)
        } else if (func == "cos") {
            return Result(cos(Math.toRadians(r.acc)), r.rest)
        } else if (func == "tan") {
            return Result(tan(Math.toRadians(r.acc)), r.rest)
        } else {
            System.err.println("function '$func' is not defined")
        }
        return r
    }
}
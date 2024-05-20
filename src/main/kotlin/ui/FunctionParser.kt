package ui

import androidx.compose.ui.util.fastForEachReversed
import kotlinx.collections.immutable.PersistentMap
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

sealed interface Token {
    data class Inverse(val value: Token) : Token
    data class Add(val a: Token, val b: Token) : Token
    data class Sub(val a: Token, val b: Token) : Token
    data class Mul(val a: Token, val b: Token) : Token
    data class Div(val a: Token, val b: Token) : Token
    data class Pow(val base: Token, val degree: Token) : Token
    data class Sqrt(val value: Token) : Token
    data class Sin(val value: Token) : Token
    data class Cos(val value: Token) : Token
    data class Number(val value: Double) : Token
    data class Variable(val letter: Char) : Token
}

fun Token.calculate(variables: PersistentMap<Char, Double>): Double =
    when (this) {
        is Token.Inverse -> -value.calculate(variables)
        is Token.Add -> a.calculate(variables) + b.calculate(variables)
        is Token.Sub -> a.calculate(variables) - b.calculate(variables)
        is Token.Mul -> a.calculate(variables) * b.calculate(variables)
        is Token.Div -> a.calculate(variables) / b.calculate(variables)
        is Token.Pow -> base.calculate(variables)
            .pow(degree.calculate(variables))

        is Token.Sqrt -> value.calculate(variables)
        is Token.Cos -> cos(value.calculate(variables))
        is Token.Sin -> sin(value.calculate(variables))
        is Token.Number -> value
        is Token.Variable -> variables[letter]
            ?: error("Variable does not exists")
    }

data class FunctionParser(
    val s: String,
) {
    private val result = parse(s)

    fun calculate(variables: PersistentMap<Char, Double>): Double =
        result.calculate(variables)

    private fun parse(s: String): Token {
        val result = parseAddSub(s.filterNot { it.isWhitespace() })
        return result.token
    }

    private fun parseAddSub(s: String): ParseResult {
        var current = parseMulDiv(s)
        var token = current.token

        while (current.remainingPart.isNotEmpty()) {
            val sign = current.remainingPart[0]
            if (!(sign == '+' || sign == '-')) break

            val next = current.remainingPart.substring(1)

            current = parseMulDiv(next)
            when (sign) {
                '+' -> token = Token.Add(token, current.token)
                '-' -> token = Token.Sub(token, current.token)
            }
        }
        return ParseResult(token, current.remainingPart)
    }

    private fun parseMulDiv(s: String): ParseResult {
        var current = parsePower(s)
        var token = current.token

        while (current.remainingPart.isNotEmpty()) {
            val sign = current.remainingPart[0]
            if (!(sign == '*' || sign == '/')) break

            val next = current.remainingPart.substring(1)

            current = parsePower(next)
            when (sign) {
                '*' -> token = Token.Mul(token, current.token)
                '/' -> token = Token.Div(token, current.token)
            }
        }
        return ParseResult(token, current.remainingPart)
    }

    private fun parsePower(s: String): ParseResult {
        var current = parseBracket(s)
        val tokens = mutableListOf(current.token)

        while (current.remainingPart.isNotEmpty()) {
            if (current.remainingPart[0] != '^') break

            current = parseBracket(current.remainingPart.substring(1))
            tokens += current.token
        }

        return if (tokens.size > 1) {
            var temp = tokens.last()
            tokens.dropLast(1).fastForEachReversed {
                temp = Token.Pow(it, temp)
            }
            ParseResult(temp, current.remainingPart)
        } else current
    }

    private fun parseBracket(s: String): ParseResult {
        if (s[0] == '(') {
            var r = parseAddSub(s.substring(1))
            if (r.remainingPart.isNotEmpty() && r.remainingPart[0] == ')') {
                r = r.copy(remainingPart = r.remainingPart.substring(1))
            } else {
                error("didn't find close bracket")
            }
            return r
        }
        return parseOperand(s)
    }

    private fun parseOperand(s: String): ParseResult {
        var str = s
        var negative = false
        while (str[0] == '-') {
            str = str.substring(1)
            negative = !negative
        }
        return if (str[0].isDigit()) parseNumber(str) else {
            val operand = if (str.length == 1) str else str.substring(0,
                str.indexOfFirst { !it.isLetter() }
                    .let { if (it == -1) error("Invalid operand") else it })

            return (if (operand.length == 1) parseVariable(s)
            else if (operand.length > 1) {
                parseFunction(str)
            } else error("Invalid operand")).let {
                if (negative) it.copy(token = Token.Inverse(it.token))
                else it
            }
        }
    }

    private fun parseVariable(s: String): ParseResult =
        if (s[0].isDigit()) parseNumber(s) else {
            ParseResult(Token.Variable(s[0]), s.substring(1))
        }

    private fun parseFunction(
        s: String,
    ): ParseResult {
        val indexOfBracket = s.indexOfFirst { it == '(' }
        if (indexOfBracket == -1) error("Invalid operand")
        val function = s.substring(0, indexOfBracket)
        val childrenResult = parseBracket(s.substring(indexOfBracket))
        return childrenResult.let {
            when (function) {
                "sin" -> it.copy(token = Token.Sin(it.token))
                "cos" -> it.copy(token = Token.Cos(it.token))
                "sqrt" -> it.copy(token = Token.Sqrt(it.token))
                else -> error("Function not found")
            }
        }
    }

    private fun parseNumber(s: String): ParseResult {
        if (s[0].isDigit()) {
            val nonDigitCharIndex =
                s.substring(1).indexOfFirst { !it.isDigit() && it != '.' }.let {
                    if (it == -1) s.length
                    else it + 1
                }
            return ParseResult(
                Token.Number(s.substring(0, nonDigitCharIndex).toDouble()),
                s.substring(nonDigitCharIndex)
            )
        } else {
            error("Not valid number in $s")
        }
    }

    private data class ParseResult(val token: Token, val remainingPart: String)
}
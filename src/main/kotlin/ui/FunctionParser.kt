package ui

import kotlin.math.pow

data class _FunctionParser(
    val s: String,
    val variables: HashMap<Char, Double>,
) {
    val result = parse(s)
    private fun parse(s: String): Double {
        val result = parseAddSub(s.filterNot { it.isWhitespace() })
        if (result.remainingPart.isNotEmpty()) {
            error("Parsing error: can't parse ${result.remainingPart}")
        }
        return result.acc
    }

    private fun parseAddSub(s: String): ParseResult {
        var current = parseMulDiv(s)
        var acc = current.acc

        while (current.remainingPart.isNotEmpty()) {
            val sign = current.remainingPart[0]
            if (!(sign == '+' || sign == '-')) break

            val next = current.remainingPart.substring(1)

            current = parseMulDiv(next)
            when (sign) {
                '+' -> acc += current.acc
                '-' -> acc -= current.acc
            }
        }
        return ParseResult(acc, current.remainingPart)
    }

    private fun parseMulDiv(s: String): ParseResult {
        var current = parsePower(s)
        var acc = current.acc

        while (current.remainingPart.isNotEmpty()) {
            val sign = current.remainingPart[0]
            if (!(sign == '*' || sign == '/')) break

            val next = current.remainingPart.substring(1)

            current = parsePower(next)
            when (sign) {
                '*' -> acc *= current.acc
                '/' -> acc /= current.acc
            }
        }
        return ParseResult(acc, current.remainingPart)
    }

    private fun parsePower(s: String): ParseResult {
        val values = mutableListOf(parseBracket(s))

        while (values.last().remainingPart.isNotEmpty()) {
            if (values.last().remainingPart[0] != '^') break

            values += parseBracket(values.last().remainingPart.substring(1))
        }

        return ParseResult(values.foldRight(1.0) {
                it, acc,
            ->
            it.acc.pow(acc)
        }, values.last().remainingPart)
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
        return parseVariable(s)
    }

    private fun parseVariable(s: String): ParseResult {
        var result: ParseResult? = null
        val negative = s[0] == '-'
        var si = s
        if (negative) si = s.substring(1)
        result = if (si[0].isDigit()) parseNumber(s) else {
            val variable = variables[s[0]]
            if (variable != null) {
                ParseResult(variable, si.substring(1))
            } else error("Variable does not exists")
        }
        if (negative) result = result.copy(acc = -result.acc)
        return result
    }

    private fun parseNumber(s: String): ParseResult {
        if (s[0].isDigit()) {
            val nonDigitCharIndex =
                s.substring(1).indexOfFirst { !it.isDigit() && it != '.' }.let {
                    if (it == -1) s.length
                    else it + 1
                }
            return ParseResult(
                s.substring(0, nonDigitCharIndex).toDouble(),
                s.substring(nonDigitCharIndex)
            )
        } else {
            error("Not valid number in $s")
        }
    }

    private data class ParseResult(val acc: Double, val remainingPart: String)
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui

/**
 *
 * @author shurik
 */
class MatchParserPlusMinus {
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
        var current = Num(s)
        var acc = current.acc

        while (current.rest.isNotEmpty()) {
            if (!(current.rest[0] == '+' || current.rest[0] == '-')) break

            val sign = current.rest[0]
            val next = current.rest.substring(1)

            acc = current.acc

            current = Num(next)
            if (sign == '+') {
                acc += current.acc
            } else {
                acc -= current.acc
            }
            current.acc = acc
        }
        return Result(current.acc, current.rest)
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
                throw Exception("not valid number '" + s.substring(0, i + 1) + "'")
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
}

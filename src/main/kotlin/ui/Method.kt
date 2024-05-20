package ui

import kotlin.random.Random

enum class Method {
    LeftRectangle {
        override fun calculate(
            start: Double,
            end: Double,
            countSub: Int,
            f: (Double) -> Double,
        ): Double {
            val deltaX = (end - start) / countSub

            var acc = 0.0
            repeat(countSub) { i ->
                acc += f(start + deltaX * i)
            }

            return acc * deltaX
        }
    },
    Simpson {
        override fun calculate(
            start: Double,
            end: Double,
            countSub: Int,
            f: (Double) -> Double,
        ): Double {
            val n = countSub / 2

            val first = (end - start) / (6 * n)

            fun fi(i: Int) = f(start + ((end - start) * i) / (2 * n))

            val f0: Double = fi(0)
            var fFirst = 0.0
            var fSecond = 0.0
            val f2N: Double = fi(n)

            (1..<n).forEach { i ->
                fFirst += fi(2 * i - 1)
                fSecond += fi(2 * i)
            }

            return first * (f0 + 4 * fFirst + 2 * fSecond + f2N)
        }
    },
    MonteCarlo {
        override fun calculate(
            start: Double,
            end: Double,
            countSub: Int,
            f: (Double) -> Double,
        ): Double {
            val arr = DoubleArray(countSub) {
                Random.nextDouble(start, end)
            }
            return arr.sumOf { f(it) } * (end - start) / countSub
        }
    };

    abstract fun calculate(
        start: Double,
        end: Double,
        countSub: Int,
        f: (Double) -> Double,
    ): Double
}
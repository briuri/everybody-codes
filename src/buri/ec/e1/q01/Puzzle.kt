package buri.ec.e1.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractLongs
import org.junit.Test
import java.math.BigInteger

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(11611972920, true)
        assertRun(1099999998, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(11051340, true)
        assertRun(236309384189337, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(3279640, true)
        assertRun(667544464902526, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var max = 0L
        for (line in input) {
            val numbers = line.extractLongs()
            val result =
                eni(part, numbers[0], numbers[3], numbers[6]) +
                        eni(part, numbers[1], numbers[4], numbers[6]) +
                        eni(part, numbers[2], numbers[5], numbers[6])
            max = max.coerceAtLeast(result)
        }
        return max
    }

    private fun eni(part: Part, rawN: Long, exp: Long, rawMod: Long): Long {
        val n = BigInteger.valueOf(rawN)
        val mod = BigInteger.valueOf(rawMod)
        val remainders = mutableListOf<Long>()
        if (part.isOne()) {
            var score = 1L
            repeat(exp.toInt()) {
                score = score * rawN % rawMod
                remainders.add(0, score)
            }
        } else if (part.isTwo()) {
            for (i in 0..4) {
                val expStep = BigInteger.valueOf(exp - i)
                val result = n.modPow(expStep, mod)
                remainders.add(result.toLong())
            }
        } else {
            val cycle = mutableListOf<BigInteger>()
            var i = 0
            while (true) {
                val result = n.modPow(BigInteger.valueOf(exp - i), mod)
                if (result !in cycle) {
                    cycle.add(result)
                } else {
                    break
                }
                i++
            }

            var leftoverStart = exp % cycle.size
            while (n.modPow(BigInteger.valueOf(leftoverStart), mod) != cycle[0]) {
                leftoverStart += cycle.size
            }
            var leftover = 0L
            for (j in 1L..leftoverStart.toInt()) {
                leftover += n.modPow(BigInteger.valueOf(j), mod).toLong()
            }
            val numCycles = (exp - leftoverStart) / cycle.size
            val total = cycle.sumOf { it.toLong() } * numCycles + leftover
            return total
        }
        return remainders.joinToString("").toLong()
    }
}
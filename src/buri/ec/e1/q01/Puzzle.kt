package buri.ec.e1.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import buri.ec.common.extractLongs
import org.junit.Test
import java.math.BigInteger
import kotlin.math.pow

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
        assertRun(0, true)
        assertRun(0, false, true)
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

    private fun eni(part: Part, n: Long, exp: Long, mod: Long): Long {
        var remainders = mutableListOf<Long>()
        if (part.isOne()) {
            var score = 1L
            repeat(exp.toInt()) {
                score = score * n % mod
                remainders.add(0, score)
            }
        } else {
            for (i in 0..4) {
                val expStep = BigInteger.valueOf(exp - i)
                val result = BigInteger.valueOf(n).modPow(expStep, BigInteger.valueOf(mod))
                remainders.add(result.toLong())
            }
        }
        return remainders.joinToString("").toLong()
    }
}
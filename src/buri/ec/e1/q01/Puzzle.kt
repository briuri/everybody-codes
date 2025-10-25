package buri.ec.e1.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import org.junit.Test
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
        assertRun(0, false, true)
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
            val numbers = line.extractInts()
            val result =
                eni(part, numbers[0], numbers[3], numbers[6]) +
                        eni(part, numbers[1], numbers[4], numbers[6]) +
                        eni(part, numbers[2], numbers[5], numbers[6])
            max = max.coerceAtLeast(result)
        }
        return max
    }

    private fun eni(part: Part, n: Int, exp: Int, mod: Int): Long {
        var remainders = mutableListOf<Int>()
        var score = 1
        repeat(exp) {
            score = score * n % mod
            remainders.add(0, score)
        }
        return remainders.joinToString("").toLong()
    }
}
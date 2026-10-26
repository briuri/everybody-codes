package buri.ec.e2026.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(11, 1)
        assertRun(1452, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(9, 1)
        assertRun(307, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(61, 1)
        assertRun(682048, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var missingCones = 0
        val requiredCones = mutableListOf<Int>()
        val outpostCones = input[0].extractInts()
        val startIndex = if (part.isOne()) {
            1
        } else {
            3
        }
        for (i in startIndex..outpostCones.last() + 1) {
            if (part.isOne()) {
                requiredCones.add(i * i)
            } else if (part.isTwo()) {
                requiredCones.add(i * i - (i - 2) * (i - 2))
            } else {
                if (i % 2 != 0) {
                    requiredCones.add(i * i - 1)
                }
            }
        }
        for (outpost in outpostCones) {
            val nextPossibleOutpost = requiredCones.first { it >= outpost }
            missingCones += (nextPossibleOutpost - outpost)
        }
        return missingCones
    }
}
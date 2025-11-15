package buri.ec.e2025.q06

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(5, 1)
        assertRun(187, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(11, 1)
        assertRun(3490, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(3442321, 1)
        assertRun(1667582509, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val pairs = mutableListOf<Pair<Char, Char>>()
        pairs.add(Pair('a', 'A'))
        if (!part.isOne()) {
            pairs.add(Pair('b', 'B'))
            pairs.add(Pair('c', 'C'))
        }

        val repeat = if (!part.isThree()) {
            1
        } else {
            1000
        }
        val letters = input[0].repeat(repeat)
        var count = 0
        for (pair in pairs) {
            var index = letters.indexOf(pair.first)
            while (index != -1 && index <= letters.lastIndex) {
                val localCount = if (!part.isThree()) {
                    letters.take(index).count { it == pair.second }
                } else {
                    val leftBound = (index - 1000).coerceAtLeast(0)
                    val rightBoundExclusive = (index + 1001).coerceAtMost(letters.length)
                    letters.substring(leftBound, rightBoundExclusive).count { it == pair.second }
                }
                count += localCount
                index = letters.indexOf(pair.first, index + 1)
            }
        }
        return count
    }
}
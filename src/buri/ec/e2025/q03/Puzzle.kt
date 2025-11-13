package buri.ec.e2025.q03

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
        assertRun(29, true)
        assertRun(2532, isExample = false, toConsole = true)
    }

    @Test
    fun runPart2() {
        assertRun(781, true)
        assertRun(226, isExample = false, toConsole = true)
    }

    @Test
    fun runPart3() {
        assertRun(3, true)
        assertRun(2229, isExample = false, toConsole = true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val numbers = input[0].extractInts().sorted().toMutableList()
        var uniqueNumbers = numbers.toSet().toList()

        if (part.isOne()) {
            return uniqueNumbers.sum()
        } else if (part.isTwo()) {
            return uniqueNumbers.subList(0, 20).sum()
        }

        var sets = 0
        while (numbers.isNotEmpty()) {
            sets++
            for (num in uniqueNumbers) {
                numbers.remove(num)
            }
            uniqueNumbers = numbers.toSet().toList()
        }
        return sets
    }
}
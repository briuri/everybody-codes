package buri.ec.e4.q01

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
        assertRun(66, 1)
        assertRun(34, 2)
        assertRun(298, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(43, 1)
        assertRun(32711, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(27, 1)
        assertRun(35, 2)
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var sum = 0
        for (line in input) {
            val steps = line.extractInts()

            var current = 0
            val visited = mutableSetOf<Int>()
            visited.add(current)
            for (step in steps) {
                current = getNext(part, current, step, visited)
                visited.add(current)
            }
            sum += current
        }
        return sum
    }

    fun getNext(part: Part, current: Int, step: Int, visited: MutableSet<Int>): Int {
        var test = current - step
        if (part.isOne()) {
            if (test < 0 || test in visited) {
                test = current + step
            }
            return test
        }
        if (part.isTwo()) {
            if (test < 0 || test in visited) {
                test = current + step
                while (test in visited) {
                    test++
                }
            }
        }
        else {
            
        }
        return test
    }
}
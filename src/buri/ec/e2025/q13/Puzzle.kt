package buri.ec.e2025.q13

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
        assertRun(67, 1)
        assertRun(933, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(30, 1)
        assertRun(1989, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(87640, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val dial = ArrayDeque<Long>()
        dial.addFirst(1)

        if (part.isOne()) {
            var onRight = true
            val numbers = input.map { it.toLong() }
            for (number in numbers) {
                if (onRight) {
                    dial.addLast(number)
                } else {
                    dial.addFirst(number)
                }
                onRight = !onRight
            }
        } else {
            var onRight = true
            for (range in input) {
                val start = range.split("-")[0].toLong()
                val end = range.split("-")[1].toLong()
                for (i in start..end) {
                    if (onRight) {
                        dial.addLast(i)
                    } else {
                        dial.addFirst(i)
                    }
                }
                onRight = !onRight
            }
        }
        // Reset dial to 1.
        while (dial.first() != 1L) {
            dial.addLast(dial.removeFirst())
        }
        val turns = if (part.isOne()) {
            2025
        } else if (part.isTwo()) {
            20252025
        } else {
            202_520_252_025
        }
        repeat((turns % dial.size).toInt()) {
            dial.addLast(dial.removeFirst())
        }
        return dial.first()
    }
}
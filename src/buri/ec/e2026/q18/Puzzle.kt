package buri.ec.e2026.q18

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
        assertRun(96, 1)
        assertRun(807, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(205, 1)
        assertRun(2423053, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(938868227838916, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val bars = mutableSetOf<Bar>()
        for (line in input) {
            val numbers = line.extractInts()
            val width = if (part.isOne()) {
                1
            } else {
                numbers[2]
            }
            bars.add(Bar(numbers[0], numbers[1], width))
        }
        
        val maxY = bars.maxOf { it.yRange.last }
        var materialNeeded = 0L
        for (y in maxY downTo 1) {
            val barsInRow = bars.filter { it.yRange.contains(y) }
            val xRange = barsInRow.minOf { it.xRange.first }..barsInRow.maxOf { it.xRange.last }
            materialNeeded += xRange.last - xRange.first + 1
        }
        return materialNeeded
    }
}

class Bar(x: Int, y: Int, width: Int) {
    val xRange = x..<x + width
    val yRange = 1..y
}
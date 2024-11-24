package buri.ec.y24.q04

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test
import kotlin.math.absoluteValue

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(10, true)
        assertRun(87, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(844594, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(8, true)
        assertRun(127387940, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val heights = input.map { it.toInt() }
        if (part != Part.THREE) {
            return heights.sumOf { it - heights.min() }
        }
        val median = heights.sorted()[heights.size / 2]
        return heights.sumOf { (it - median).absoluteValue }
    }
}
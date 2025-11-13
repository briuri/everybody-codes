package buri.ec.template

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
        assertRun(0, true)
        assertRun(0, isExample = false, toConsole = true)
    }

    @Test
    fun runPart2() {
        assertRun(0, true)
        assertRun(0, isExample = false, toConsole = true)
    }

    @Test
    fun runPart3() {
        assertRun(0, true)
        assertRun(0, isExample = false, toConsole = true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        return -1
    }
}
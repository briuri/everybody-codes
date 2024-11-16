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

    private val input1 = """
        
    """.trimIndent()

    private val input2 = """
        
    """.trimIndent()

    private val input3 = """
      
    """.trimIndent()

    @Test
    fun runPart1() {
        assertRun(0, "example1")
        assertRun(0, input1, true)
    }

    @Test
    fun runPart2() {
        assertRun(0, "example2")
        assertRun(0, input2, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, "example3")
        assertRun(0, input3, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: String): Number {
        return -1
    }
}
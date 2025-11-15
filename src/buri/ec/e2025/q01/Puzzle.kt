package buri.ec.e2025.q01

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
        assertRun("Fyrryn", 1)
        assertRun("Xaneldrin", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("Elarzris", 1)
        assertRun("Ulmarulth", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("Drakzyph", 1)
        assertRun("Myrjorath", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val names = input[0].split(",").toMutableList()
        val instructions = input.last().split(",")

        var index = 0
        for (instruction in instructions) {
            index += getOffset(instruction)
            if (part.isOne()) {
                index = index.coerceAtLeast(0).coerceAtMost(names.lastIndex)
            } else {
                while (index < 0) {
                    index += names.size
                }
                index %= names.size
                if (part.isThree()) {
                    val temp = names[index]
                    names[index] = names[0]
                    names[0] = temp
                    index = 0
                }
            }
        }
        return names[index]

    }

    fun getOffset(instruction: String): Int {
        val direction = if (instruction.startsWith("L")) {
            -1
        } else {
            1
        }
        return instruction.drop(1).toInt() * direction
    }
}
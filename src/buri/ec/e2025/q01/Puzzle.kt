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
            index = getBoundedIndex(part, index, names.size)
            if (part.isThree()) {
                val swap = names[index]
                names[index] = names[0]
                names[0] = swap
                index = 0
            }
        }
        return names[index]

    }

    // Returns a min/max bounded index in Part 1, and a circular index in Part 2/3.
    fun getBoundedIndex(part: Part, index: Int, size: Int): Int {
        if (part.isOne()) {
            return index.coerceAtLeast(0).coerceAtMost(size - 1)
        }
        var newIndex = index
        while (newIndex < 0) {
            newIndex += size
        }
        newIndex %= size
        return newIndex
    }

    // Translates an instruction into a numeric offset.
    fun getOffset(instruction: String): Int {
        val direction = if (instruction.startsWith("L")) {
            -1
        } else {
            1
        }
        return instruction.drop(1).toInt() * direction
    }
}
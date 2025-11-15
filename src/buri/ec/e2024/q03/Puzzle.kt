package buri.ec.e2024.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(35, 1)
        assertRun(124, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(2737, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(29, 1)
        assertRun(10365, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val inputTransform = input.map { it.replace('.', '0').replace('#', '1') }
        val grid = Grid.fromIntInput(inputTransform)

        var level = 1
        while (true) {
            val validDigs = grid.filter { it == level }.filter { grid.canDig(it, part.isThree()) }
            if (validDigs.isEmpty()) {
                break
            }
            for (pos in validDigs) {
                grid[pos] = level + 1
            }
            level++
        }

        return grid.sum()
    }

    /**
     * Checks if all neighbours have the same height (so this square can be dug). Never needs to check beyond grid bounds
     * except in part 3.
     */
    private fun Grid<Int>.canDig(pos: Point2D<Int>, useDiagonals: Boolean): Boolean {
        val (x, y) = pos
        val value = this[x, y]
        val values = mutableListOf<Int>()
        values.add(value)

        if (isInBounds(x - 1, y)) {
            values.add(this[x - 1, y])
        }
        if (isInBounds(x + 1, y)) {
            values.add(this[x + 1, y])
        }
        if (isInBounds(x, y - 1)) {
            values.add(this[x, y - 1])
        }
        if (isInBounds(x, y + 1)) {
            values.add(this[x, y + 1])
        }
        // One or more neighbours was a border (so it's 0).
        if (values.size != 5) {
            values.add(0)
        }
        if (useDiagonals) {
            if (isInBounds(x - 1, y - 1)) {
                values.add(this[x - 1, y - 1])
            }
            if (isInBounds(x + 1, y - 1)) {
                values.add(this[x + 1, y - 1])
            }
            if (isInBounds(x - 1, y + 1)) {
                values.add(this[x - 1, y + 1])
            }
            if (isInBounds(x + 1, y + 1)) {
                values.add(this[x + 1, y + 1])
            }
            // Don't need to worry about border diagonals -- covered by adjacents above.
        }
        return (values.toSet().size == 1)
    }
}
package buri.ec.e2025.q14

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
        assertRun(200, 1)
        assertRun(508, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1171540, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(278388552, 1)
        assertRun(1006105152, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val bigGrid = Grid(34, 34, '.')
        val littleGrid = Grid.fromCharInput(input)
        var grid = if (!part.isThree()) {
            littleGrid
        } else {
            bigGrid
        }
        val turns = if (part.isOne()) {
            10L
        } else if (part.isTwo()) {
            2025L
        } else {
            1_000_000_000L
        }

        var round = 0L
        var activeSum = 0L
        var firstMatch = -1L
        val repeats = mutableListOf<Int>()
        while (round != turns) {
            round++
            val newGrid = grid.copy()
            for (y in grid.yRange) {
                for (x in grid.xRange) {
                    val point = Point2D(x, y)
                    val neighbors = grid.getNeighbors(point, true).toMutableList()
                    neighbors.removeAll(grid.getNeighbors(point, false))
                    val count = neighbors.filter { grid[it] == '#' }.size
                    newGrid[point] =
                        if ((grid[point] == '#') && (count % 2 == 1) || (grid[point] == '.') && (count % 2 == 0)) {
                            '#'
                        } else {
                            '.'
                        }
                }
            }
            grid = newGrid

            if (!part.isThree()) {
                activeSum += grid.filter { it == '#' }.size
            } else {
                val center = grid.getSubGrid(Point2D(13, 13), 8, 8)
                if (center.toString() == littleGrid.toString()) {
                    val count = grid.filter { it == '#' }.size
                    if (repeats.isNotEmpty() && count == repeats[0]) {
                        val cycleLength = round - firstMatch
                        while (round + cycleLength < turns) {
                            activeSum += repeats.sum()
                            round += cycleLength
                        }
                    }
                    activeSum += count
                    repeats.add(count)
                    if (firstMatch == -1L) {
                        firstMatch = round
                    }
                }
            }
        }
        return activeSum
    }
}
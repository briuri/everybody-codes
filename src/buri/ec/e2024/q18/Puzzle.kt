package buri.ec.e2024.q18

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.countSteps
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
        assertRun(11, true)
        assertRun(119, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(21, true)
        assertRun(1641, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(12, true)
        assertRun(226331, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val spaces = grid.filter { it == '.' }
        val startA = spaces.first()
        val startB = spaces.last()
        val trees = grid.filter { it == 'P' }
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            grid.getNeighbors(current).filter { grid[it] != '#' }
        }

        val cameFromA = pathfinder.exploreFrom(startA)
        val stepsA = trees.map { cameFromA.countSteps(startA, it) }
        if (part.isOne()) {
            return stepsA.max()
        }
        if (part.isTwo()) {
            val cameFromB = pathfinder.exploreFrom(startB)
            val stepsB = trees.map { cameFromB.countSteps(startB, it) }
            val steps = (stepsA zip stepsB).map { it.first.coerceAtMost(it.second) }
            return steps.max()
        }

        // Part THREE
        var minSteps = Int.MAX_VALUE
        for (space in spaces) {
            val cameFrom = pathfinder.exploreFrom(space)
            val steps = trees.sumOf { cameFrom.countSteps(space, it) }
            minSteps = minSteps.coerceAtMost(steps)
        }
        return minSteps
    }
}
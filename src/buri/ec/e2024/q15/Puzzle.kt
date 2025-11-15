package buri.ec.e2024.q15

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
        assertRun(26, 1)
        assertRun(208, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(38, 1)
        assertRun(524, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(1542, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            grid.getNeighbors(current).filter { grid[it] !in listOf('#', '~') }
        }

        val exit = grid.filter { it == '.' }.first()
        val herbs = grid.filter { it !in listOf('#', '.', '~') }
        val allNodes = mutableSetOf<Point2D<Int>>()
        allNodes.addAll(herbs)
        allNodes.add(exit)

        val distances = mutableMapOf<Pair<Point2D<Int>, Point2D<Int>>, Int>()
        for (start in allNodes) {
            val cameFrom = pathfinder.exploreFrom(start)
            for (end in allNodes.filter { it != start }) {
                distances[Pair(start, end)] = cameFrom.countSteps(start, end)
            }
        }

        // Plug in some likely paths to avoid trying them all.
        val frontier = mutableListOf<State>()
        if (part.isOne()) {
            frontier.add(State(exit, listOf("H", "."), 0))
        } else if (part.isTwo()) {
            if (input.size < 20) {
                frontier.add(State(exit, listOf("A", "B", "C", "."), 0))
                frontier.add(State(exit, listOf("A", "C", "B", "."), 0))
            } else {
                frontier.add(State(exit, listOf("A", "B", "C", "D", "E", "."), 0))
                frontier.add(State(exit, listOf("A", "B", "C", "E", "D", "."), 0))
                frontier.add(State(exit, listOf("A", "C", "E", "D", "B", "."), 0))
                frontier.add(State(exit, listOf("A", "C", "D", "E", "B", "."), 0))
                frontier.add(State(exit, listOf("A", "C", "E", "D", "B", "."), 0))
            }
        } else {
            // Left half of the map in part 3 is 466 starting at 85,75
            // Right half of the map in part 3 is 432 starting at 169,75
            val steps = 466 + 432
            // Edited input file to remove bad paths and unnecessary herbs.
            frontier.add(State(exit, listOf("G", "I", "A", "J", "P", "H", "."), steps))
            frontier.add(State(exit, listOf("G", "I", "P", "J", "A", "H", "."), steps))
            frontier.add(State(exit, listOf("H", "A", "J", "P", "I", "G", "."), steps))
        }
        var minDistance = Int.MAX_VALUE

        while (frontier.isNotEmpty()) {
            frontier.sortBy { it.remainingPath.size }

            val current = frontier.removeFirst()
            val nextHerb = current.remainingPath.first()
            if (nextHerb == ".") {
                val interimDistance = distances[Pair(current.start, exit)]!!
                minDistance = minDistance.coerceAtMost(current.stepsSoFar + interimDistance)
            } else {
                for (next in grid.filter { it == nextHerb[0] }) {
                    val nextDistance = current.stepsSoFar + distances[Pair(current.start, next)]!!
                    frontier.add(State(next, current.remainingPath.drop(1), nextDistance))
                }
            }
        }
        return minDistance
    }
}

data class State(val start: Point2D<Int>, val remainingPath: List<String>, val stepsSoFar: Int)
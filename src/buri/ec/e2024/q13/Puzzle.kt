package buri.ec.e2024.q13

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
        assertRun(28, true)
        assertRun(145, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(610, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(14, true)
        assertRun(532, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val end = Position(grid.filter { it == 'E' }.first(), 0)
        grid[end.coords] = '0'

        val starts = mutableSetOf<Position>()
        for (start in grid.filter { it == 'S' }) {
            starts.add(Position(start, 0))
            grid[start] = '0'
        }

        var bestTry = Int.MAX_VALUE
        val frontier = ArrayDeque<State>()
        val bestSteps = mutableMapOf<Position, Int>()
        frontier.add(State(end, 0))
        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            if (current.position in bestSteps.keys && current.steps >= bestSteps[current.position]!!) {
                continue
            }
            bestSteps[current.position] = current.steps
            if (current.position in starts) {
                bestTry = bestTry.coerceAtMost(current.steps)
            }

            for (neighbor in grid.getNeighbors(current.position.coords)
                .filter { grid[it] != '#' && grid[it].digitToInt() == current.position.height }) {
                val next = State(Position(neighbor, current.position.height), current.steps + 1)
                frontier.add(next)
            }
            val up = Position(current.position.coords, current.position.height + 1)
            frontier.add(State(up, current.steps + 1))
            val down = Position(current.position.coords, current.position.height - 1)
            frontier.add(State(down, current.steps + 1))
        }
        return bestTry
    }
}

data class Position(val coords: Point2D<Int>, var height: Int) {
    init {
        if (height == 10) {
            height = 0
        }
        if (height == -1) {
            height = 9
        }
    }
}

data class State(val position: Position, val steps: Int)
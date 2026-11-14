package buri.ec.e2026.q15

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Direction
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import org.junit.Test
import kotlin.math.abs
import kotlin.math.min

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(184, 1)
        assertRun(281, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(5448, 1)
        assertRun(20297, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(540320698784, 1)
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val gridStart = grid.filter { it == 'S' }.first()
        val gridEnd = grid.filter { it == 'E' }.first()
        val middleY = gridStart.y

        // Reduce the runs for sulphur down to a start, end, and number of steps.
        // Find the points where the maze narrows to a single passable block, to reduce the number of runs to consider at the same point.
        var isBeforeChokepoint = true
        val chokepoints = mutableListOf<Int>()
        chokepoints.add(gridStart.x)
        val sulphurRuns = mutableSetOf<SulphurRun>()
        for (x in gridStart.x + 1..<gridEnd.x) {
            val start = Point2D(x, middleY)
            val oneNorth = Point2D(x, middleY - 1)
            if (!isBeforeChokepoint && chokepoints.isNotEmpty() && start.x > chokepoints.last()) {
                isBeforeChokepoint = true
            }
            if (start !in sulphurRuns.map { it.end } && grid[oneNorth] != '#') {
                val runN = grid.getSuplhurRun(start, true)
                if (runN != null) {
                    sulphurRuns.add(runN)
                    if (isBeforeChokepoint) {
                        chokepoints.add(runN.end.x + 1)
                        isBeforeChokepoint = false
                    }
                }
                val runS = grid.getSuplhurRun(start, false)
                if (runS != null) {
                    sulphurRuns.add(runS)
                    if (isBeforeChokepoint) {
                        chokepoints.add(runS.end.x + 1)
                        isBeforeChokepoint = false
                    }
                }
            }
        }
        chokepoints.add(gridEnd.x)

        if (part.isOne()) {
            var minSteps = 0
            // Calculate the min steps between chokepoints.
            for (i in 0..<chokepoints.lastIndex) {
                val chunkStart = Point2D(chokepoints[i], middleY)
                val chunkEnd = Point2D(chokepoints[i + 1], middleY)
                val chunkRuns =
                    sulphurRuns.filter { it.start.x > chokepoints[i] && it.end.x < chokepoints[i + 1] }.toSet()

                var minChunkSteps = Int.MAX_VALUE
                for (path in getAllPaths(chunkRuns, chunkStart, chunkEnd)) {
                    var pathSteps = 0
                    for (j in 0..<path.lastIndex) {
                        val run = sulphurRuns.firstOrNull { it.matches(path[j], path[j + 1]) }
                        pathSteps += run?.steps ?: abs(path[j + 1].x - path[j].x)
                    }
                    minChunkSteps = min(minChunkSteps, pathSteps)
                }
                minSteps += minChunkSteps
            }
            return minSteps
        }
        if (part.isTwo()) {
            val sulphurSoFar = mutableMapOf<Int, Int>()
            var bestStepsToEnd = Int.MAX_VALUE
            val sulphurTarget = 2026
            val frontier = ArrayDeque<State>()
            frontier.add(State(gridStart, Direction.WEST, 0, 0))

            while (frontier.isNotEmpty()) {
                val current = frontier.removeFirst()
                if (current.pos == gridEnd) {
                    bestStepsToEnd = min(bestStepsToEnd, current.steps)
                }
                if (sulphurSoFar[current.steps] == null || sulphurSoFar[current.steps]!! < current.sulphur) {
                    sulphurSoFar[current.steps] = current.sulphur
                    if (current.sulphur >= sulphurTarget) {
                        if (current.pos != gridEnd) {
                            val toEnd = abs(current.pos.x - gridEnd.x)
                            val endState = State(gridEnd, Direction.WEST, current.steps + toEnd, current.sulphur)
                            frontier.add(endState)
                        }
                    } else {
                        for (run in sulphurRuns) {
                            val toRunStart = abs(current.pos.x - run.start.x)
                            var cantTurnAround = (toRunStart == 0 && current.cameFrom == run.cameFrom)
                            if (!cantTurnAround) {
                                val byStartState =
                                    State(
                                        run.end,
                                        run.cameFrom,
                                        current.steps + toRunStart + run.steps,
                                        current.sulphur + run.sulphur
                                    )
                                frontier.add(byStartState)
                            }

                            val toRunEnd = abs(current.pos.x - run.end.x)
                            cantTurnAround = (toRunEnd == 0 && current.cameFrom == run.cameFrom)
                            if (!cantTurnAround) {
                                val byEndState =
                                    State(
                                        run.start,
                                        run.cameFrom,
                                        current.steps + toRunEnd + run.steps,
                                        current.sulphur + run.sulphur
                                    )
                                frontier.add(byEndState)
                            }
                        }
                    }
                }
            }
            return bestStepsToEnd
        }
        // Part 3
        return -1
    }

    fun Grid<Char>.getSuplhurRun(pos: Point2D<Int>, toNorth: Boolean): SulphurRun? {
        var sulphur = 0
        var steps = 1
        var dx = 0
        var dy = if (toNorth) {
            -1
        } else {
            1
        }
        var current = Point2D(pos.x, pos.y + dy)
        while (current.y != pos.y) {
            if (this[current] == '@') {
                sulphur++
            }
            if (this[current.x + dx, current.y + dy] == '#') {
                if (dx == 0) {
                    dx = 1
                    dy = 0
                } else {
                    dx = 0
                    dy = if (toNorth) {
                        1
                    } else {
                        -1
                    }
                }
            }
            current = Point2D(current.x + dx, current.y + dy)
            steps++
        }
        return if (sulphur > 0) {
            SulphurRun(pos, current, toNorth, steps, sulphur)
        } else {
            null
        }
    }

    fun getAllPaths(requiredRuns: Set<SulphurRun>, start: Point2D<Int>, end: Point2D<Int>): List<List<Point2D<Int>>> {
        if (requiredRuns.isEmpty()) {
            return listOf(listOf(start, end))
        }
        val paths = mutableListOf<List<Point2D<Int>>>()
        for (run in requiredRuns) {
            val toRunStart = listOf(start, run.start)
            val toRunEnd = listOf(start, run.end)
            for (path in getAllPaths(requiredRuns - run, run.end, end)) {
                paths.add(toRunStart + path)
            }
            for (path in getAllPaths(requiredRuns - run, run.start, end)) {
                paths.add(toRunEnd + path)
            }
        }
        return paths
    }
}

data class SulphurRun(
    val start: Point2D<Int>,
    val end: Point2D<Int>,
    val isNorth: Boolean,
    val steps: Int,
    val sulphur: Int
) {
    override fun toString() = "[$start-$end $isNorth $steps $sulphur]"

    fun matches(testStart: Point2D<Int>, testEnd: Point2D<Int>): Boolean {
        return (start == testStart && end == testEnd) || (end == testStart && start == testEnd)
    }

    val cameFrom = if (isNorth) {
        Direction.NORTH
    } else {
        Direction.SOUTH
    }
}

data class State(val pos: Point2D<Int>, val cameFrom: Direction, val steps: Int, val sulphur: Int)
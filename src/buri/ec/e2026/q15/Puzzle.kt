package buri.ec.e2026.q15

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import org.junit.Test
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
        assertRun(0, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, 1)
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
        val requiredRuns = mutableSetOf<SulphurRun>()
        for (x in gridStart.x + 1..<gridEnd.x) {
            val start = Point2D(x, middleY)
            val oneNorth = Point2D(x, middleY - 1)
            if (!isBeforeChokepoint && chokepoints.isNotEmpty() && start.x > chokepoints.last()) {
                isBeforeChokepoint = true
            }
            if (start !in requiredRuns.map { it.end } && grid[oneNorth] != '#') {
                val runN = grid.getSuplhurRun(start, true)
                if (runN != null) {
                    requiredRuns.add(runN)
                    if (isBeforeChokepoint) {
                        chokepoints.add(runN.end.x + 1)
                        isBeforeChokepoint = false
                    }
                }
                val runS = grid.getSuplhurRun(start, false)
                if (runS != null) {
                    requiredRuns.add(runS)
                    if (isBeforeChokepoint) {
                        chokepoints.add(runS.end.x + 1)
                        isBeforeChokepoint = false
                    }
                }
            }
        }
        chokepoints.add(gridEnd.x)

        if (part.isOne()) {
            var steps = 0
            // Calculate the min steps between chokepoints.
            for (i in 0..<chokepoints.lastIndex) {
                val chunkStart = Point2D(chokepoints[i], middleY)
                val chunkEnd = Point2D(chokepoints[i + 1], middleY)
                val chunkRuns =
                    requiredRuns.filter { it.start.x > chokepoints[i] && it.end.x < chokepoints[i + 1] }.toSet()

                var minChunkSteps = Int.MAX_VALUE
                for (path in getAllPaths(chunkRuns, chunkStart, chunkEnd)) {
                    var pathSteps = 0
                    for (j in 0..<path.lastIndex) {
                        val run =
                            requiredRuns.firstOrNull { (it.start == path[j] && it.end == path[j + 1]) || (it.end == path[j] && it.start == path[j + 1]) }
                        pathSteps += run?.steps ?: path[j].getManhattanDistance(path[j + 1]).toInt()
                    }
                    minChunkSteps = min(minChunkSteps, pathSteps)
                }
                steps += minChunkSteps
            }
            return steps
        }
        if (part.isTwo()) {
            return -1
        }
        // Part 3
        return -1
    }

    fun Grid<Char>.getSuplhurRun(pos: Point2D<Int>, toNorth: Boolean): SulphurRun? {
        var foundSulphur = false
        var cost = 1
        var dx = 0
        var dy = if (toNorth) {
            -1
        } else {
            1
        }
        var current = Point2D(pos.x, pos.y + dy)
        while (current.y != pos.y) {
            if (this[current] == '@') {
                foundSulphur = true
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
            cost++
        }
        return if (foundSulphur) {
            SulphurRun(pos, current, toNorth, cost)
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

data class SulphurRun(val start: Point2D<Int>, val end: Point2D<Int>, val isNorth: Boolean, val steps: Int) {
    override fun toString() = "[$start-$end $isNorth $steps]"
}
package buri.ec.e2025.q20

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.countSteps
import buri.ec.common.position.Point3D
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(7, 1)
        assertRun(0, 2)
        assertRun(0, 3)
        assertRun(131, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(32, 1)
        assertRun(573, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(23, 1)
        assertRun(494, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     *
     * Using triangular grid described here:
     * https://www.redblobgames.com/grids/parts/#triangle-grids
     */
    override fun run(part: Part, input: List<String>): Number {
        val cells = mutableSetOf<Cell>()

        var leftOffset = 0
        for ((y, line) in input.withIndex()) {
            var x = leftOffset
            while (x < line.length) {
                if (line[x] != '.') {
                    val triangleX = (x - leftOffset) / 2
                    cells.add(Cell(Point3D(triangleX, y, 0), line[x]))
                    if (x + 1 in line.indices && line[x + 1] != '.') {
                        cells.add(Cell(Point3D(triangleX, y, 1), line[x + 1]))
                    }
                }
                x += 2
            }
            leftOffset++
        }

        if (part.isOne()) {
            var count = 0
            for (cell in cells.filter { it.isTrampoline() }) {
                val neighbors = cells.filter { it.point in cell.getNeighbors() }
                count += neighbors.count { it.isTrampoline() }
            }
            return (count / 2)
        } else if (part.isTwo()) {
            val start = cells.first { it.value == 'S' }
            val end = cells.first { it.value == 'E' }
            val pathfinder = Pathfinder<Cell> { current ->
                cells.filter { it.point in current.getNeighbors() }.filter { it.isTrampoline() }
            }
            val cameFrom = pathfinder.exploreFrom(start)
            return cameFrom.countSteps(start, end)
        }

        // Part Three
        val rotatingCells = mutableListOf<Set<Cell>>()
        rotatingCells.add(cells)
        rotatingCells.add(rotatingCells.last().rotate())
        rotatingCells.add(rotatingCells.last().rotate())

        val start = State(rotatingCells[0].first { it.value == 'S' }, 0)
        val frontier = ArrayDeque<State>()
        frontier.add(start)
        val cameFrom = mutableMapOf<State, State?>()
        cameFrom[start] = null

        while (frontier.isNotEmpty()) {
            val cellInOld = frontier.removeFirst()
            if (cellInOld.cell.value != 'E') {
                // Map the starting point onto the next set of cells.
                val newTime = (cellInOld.time + 1) % rotatingCells.size
                val newCells = rotatingCells[newTime]
                val cellInNew = newCells.first { it.point == cellInOld.cell.point }
                val neighbors =
                    newCells.filter { it.point in cellInNew.getNeighbors() && it.isTrampoline() }.toMutableList()
                // Jump in Place
                if (cellInNew.isTrampoline()) {
                    neighbors.add(cellInNew)
                }
                for (next in neighbors.filter { !cameFrom.containsKey(State(it, newTime)) }) {
                    val nextState = State(next, newTime)
                    frontier.add(nextState)
                    cameFrom[nextState] = cellInOld
                }
            }
        }

        var best = Int.MAX_VALUE
        for (i in rotatingCells.indices) {
            val steps = cameFrom.countSteps(start, State(rotatingCells[i].first { it.value == 'E' }, i))
            best = best.coerceAtMost(steps)
        }
        return best
    }

    fun Set<Cell>.rotate(): Set<Cell> {
        val newList = mutableListOf<Cell>()
        val maxX = this.maxOf { it.point.x }
        for (cell in this) {
            val x = maxX - cell.point.z - (cell.point.x + cell.point.y)
            val y = cell.point.x
            val z = cell.point.z
            newList.add(Cell(Point3D(x, y, z), cell.value))
        }
        newList.sortBy { it.point }
        return newList.toSet()
    }
}

data class State(val cell: Cell, val time: Int) {
    override fun toString(): String = "$cell at $time"
}

data class Cell(val point: Point3D<Int>, val value: Char) {

    override fun toString(): String = "$point=$value"

    fun getNeighbors(): List<Point3D<Int>> {
        val neighbors = mutableListOf<Point3D<Int>>()
        when (point.z) {
            0 -> {
                neighbors.add(Point3D(point.x, point.y - 1, 1))     // Above
                neighbors.add(Point3D(point.x - 1, point.y, 1))     // Left
                neighbors.add(Point3D(point.x, point.y, 1))         // Right
            }
            else -> {
                neighbors.add(Point3D(point.x, point.y, 0))         // Left
                neighbors.add(Point3D(point.x + 1, point.y, 0))     // Right
                neighbors.add(Point3D(point.x, point.y + 1, 0))     // Below
            }
        }
        return neighbors
    }

    fun isTrampoline(): Boolean = value in listOf('T', 'E')
}
package buri.ec.e3.q02

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.position.Bounds2D
import buri.ec.common.position.Direction
import buri.ec.common.position.Grid
import buri.ec.common.position.MutablePosition
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
import buri.ec.e2025.q20.Cell
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(12, 1)
        assertRun(298, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(47, 1)
        assertRun(3424, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(87, 1)
//        assertRun(239, 2)
//        assertRun(1539, 3)
//        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val deltas = mutableListOf<Point2D<Int>>()
        if (!part.isThree()) {
            // U R D L
            deltas.add(Point2D(0, -1))
            deltas.add(Point2D(1, 0))
            deltas.add(Point2D(0, 1))
            deltas.add(Point2D(-1, 0))
        } else {
            // U U U R R R D D D L L L
            deltas.add(Point2D(0, -1))
            deltas.add(Point2D(0, -1))
            deltas.add(Point2D(0, -1))
            deltas.add(Point2D(1, 0))
            deltas.add(Point2D(1, 0))
            deltas.add(Point2D(1, 0))
            deltas.add(Point2D(0, 1))
            deltas.add(Point2D(0, 1))
            deltas.add(Point2D(0, 1))
            deltas.add(Point2D(-1, 0))
            deltas.add(Point2D(-1, 0))
            deltas.add(Point2D(-1, 0))
        }
        val grid = Grid.fromCharInput(input)
        val start = grid.filter { it == '@' }.first()
        val ends = grid.filter { it == '#' }
        val visited = mutableSetOf<Point2D<Int>>()
        visited.add(start)
        val endNeighbors = mutableSetOf<Point2D<Int>>()

        var pos = start
        var deltaIndex = 0
        var step = 0

        if (!part.isOne()) {
            visited.addAll(ends)
            endNeighbors.addAll(ends.flatMap { it.getNeighbors() })
        }
        while (true) {
            val nextDirection = deltas[deltaIndex % 4]
            val nextPos = Point2D(pos.x + nextDirection.x, pos.y + nextDirection.y)
            if (nextPos !in visited) {
                pos = nextPos
                step++
                visited.add(pos)
                visited.addAll(getInternalSurrounded(pos, visited))
                if (part.isOne() && nextPos in ends) {
                    return step
                }
                if (!part.isOne() && endNeighbors.all { it in visited }) {
                    return step
                }
            }
            deltaIndex++
        }
    }

    fun getInternalSurrounded(pos: Point2D<Int>, visited: MutableSet<Point2D<Int>>): Set<Point2D<Int>> {
        val bounds = Bounds2D(visited)
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            current.getNeighbors().filter { it !in visited && it.x in bounds.x && it.y in bounds.y }
        }

        val internal = mutableSetOf<Point2D<Int>>()
        // Get all nearby points that have not yet been filled
        for (neighbor in pos.getNeighbors().filter { it !in visited }) {
            val cameFrom = pathfinder.exploreFrom(neighbor)
            // If any visted points are along the min/max bounds of the set, they are external.
            if (cameFrom.keys.none { it.x <= bounds.x.first || it.x >= bounds.x.last || it.y <= bounds.y.first || it.y >= bounds.y.last }) {
                internal.addAll(cameFrom.keys)
            }
        }
        return internal
    }
}
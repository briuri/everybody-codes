package buri.ec.e2024.q14

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
        assertRun(7, true)
        assertRun(155, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(32, true)
        assertRun(4995, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(46, true)
        assertRun(1252, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val segments = mutableSetOf<Point3D<Int>>()
        val leaves = mutableSetOf<Point3D<Int>>()
        for (branch in input) {
            var position = Point3D(0, 0, 0)
            for (command in branch.split(",")) {
                val amount = command.drop(1).toInt()
                val delta = when (command[0]) {
                    'U' -> Point3D(0, 1, 0)
                    'D' -> Point3D(0, -1, 0)
                    'L' -> Point3D(-1, 0, 0)
                    'R' -> Point3D(1, 0, 0)
                    'F' -> Point3D(0, 0, 1)
                    else -> Point3D(0, 0, -1)
                }
                repeat(amount) {
                    position = Point3D(position.x + delta.x, position.y + delta.y, position.z + delta.z)
                    segments.add(position)
                }
            }
            leaves.add(position)
        }
        if (part.isOne()) {
            return segments.maxOf { it.y }
        }
        if (part.isTwo()) {
            return segments.size
        }

        val pathfinder = Pathfinder<Point3D<Int>> { current ->
            segments.getNeighbors(current)
        }

        val cameFroms = mutableMapOf<Point3D<Int>, Map<Point3D<Int>, Point3D<Int>?>>()
        for (leaf in leaves) {
            cameFroms[leaf] = pathfinder.exploreFrom(leaf)
        }

        var minMurkiness = Int.MAX_VALUE
        for (y in 1..segments.maxOf { it.y }) {
            val trunk = Point3D(0, y, 0)
            if (trunk in segments) {
                var murkiness = 0
                for (leaf in leaves) {
                    val cameFrom = cameFroms[leaf]!!
                    murkiness += cameFrom.countSteps(leaf, trunk)
                }
                minMurkiness = minMurkiness.coerceAtMost(murkiness)
            }
        }
        return minMurkiness
    }

    private fun Set<Point3D<Int>>.getNeighbors(point: Point3D<Int>): List<Point3D<Int>> {
        val points = mutableSetOf<Point3D<Int>>()
        points.add(Point3D(point.x - 1, point.y, point.z))
        points.add(Point3D(point.x + 1, point.y, point.z))
        points.add(Point3D(point.x, point.y - 1, point.z))
        points.add(Point3D(point.x, point.y + 1, point.z))
        points.add(Point3D(point.x, point.y, point.z - 1))
        points.add(Point3D(point.x, point.y, point.z + 1))
        return this.intersect(points).toList()
    }
}
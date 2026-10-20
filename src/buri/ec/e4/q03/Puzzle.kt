package buri.ec.e4.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
import org.junit.Test
import kotlin.math.max

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(27, 1)
        assertRun(230, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(15, 1)
        assertRun(7, 2)
        assertRun(269, 3)
        assertRun(16921, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val width = input[0].split("=")[1].toInt()
        val height = input[1].split("=")[1].toInt()
        var hOffsets = input[2].split("=")[1]
        var vOffsets = input[3].split("=")[1]
        val floor = Floor(
            width,
            height,
            hOffsets.repeat(height / hOffsets.length + 1),
            vOffsets.repeat(width / vOffsets.length + 1)
        )

        if (part.isOne()) {
            return floor.getEnclosed().size
        } else if (part.isTwo()) {
            val enclosed = floor.getEnclosed()
            val first = enclosed.filter { floor.getColor(it) }
            val second = enclosed.filter { !floor.getColor(it) }
            return max(first.size, second.size)
        } else {
            return -1
        }
    }
}

data class Floor(val width: Int, val height: Int, val hOffsets: String, val vOffsets: String) {
    val xBounds = 0..<width
    val yBounds = 0..<height
    val colors = mutableMapOf<Point2D<Int>, Boolean>()
    val pathfinder =
        Pathfinder<Point2D<Int>> { current -> getNeighbors(current).filter { it.x in xBounds && it.y in yBounds } }

    init {
        var currentColor = true
        for (y in 0..<height) {
            for (x in 0..<width) {
                val point = Point2D(x, y)
                if (point !in colors) {
                    val cameFrom = pathfinder.exploreFrom(point)
                    for (connectedPoint in cameFrom.keys) {
                        colors[connectedPoint] = currentColor
                    }
                    currentColor = !currentColor
                } else {
                    currentColor = !colors[point]!!
                }
            }
        }
    }

    fun getColor(current: Point2D<Int>): Boolean {
        return colors[current]!!
    }

    fun getNeighbors(current: Point2D<Int>): List<Point2D<Int>> {
        val neighbors = mutableListOf<Point2D<Int>>()
        if (!hasStitchTop(current)) {
            neighbors.add(Point2D(current.x, current.y - 1))
        }
        if (!hasStitchBottom(current)) {
            neighbors.add(Point2D(current.x, current.y + 1))
        }
        if (!hasStitchLeft(current)) {
            neighbors.add(Point2D(current.x - 1, current.y))
        }
        if (!hasStitchRight(current)) {
            neighbors.add(Point2D(current.x + 1, current.y))
        }
        return neighbors
    }

    fun getEnclosed(): List<Point2D<Int>> {
        val points = mutableListOf<Point2D<Int>>()
        for (y in 0..<height) {
            for (x in 0..<width) {
                val point = Point2D(x, y)
                if (isEnclosed(point)) {
                    points.add(point)
                }
            }
        }
        return points
    }

    fun isEnclosed(pos: Point2D<Int>): Boolean {
        return (hasStitchTop(pos) && hasStitchBottom(pos) && hasStitchLeft(pos) && hasStitchRight(pos))
    }

    fun hasStitchTop(pos: Point2D<Int>): Boolean {
        val offset = hOffsets[pos.y].digitToInt()
        return (pos.x % 2 == offset)
    }

    fun hasStitchBottom(pos: Point2D<Int>): Boolean {
        return hasStitchTop(Point2D(pos.x, pos.y + 1))
    }

    fun hasStitchLeft(pos: Point2D<Int>): Boolean {
        val offset = vOffsets[pos.x].digitToInt()
        return (pos.y % 2 == offset)
    }

    fun hasStitchRight(pos: Point2D<Int>): Boolean {
        return hasStitchLeft(Point2D(pos.x + 1, pos.y))
    }
}
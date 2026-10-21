package buri.ec.e4.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Point2D
import org.junit.Test
import kotlin.collections.set
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
        assertRun(529497857464808, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val width = input[0].split("=")[1].toInt()
        val height = input[1].split("=")[1].toInt()
        val hOffsets = input[2].split("=")[1]
        val vOffsets = input[3].split("=")[1]

        if (part.isOne()) {
            val floor = Floor(width, height, hOffsets, vOffsets)
            return floor.enclosedPoints.size
        }
        if (part.isTwo()) {
            val floor = Floor(width, height, hOffsets, vOffsets)
            val (first, second) = floor.getColors()
            return max(first, second)
        }
        // Part Three
        // Break up grid into 4 parts, complete, remainder right, remainder bottom, and remainder bottom right.
        val subWidth = vOffsets.length * 2
        val subHeight = hOffsets.length * 2
        val xRepeats = width / subWidth
        val xRemainder = width % subWidth
        val yRepeats = height / subHeight
        val yRemainder = height % subHeight

        var color1 = 0L
        var color2 = 0L

        // Complete
        var colors = Floor(subWidth, subHeight, hOffsets, vOffsets).getColors()
        color1 += colors.first * xRepeats * yRepeats
        color2 += colors.second * xRepeats * yRepeats

        // Right
        colors = Floor(xRemainder, subHeight, hOffsets, vOffsets).getColors()
        color1 += colors.first * yRepeats
        color2 += colors.second * yRepeats

        // Bottom
        colors = Floor(subWidth, yRemainder, hOffsets, vOffsets).getColors()
        color1 += colors.first * xRepeats
        color2 += colors.second * xRepeats

        // Bottom Right
        colors = Floor(xRemainder, yRemainder, hOffsets, vOffsets).getColors()
        color1 += colors.first
        color2 += colors.second

        return max(color1, color2)
    }
}

data class Floor(
    val width: Int,
    val height: Int,
    val hOffsets: String,
    val vOffsets: String
) {
    val connectedPoints = mutableMapOf<Point2D<Int>, Point2D<Int>>()
    val enclosedPoints = mutableListOf<Point2D<Int>>()
    val colors = mutableMapOf<Point2D<Int>, Boolean>()

    init {
        // Connect grouped tiles.
        for (y in 0..height) {
            for (x in 0..width) {
                connectedPoints[Point2D(x, y)] = Point2D(x, y)
            }
        }
        for (y in 0..height) {
            for (x in 1..width) {
                if (!hasStitchLeft(Point2D(x, y))) {
                    connect(Point2D(x - 1, y), Point2D(x, y))
                }
            }
        }
        for (y in 1..height) {
            for (x in 0..width) {
                if (!hasStitchTop(Point2D(x, y))) {
                    connect(Point2D(x, y - 1), Point2D(x, y))
                }
            }
        }

        // Find enclosed tiles.
        for (y in 0..<height) {
            for (x in 0..<width) {
                val point = Point2D(x, y)
                if (isEnclosed(point)) {
                    enclosedPoints.add(point)
                }
            }
        }

        // Color tiles.
        colors[getGroupStart(Point2D(0, 0))] = false
        for (y in 0..height) {
            for (x in 0..width) {
                val groupStart = getGroupStart(Point2D(x, y))
                if (groupStart !in colors) {
                    val current = if (y == 0) {
                        Point2D(x - 1, 0)
                    } else {
                        Point2D(x, y - 1)
                    }
                    colors[groupStart] = !colors[getGroupStart(current)]!!
                }
            }
        }
    }

    fun getGroupStart(current: Point2D<Int>): Point2D<Int> {
        if (connectedPoints[current] == current) {
            return current
        }
        val groupStart = getGroupStart(connectedPoints[current]!!)
        // Optimize lookup for next time.
        connectedPoints[current] = groupStart
        return groupStart
    }

    fun connect(left: Point2D<Int>, right: Point2D<Int>) {
        connectedPoints[getGroupStart(left)] = getGroupStart(right)
    }

    fun getColors(): Pair<Long, Long> {
        return Pair(
            enclosedPoints.filter { colors[it]!! }.size.toLong(),
            enclosedPoints.filter { !colors[it]!! }.size.toLong()
        )
    }

    fun isEnclosed(pos: Point2D<Int>): Boolean {
        return (hasStitchTop(pos) && hasStitchBottom(pos) && hasStitchLeft(pos) && hasStitchRight(pos))
    }

    fun hasStitchTop(pos: Point2D<Int>): Boolean {
        val offset = hOffsets[pos.y % hOffsets.length].digitToInt()
        return (pos.x % 2 == offset)
    }

    fun hasStitchBottom(pos: Point2D<Int>): Boolean {
        return hasStitchTop(Point2D(pos.x, pos.y + 1))
    }

    fun hasStitchLeft(pos: Point2D<Int>): Boolean {
        val offset = vOffsets[pos.x % vOffsets.length].digitToInt()
        return (pos.y % 2 == offset)
    }

    fun hasStitchRight(pos: Point2D<Int>): Boolean {
        return hasStitchLeft(Point2D(pos.x + 1, pos.y))
    }
}
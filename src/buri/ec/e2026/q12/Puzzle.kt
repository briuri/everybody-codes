package buri.ec.e2026.q12

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.position.Bounds2D
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
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
        assertRun(7, 1)
        assertRun(206, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(6, 1)
        assertRun(23, 2)
        assertRun(2036, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(8, 1)
        assertRun(29, 2)
        assertRun(16, 3)
        assertRun(2816, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val points = Grid.fromCharInput(input).filter { it == '#' }.toMutableList()
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            current.getNeighbors().filter { it in points }
        }
        val shapes = mutableSetOf<Shape>()
        while (points.isNotEmpty()) {
            val point = points.removeFirst()
            val cameFrom = pathfinder.exploreFrom(point)
            shapes.add(Shape(cameFrom.keys))
            points.removeAll(cameFrom.keys)
        }
        return shapes.sumOf { it.getSpells() }
    }
}

data class Shape(val points: Set<Point2D<Int>>) {
    val bounds = Bounds2D(points)
    val width = bounds.x.last - bounds.x.first + 1
    val height = bounds.y.last - bounds.y.first + 1
    val gap = mutableSetOf<Point2D<Int>>()

    init {
        for (y in bounds.y) {
            for (x in bounds.x) {
                val pos = Point2D(x, y)
                if (pos !in points) {
                    gap.add(pos)
                }
            }
        }
    }

    fun getSpells(): Int {
        if (isFilledRectangle()) {
            return getBaseSpells()
        }

        val gapBounds = Bounds2D(gap)
        val gapType = if (gapBounds.x.first == bounds.x.first && gapBounds.y.first == bounds.y.first) {
            GapType.NW
        } else if (gapBounds.x.last == bounds.x.last && gapBounds.y.first == bounds.y.first) {
            GapType.NE
        } else if (gapBounds.x.last == bounds.x.last && gapBounds.y.last == bounds.y.last) {
            GapType.SE
        } else if (gapBounds.x.first == bounds.x.first && gapBounds.y.last == bounds.y.last) {
            GapType.SW
        } else {
            GapType.CENTER
        }

        val chunks = this.subdivide()
        if (gapType != GapType.CENTER) {
            val left: Shape
            val right: Shape
            when (gapType) {
                GapType.NW -> {
                    left = chunks["s"]!!
                    right = merge(listOf(chunks["e"], chunks["se"]))
                }
                GapType.NE -> {
                    left = merge(listOf(chunks["w"], chunks["sw"]))
                    right = chunks["s"]!!
                }
                GapType.SE -> {
                    left = merge(listOf(chunks["nw"], chunks["w"]))
                    right = chunks["n"]!!
                }
                // SW
                else -> {
                    left = chunks["n"]!!
                    right = merge(listOf(chunks["ne"], chunks["e"]))
                }
            }
            return min(getBaseSpells(), left.getBaseSpells() + right.getBaseSpells())
        } else {
            var minSpells = Int.MAX_VALUE
            // First, try horizontal slices.
            var top: Shape = merge(listOf(chunks["nw"], chunks["n"], chunks["ne"]))
            var left: Shape = chunks["w"]!!
            var bottom: Shape = merge(listOf(chunks["sw"], chunks["s"], chunks["se"]))
            var right: Shape = chunks["e"]!!
            minSpells = min(
                minSpells,
                left.getBaseSpells() + right.getBaseSpells() + top.getBaseSpells() + bottom.getBaseSpells()
            )

            // Then, try vertical slices.
            top = chunks["n"]!!
            left = merge(listOf(chunks["nw"], chunks["w"], chunks["sw"]))
            bottom = chunks["s"]!!
            right = merge(listOf(chunks["ne"], chunks["e"], chunks["se"]))
            minSpells = min(
                minSpells,
                left.getBaseSpells() + right.getBaseSpells() + top.getBaseSpells() + bottom.getBaseSpells()
            )
            return minSpells
        }
    }

    fun isFilledRectangle(): Boolean {
        return (width * height == points.size)
    }

    fun getBaseSpells(): Int {
        return min(width, height)
    }

    fun subdivide(): Map<String, Shape?> {
        val gapBounds = Bounds2D(gap)
        val nwPoints = mutableSetOf<Point2D<Int>>()
        val nPoints = mutableSetOf<Point2D<Int>>()
        val nePoints = mutableSetOf<Point2D<Int>>()
        val wPoints = mutableSetOf<Point2D<Int>>()
        val ePoints = mutableSetOf<Point2D<Int>>()
        val swPoints = mutableSetOf<Point2D<Int>>()
        val sPoints = mutableSetOf<Point2D<Int>>()
        val sePoints = mutableSetOf<Point2D<Int>>()
        for (y in bounds.y) {
            for (x in bounds.x) {
                val point = Point2D(x, y)
                if (point in points) {
                    // Top Row
                    if (y < gapBounds.y.first) {
                        if (x < gapBounds.x.first) {
                            nwPoints.add(point)
                        } else if (x <= gapBounds.x.last) {
                            nPoints.add(point)
                        } else {
                            nePoints.add(point)
                        }
                    }
                    // Middle Row
                    else if (y <= gapBounds.y.last) {
                        if (x < gapBounds.x.first) {
                            wPoints.add(point)
                        } else {
                            ePoints.add(point)
                        }
                    }
                    // Bottom Row
                    else {
                        if (x < gapBounds.x.first) {
                            swPoints.add(point)
                        } else if (x <= gapBounds.x.last) {
                            sPoints.add(point)
                        } else {
                            sePoints.add(point)
                        }
                    }
                }
            }
        }
        val map = mutableMapOf<String, Shape?>()
        if (nwPoints.isNotEmpty()) {
            map["nw"] = Shape(nwPoints)
        }
        if (nPoints.isNotEmpty()) {
            map["n"] = Shape(nPoints)
        }
        if (nePoints.isNotEmpty()) {
            map["ne"] = Shape(nePoints)
        }
        if (wPoints.isNotEmpty()) {
            map["w"] = Shape(wPoints)
        }
        if (ePoints.isNotEmpty()) {
            map["e"] = Shape(ePoints)
        }
        if (swPoints.isNotEmpty()) {
            map["sw"] = Shape(swPoints)
        }
        if (sPoints.isNotEmpty()) {
            map["s"] = Shape(sPoints)
        }
        if (sePoints.isNotEmpty()) {
            map["se"] = Shape(sePoints)
        }
        return map
    }

    fun merge(shapes: List<Shape?>): Shape {
        val points = mutableSetOf<Point2D<Int>>()
        for (possibleShape in shapes) {
            if (possibleShape != null) {
                points.addAll(possibleShape.points)
            }
        }
        return Shape(points)
    }
}

enum class GapType {
    NW, NE, CENTER, SW, SE
}
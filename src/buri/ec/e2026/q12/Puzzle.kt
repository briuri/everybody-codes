package buri.ec.e2026.q12

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.position.Bounds2D
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
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val visited = mutableSetOf<Point2D<Int>>()
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            grid.getNeighbors(current).filter { grid[it] == '#' }
        }

        var numSpells = 0
        for (y in grid.yRange) {
            for (x in grid.xRange) {
                val pos = Point2D(x, y)
                if (pos in visited || grid[pos] == '.') {
                    continue
                }
                if (grid[pos] == '#') {
                    val cameFrom = pathfinder.exploreFrom(pos)
                    val shape = cameFrom.keys
                    visited.addAll(shape)
                    val bounds = Bounds2D(shape)
                    numSpells += if (shape.containsAll(
                            listOf(
                                Point2D(bounds.x.first, bounds.y.first),
                                Point2D(bounds.x.last, bounds.y.first),
                                Point2D(bounds.x.first, bounds.y.last),
                                Point2D(bounds.x.last, bounds.y.last)
                            )
                        )
                    ) {
                        handleRectangle(shape)
                    } else {
                        handleL(shape)
                    }
                }
            }
        }
        return numSpells
    }

    fun handleRectangle(rectangle: Set<Point2D<Int>>): Int {
        val minX = rectangle.minOf { it.x }
        val maxX = rectangle.maxOf { it.x }
        val minY = rectangle.minOf { it.y }
        val maxY = rectangle.maxOf { it.y }
        val width = maxX - minX + 1
        val height = maxY - minY + 1
        return min(width, height)
    }
    
    fun handleL(shape: Set<Point2D<Int>>): Int {
        var min = handleRectangle(shape)
        val bounds = Bounds2D(shape)

        val excluded = mutableSetOf<Point2D<Int>>()
        for (y in bounds.y) {
            for (x in bounds.x) {
                val pos = Point2D(x, y)
                if (pos !in shape) {
                    excluded.add(pos)
                }
            }
        }

        val exBounds = Bounds2D(excluded)
        val exclude = if (exBounds.x.first == bounds.x.first && exBounds.y.first == bounds.y.first) {
            Exclude.UL
        } else if (exBounds.x.last == bounds.x.last && exBounds.y.first == bounds.y.first) {
            Exclude.UR
        } else if (exBounds.x.last == bounds.x.last && exBounds.y.last == bounds.y.last) {
            Exclude.LR
        } else {
            Exclude.LL
        }

        val rectLeft = mutableSetOf<Point2D<Int>>()
        val rectRight = mutableSetOf<Point2D<Int>>()
        for (y in bounds.y) {
            for (x in bounds.x) {
                val pos = Point2D(x, y)
                if (pos in shape) {
                    when (exclude) {
                        Exclude.UL, Exclude.LL -> {
                            if (x <= exBounds.x.last) {
                                rectLeft.add(pos)
                            } else {
                                rectRight.add(pos)
                            }
                        }
                        else -> {
                            if (x < exBounds.x.first) {
                                rectLeft.add(pos)
                            } else {
                                rectRight.add(pos)
                            }
                        }
                    }
                }
            }
        }
        min = min(min, handleRectangle(rectLeft) + handleRectangle(rectRight))

        val rectTop = mutableSetOf<Point2D<Int>>()
        val rectBottom = mutableSetOf<Point2D<Int>>()
        for (y in bounds.y) {
            for (x in bounds.x) {
                val pos = Point2D(x, y)
                if (pos in shape) {
                    when (exclude) {
                        Exclude.UR, Exclude.UL -> {
                            if (y <= exBounds.y.last) {
                                rectTop.add(pos)
                            } else {
                                rectBottom.add(pos)
                            }
                        }
                        else -> {
                            if (y < exBounds.y.first) {
                                rectTop.add(pos)
                            } else {
                                rectBottom.add(pos)
                            }

                        }
                    }
                }
            }
        }
        min = min(min, handleRectangle(rectTop) + handleRectangle(rectBottom))
        return min
    }
}
enum class Exclude {
    UL, UR, LR, LL
}

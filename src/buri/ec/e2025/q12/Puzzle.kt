package buri.ec.e2025.q12

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
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
        assertRun(16, 1)
        assertRun(239, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(58, 1)
        assertRun(5594, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(133, 1)
        assertRun(4161, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromIntInput(input)
        val visited = mutableSetOf<Point2D<Int>>()

        val ul = Point2D(0, 0)
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            grid.getNeighbors(current).filter { grid[it] <= grid[current] && it !in visited }
        }
        val cameFrom = pathfinder.exploreFrom(ul)
        if (part.isOne()) {
            return cameFrom.keys.size
        }
        if (part.isTwo()) {
            val lr = Point2D<Int>(grid.xRange.last, grid.yRange.last)
            val cameFrom2 = pathfinder.exploreFrom(lr)
            val set = cameFrom.keys.toMutableSet()
            set.addAll(cameFrom2.keys)
            return set.size
        }

        var sum = getBestBarrel(grid, pathfinder, visited)
        sum += getBestBarrel(grid, pathfinder, visited)
        sum += getBestBarrel(grid, pathfinder, visited)
        return sum
    }

    fun getBestBarrel(grid: Grid<Int>, pathfinder: Pathfinder<Point2D<Int>>, visited: MutableSet<Point2D<Int>>): Int {
        val attempts = mutableMapOf<Point2D<Int>, Int>()
        for (x in grid.xRange) {
            for (y in grid.yRange) {
                val start = Point2D(x, y)
                if (start in visited) {
                    continue
                }
                attempts[start] = pathfinder.exploreFrom(start).keys.size
            }
        }
        val max = attempts.maxByOrNull { it.value }!!
        visited.addAll(pathfinder.exploreFrom(max.key).keys)
        return max.value
    }
}
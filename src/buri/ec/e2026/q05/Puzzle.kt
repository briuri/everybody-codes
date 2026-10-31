package buri.ec.e2026.q05

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
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
        assertRun(137, 1)
        assertRun(1085, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(69, 1)
        assertRun(1093, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(14, 1)
        assertRun(9296, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val center = Point2D(grid.xRange.last / 2, grid.yRange.last / 2)
        grid[center] = '#'

        // Build and grow land
        var frontier = mutableListOf<Point2D<Int>>()
        frontier.addAll(grid.getNeighbors(center, true))
        while (true) {
            var changed = false
            for (current in frontier) {
                if (grid.isLand(current)) {
                    grid[current] = '#'
                    changed = true
                }
            }
            if (!changed) {
                break
            }
            val nextNeighbors = grid.filter { it == '#' }.flatMap { grid.getNeighbors(it, true) }
            frontier = nextNeighbors.filter { grid[it] != '#' }.toSet().toMutableList()
        }

        if (part.isOne()) {
            return grid.count { it == '#' }
        }

        // Build initial shore
        val land = grid.filter { it == '#' }
        val shore = land.filter { grid.isInitialShore(it) }.toMutableSet()
        shore.addAll(land.filter { grid.isOnEdge(it) })
        shore.forEach { grid[it] = '=' }

        // Grow shore
        frontier = land.filter { grid.isNearShore(it) }.toMutableList()
        while (true) {
            var changed = false
            for (current in frontier) {
                if (grid.isGrowingShore(current)) {
                    grid[current] = '='
                    changed = true
                }
            }
            if (!changed) {
                break
            }
            val allSand = grid.filter { it == '=' }
            val nextNeighbors = allSand.flatMap { grid.getNeighbors(it, true) }
            frontier = nextNeighbors.filter { grid[it] != '=' }.toSet().toMutableList()
        }

        if (part.isTwo()) {
            return grid.count { it == '=' }
        }

        // Build initial trees
        for (x in grid.xRange.first + 2..grid.xRange.last - 2) {
            for (y in grid.yRange.first + 2..grid.yRange.last - 2) {
                val point = Point2D(x, y)
                if (grid.needsTree(point)) {
                    grid[point] = '@'
                }
            }
        }

        // Grow trees
        repeat(2) {
            val pointsToTree = mutableSetOf<Point2D<Int>>()
            val allLand = grid.filter { it == '#' }
            for (land in allLand) {
                val neighbors = grid.getNeighbors(land, true)
                val treeCount = neighbors.count { grid[it] == '@' }
                val sandCount = neighbors.count { grid[it] == '=' }
                if (treeCount >= 4 && sandCount == 0) {
                    pointsToTree.add(land)
                }
            }
            pointsToTree.forEach { grid[it] = '@' }
        }

        // Part 3
        return grid.count { it == '@' }
    }

    fun Grid<Char>.isOnEdge(point: Point2D<Int>): Boolean {
        return (point.x == xRange.first || point.x == xRange.last || point.y == yRange.first || point.y == yRange.last)
    }

    fun Grid<Char>.isLand(point: Point2D<Int>): Boolean {
        return getNeighbors(point, true).count { this[it] in listOf('1', '#') } >= 5
    }

    fun Grid<Char>.isInitialShore(point: Point2D<Int>): Boolean {
        return getNeighbors(point, true).any { this[it] in listOf('0', '1') }
    }

    fun Grid<Char>.isGrowingShore(point: Point2D<Int>): Boolean {
        return getNeighbors(point, true).count { this[it] == '=' } >= 5
    }

    fun Grid<Char>.isNearShore(point: Point2D<Int>): Boolean {
        return getNeighbors(point, true).any { this[it] == '=' }
    }

    fun Grid<Char>.needsTree(point: Point2D<Int>): Boolean {
        val neighbors = mutableSetOf<Point2D<Int>>()
        for (x in (point.x - 2)..(point.x + 2)) {
            for (y in (point.y - 2)..(point.y + 2)) {
                neighbors.add(Point2D(x, y))
            }
        }
        neighbors.remove(Point2D(point.x - 2, point.y - 2))
        neighbors.remove(Point2D(point.x + 2, point.y - 2))
        neighbors.remove(Point2D(point.x - 2, point.y + 2))
        neighbors.remove(Point2D(point.x + 2, point.y + 2))
        return neighbors.all { this[it] in listOf('#', '@') }
    }
}
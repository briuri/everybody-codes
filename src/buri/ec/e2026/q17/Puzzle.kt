package buri.ec.e2026.q17

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.countSteps
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(21, 1)
        assertRun(154, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(94, 1)
        assertRun(6214, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(31, 1)
        assertRun(2750, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val pathfinder = Pathfinder<Point2D<Int>> { current ->
            grid.getNeighbors(current).filter { grid[it] != '.' }
        }
        val start = Point2D(grid.xRange.first, grid.yRange.first)
        val end = Point2D(grid.xRange.last, grid.yRange.last)
        val points = grid.filter { it == '#' }.toSet()
                
        if (part.isOne()) {
            return pathfinder.exploreFrom(start).countSteps(start, end)
        }        
        if (part.isTwo()) {
            return points.sumOf { it.getNeighbors().count { it2 -> it2 !in points } }
        }
        
        // Part 3
        val gridOffsets = setOf(    // N S W E
            Pair(Point2D(0, -1), Point2D(1, 0)),
            Pair(Point2D(0, 1), Point2D(1, 2)),
            Pair(Point2D(-1, 0), Point2D(0, 1)),
            Pair(Point2D(1, 0), Point2D(2, 1)),
        )
        val bigGrid = Grid(grid.width * 3, grid.height * 3, '.')
        val bigStart = Point2D(bigGrid.xRange.first, bigGrid.yRange.first)
        val bigEnd = Point2D(bigGrid.xRange.last, bigGrid.yRange.last)
        for (y in grid.yRange) {
            for (x in grid.xRange) {
                if (grid[x, y] == '#') {
                    bigGrid[x * 3, y * 3] = '#'
                    bigGrid[x * 3, y * 3 + 2] = '#'
                    bigGrid[x * 3 + 2, y * 3] = '#'
                    bigGrid[x * 3 + 2, y * 3 + 2] = '#'                    
                    for ((original, offset) in gridOffsets) {
                        val next = Point2D(x + original.x, y + original.y)
                        if (!grid.isInBounds(next) || grid[next] == '.') {
                            bigGrid[x * 3 + offset.x, y * 3 + offset.y] = 'x'
                        }
                    }
                }
            }
        }
        val bigPathfinder = Pathfinder<Point2D<Int>> { current ->
            bigGrid.getNeighbors(current).filter { bigGrid[it] != '.' }
        }
        
        val cameFrom = bigPathfinder.exploreFrom(start)
        var steps = 0
        var current: Point2D<Int>? = bigEnd
        while (current != null) {
            if (bigGrid[current] == 'x') {
                steps++
            }
            if (current == bigStart) {
                break
            }
            current = cameFrom[current]
        }        
        println(cameFrom.countSteps(bigStart, bigEnd))
        return steps
    }
}
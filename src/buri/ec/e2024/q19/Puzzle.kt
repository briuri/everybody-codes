package buri.ec.e2024.q19

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
        assertRun("WIN", 1)
        assertRun("6948379425631369", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("VICTORY", 1)
        assertRun("9345391356337546", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("6742181787958513", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val instructions = Instructions(input.first())
        var grid = Grid.fromCharInput(input.drop(2))
        val cache = mutableListOf<ArrayDeque<Point2D<Int>>>()
        for (y in 1..<grid.yRange.last) {
            for (x in 1..<grid.xRange.last) {
                val rotation = instructions.next()
                grid.rotate(cache, Point2D(x, y), rotation)
            }
        }
        if (!part.isOne()) {
            val destinations = mutableMapOf<Point2D<Int>, Point2D<Int>>()
            for (deque in cache) {
                destinations[deque.first()] = deque.last()
            }
            val cachedSteps = if (part.isTwo()) 100 else 500
            cache.grow(destinations, cachedSteps - 1)
            grid = Grid.fromCharInput(input.drop(2))

            val cycles = if (part.isTwo()) 1 else 1_048_576_000 / cachedSteps
            repeat(cycles) {
                grid = apply(cache, grid)
            }
        }
        return grid.getCode()
    }

    /**
     * Finds the labeled code in the grid.
     */
    private fun Grid<Char>.getCode(): String {
        val left = this.filter { it == '>' }.first()
        val right = this.filter { it == '<' }.first()
        var string = ""
        for (x in (left.x + 1)..<right.x) {
            string += this[Point2D(x, left.y)]
        }
        return string
    }

    /**
     * 012
     * 3 4
     * 567
     */
    private fun Grid<Char>.rotate(cache: MutableList<ArrayDeque<Point2D<Int>>>, point: Point2D<Int>, rotation: Char) {
        val neighbors = this.getNeighbors(point, true)
        val temp = this[neighbors[0]]
        val cacheMap = cache.getDeques(neighbors)
        if (rotation == 'L') {
            cacheMap[neighbors[0]]!!.addLast(neighbors[3])
            cacheMap[neighbors[1]]!!.addLast(neighbors[0])
            cacheMap[neighbors[2]]!!.addLast(neighbors[1])
            cacheMap[neighbors[3]]!!.addLast(neighbors[5])
            cacheMap[neighbors[4]]!!.addLast(neighbors[2])
            cacheMap[neighbors[5]]!!.addLast(neighbors[6])
            cacheMap[neighbors[6]]!!.addLast(neighbors[7])
            cacheMap[neighbors[7]]!!.addLast(neighbors[4])
            this[neighbors[0]] = this[neighbors[1]]
            this[neighbors[1]] = this[neighbors[2]]
            this[neighbors[2]] = this[neighbors[4]]
            this[neighbors[4]] = this[neighbors[7]]
            this[neighbors[7]] = this[neighbors[6]]
            this[neighbors[6]] = this[neighbors[5]]
            this[neighbors[5]] = this[neighbors[3]]
            this[neighbors[3]] = temp
        } else {
            cacheMap[neighbors[0]]!!.addLast(neighbors[1])
            cacheMap[neighbors[1]]!!.addLast(neighbors[2])
            cacheMap[neighbors[2]]!!.addLast(neighbors[4])
            cacheMap[neighbors[3]]!!.addLast(neighbors[0])
            cacheMap[neighbors[4]]!!.addLast(neighbors[7])
            cacheMap[neighbors[5]]!!.addLast(neighbors[3])
            cacheMap[neighbors[6]]!!.addLast(neighbors[5])
            cacheMap[neighbors[7]]!!.addLast(neighbors[6])
            this[neighbors[0]] = this[neighbors[3]]
            this[neighbors[3]] = this[neighbors[5]]
            this[neighbors[5]] = this[neighbors[6]]
            this[neighbors[6]] = this[neighbors[7]]
            this[neighbors[7]] = this[neighbors[4]]
            this[neighbors[4]] = this[neighbors[2]]
            this[neighbors[2]] = this[neighbors[1]]
            this[neighbors[1]] = temp
        }
    }

    /**
     * Extends the cache ahead some number of cycles.
     */
    private fun MutableList<ArrayDeque<Point2D<Int>>>.grow(destinations: Map<Point2D<Int>, Point2D<Int>>, times: Int) {
        repeat(times) {
            val ends = this.map { it.last() }
            val cacheMap = this.getDeques(ends)
            for (end in ends) {
                cacheMap[end]!!.addLast(destinations[end]!!)
            }
        }
    }

    /**
     * Performs all the moves in the cache at once.
     */
    private fun apply(cache: MutableList<ArrayDeque<Point2D<Int>>>, grid: Grid<Char>): Grid<Char> {
        val newGrid = grid.copy()
        for (deque in cache) {
            newGrid[deque.last()] = grid[deque.first()]
        }
        return newGrid
    }

    /**
     * Makes sure a deque exists for the points.
     */
    private fun MutableList<ArrayDeque<Point2D<Int>>>.getDeques(points: List<Point2D<Int>>): Map<Point2D<Int>, ArrayDeque<Point2D<Int>>> {
        val map = mutableMapOf<Point2D<Int>, ArrayDeque<Point2D<Int>>>()
        for (point in points) {
            var deque = this.firstOrNull { it.last() == point }
            if (deque == null) {
                deque = ArrayDeque()
                deque.add(point)
                this.add(deque)
            }
            map[point] = deque
        }
        return map
    }
}

data class Instructions(val line: String) {
    var position = 0
    fun next(): Char {
        val next = line[position]
        position++
        if (position == line.length) {
            position = 0
        }
        return next
    }
}
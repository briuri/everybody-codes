package buri.ec.e2025.q17

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
        assertRun(1573, 1)
        assertRun(1573, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1090, 1)
        assertRun(67275, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(592, 1)
        assertRun(330, 2)
        assertRun(3180, 3)
        assertRun(46449, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val maxTime = maxOf(grid.xRange.last, grid.yRange.last) / 2 + 1
        val volcano = grid.filter { it == '@' }.first()
        if (part.isOne()) {
            var sum = 0
            for (y in grid.yRange) {
                for (x in grid.xRange) {
                    val point = Point2D(x, y)
                    if (point != volcano && volcano.inRange(point, 10)) {
                        sum += grid[point].digitToInt()
                    }
                }
            }
            return sum
        } else if (part.isTwo()) {
            val destruction = mutableListOf<Int>()
            destruction.add(0)
            for (radius in 1..maxTime) {
                var sum = 0
                for (y in grid.yRange) {
                    for (x in grid.xRange) {
                        val point = Point2D(x, y)
                        if (point != volcano && volcano.inRange(point, radius)) {
                            sum += grid[point].digitToInt()
                        }
                    }
                }
                destruction.add(sum)
            }
            for (i in destruction.lastIndex downTo 1) {
                destruction[i] = destruction[i] - destruction[i - 1]
            }
            val most = destruction.max()
            return destruction.indexOf(most) * most
        } else {
            val increment = 30
            val start = grid.filter { it == 'S' }.first()
            grid[start] = '0'

            // Get all destroyed points at each possible radius.
            val destroyed = mutableListOf<Set<Point2D<Int>>>()
            destroyed.add(setOf(volcano))
            for (radius in 1..maxTime) {
                val set = mutableSetOf<Point2D<Int>>()
                for (y in grid.yRange) {
                    for (x in grid.xRange) {
                        val point = Point2D(x, y)
                        if (volcano.inRange(point, radius)) {
                            set.add(point)
                        }
                    }
                }
                destroyed.add(set)
            }

            for ((radius, destroyedAtRadius) in destroyed.withIndex()) {
                if (radius == 0) {
                    continue
                }
                val seconds = (radius + 1) * increment
                val costsLeft = exploreWithCosts(grid, destroyedAtRadius, start, true)
                val costsRight = exploreWithCosts(grid, destroyedAtRadius, start, false)

                var bestRun = Int.MAX_VALUE
                for (y in destroyedAtRadius.maxOf { it.y } + 1..grid.yRange.last) {
                    val point = Point2D(start.x, y)
                    val left = costsLeft[point.toString()]!!
                    // Don't double-count the midpoint.
                    val right = costsRight[point.toString()]!! - grid[point].digitToInt()
                    val run = left + right
                    bestRun = bestRun.coerceAtMost(run)
                }
                if (bestRun < seconds) {
                    return bestRun * radius
                }
            }
            throw Exception("Could not encircle the volcano.")
        }
    }

    fun exploreWithCosts(
        grid: Grid<Char>,
        destroyed: Set<Point2D<Int>>,
        start: Point2D<Int>,
        isLeft: Boolean
    ): Map<String, Int> {
        val frontier = ArrayDeque<Pair<Point2D<Int>, Int>>()
        frontier.add(Pair(start, 0))
        val costMap = mutableMapOf<String, Int>()

        var current: Pair<Point2D<Int>, Int>?
        while (frontier.isNotEmpty()) {
            current = frontier.removeFirst()
            val key = current.first.toString()
            if (key !in costMap.keys || costMap[key]!! > current.second) {
                costMap[key] = current.second
                for (next in grid.getNeighbors(current.first).filter { it !in destroyed }) {
                    // Don't let left track go too far right.
                    if (isLeft && next.x > start.x) {
                        continue
                    }
                    // Don't let right track go too far left. Picked 3 after visual inspection of area around S.
                    if (!isLeft && next.x < start.x - 3) {
                        continue
                    }
                    frontier.add(Pair(next, current.second + grid[next].digitToInt()))
                }
            }
        }
        return costMap
    }

    fun Point2D<Int>.inRange(point: Point2D<Int>, radius: Int): Boolean {
        // (Xv - Xc) * (Xv - Xc) + (Yv - Yc) * (Yv - Yc) <= R * R
        return ((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y)) <= (radius * radius)
    }
}
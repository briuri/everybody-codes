package buri.ec.y24.q12

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
        assertRun(13, true)
        assertRun(180, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(22, true)
        assertRun(20490, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(2, true)
        assertRun(0, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val groundY = grid.filter { it == '=' }.first().y
        val a = grid.filter { it == 'A' }.first()
        val b = Point2D(a.x, a.y - 1)
        val c = Point2D(a.x, a.y - 2)

        var rankingValue = 0
        for (target in grid.filter { it in listOf('T', 'H') }) {
            val hardScore = if (grid[target] == 'H') 2 else 1
            val tries = mutableSetOf<Pair<Int, Int>?>()
            tries.add(grid.findPower(a, target, groundY))
            tries.add(grid.findPower(b, target, groundY))
            tries.add(grid.findPower(c, target, groundY))
            val (segment, power) = tries.filterNotNull().first()
            rankingValue += segment * power * hardScore
        }
        return rankingValue
    }

    /**
     * Returns the lowest possible power that can strike the target from the starting point. Returns null
     * if the catapult cannot strike at all.
     */
    private fun Grid<Char>.findPower(start: Point2D<Int>, target: Point2D<Int>, groundY: Int): Pair<Int, Int>? {
        val segment = when (this[start]) {
            'A' -> 1
            'B' -> 2
            else -> 3
        }
        for (power in 1..40) {
            val path = mutableSetOf<Point2D<Int>>()
            var current = start
            for (i in 0..<power) {
                current = current.copy(x = current.x + 1, y = current.y - 1)
                path.add(current)
            }
            for (i in 0..<power) {
                current = current.copy(x = current.x + 1)
                path.add(current)
            }
            for (i in 0..<power) {
                current = current.copy(x = current.x + 1, y = current.y + 1)
                path.add(current)
            }
            while (current.y < groundY) {
                current = current.copy(x = current.x + 1, y = current.y + 1)
                path.add(current)
            }
            if (target in path) {
                return Pair(segment, power)
            }
        }
        return null
    }
}
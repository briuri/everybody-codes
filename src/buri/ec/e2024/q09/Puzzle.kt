package buri.ec.e2024.q09

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test
import kotlin.math.abs

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(10, 1)
        assertRun(13127, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(10, 1)
        assertRun(5233, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(10449, 1)
        assertRun(149734, 0, true)
    }

    private val cache = mutableMapOf<Int, Int>()

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val stamps = when (part) {
            Part.ONE -> listOf(10, 5, 3, 1)
            Part.TWO -> listOf(30, 25, 24, 20, 16, 15, 10, 5, 3, 1)
            else -> listOf(101, 100, 75, 74, 50, 49, 38, 37, 30, 25, 24, 20, 16, 15, 10, 5, 3, 1)
        }
        val sparkBalls = input.map { it.toInt() }
        var totalBugs = 0

        for (ball in sparkBalls) {
            if (!part.isThree()) {
                totalBugs += findMinBugs(ball, stamps)
            } else {
                var testBugs = Int.MAX_VALUE
                val range = (1..(ball / 2))
                for (i in range.filter { abs(ball - 2 * it) <= 100 }) {
                    val first = findMinBugs(i, stamps)
                    val second = findMinBugs(ball - i, stamps)
                    testBugs = testBugs.coerceAtMost(first + second)
                }
                totalBugs += testBugs
            }
        }
        return totalBugs
    }

    private fun findMinBugs(remaining: Int, stamps: List<Int>): Int {
        if (remaining == 0) {
            return 0
        }
        if (remaining !in cache) {
            var bugs = Int.MAX_VALUE
            for (stamp in stamps.filter { it <= remaining }) {
                val localBugs = 1 + findMinBugs(remaining - stamp, stamps)
                bugs = bugs.coerceAtMost(localBugs)
            }
            cache[remaining] = bugs
        }
        return cache[remaining]!!
    }
}
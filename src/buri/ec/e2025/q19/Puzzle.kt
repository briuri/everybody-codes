package buri.ec.e2025.q19

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
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
        assertRun(24, 1)
        assertRun(56, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(22, 1)
        assertRun(805, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(4806046, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val gaps = mutableMapOf<Int, MutableList<Gap>>()
        for (line in input) {
            val numbers = line.extractInts()
            gaps.putIfAbsent(numbers[0], mutableListOf())
            gaps[numbers[0]]!!.add(Gap(numbers[0], numbers[1]..<numbers[1] + numbers[2]))
        }

        var allStarts = mutableSetOf<PointCost>()
        allStarts.add(PointCost(Point2D(0, 0), 0))
        while (allStarts.any { it.point.x != gaps.keys.last() }) {
            val newStarts = mutableSetOf<PointCost>()
            for (start in allStarts) {
                val nextGapKey = gaps.keys.firstOrNull { it > start.point.x }
                if (nextGapKey != null) {
                    for (nextGap in gaps[nextGapKey]!!) {
                        for (cost in nextGap.getPossibleEnds(start.point)) {
                            newStarts.add(PointCost(cost.point, cost.cost + start.cost))
                        }
                    }
                } else {
                    newStarts.add(start)
                }
            }
            allStarts = newStarts
        }
        return allStarts.minOfOrNull { it.cost }!!
    }
}

data class PointCost(val point: Point2D<Int>, val cost: Int) {
    override fun toString(): String {
        return "($point)=$cost"
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }
}

data class Gap(val x: Int, val yRange: IntRange) {

    fun getPossibleEnds(start: Point2D<Int>): List<PointCost> {
        val costs = mutableListOf<PointCost>()
        val time = x - start.x
        for (flaps in 0..time) {
            val coasts = time - flaps
            val y = start.y + flaps - coasts
            if (y in yRange) {
                costs.add(PointCost(Point2D(x, y), flaps))
            }
        }
        return costs
    }
}
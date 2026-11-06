package buri.ec.e2026.q09

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(27, 1)
        assertRun(219, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(42, 1)
        assertRun(15503, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val plants = mutableListOf<Pair<Int, Int>>()
        for (line in input) {
            val numbers = line.extractInts()
            val growth = numbers[1]
            val limit = numbers[2]
            plants.add(Pair(growth, limit))
        }

        return plants.sumOf { grow(part, it) }
    }

    fun grow(part: Part, plant: Pair<Int, Int>): Int {
        val (growth, limit) = plant

        val map = mutableMapOf<Int, Int>()
        for (i in 0..<growth) {
            map[i] = 0
        }

        var time = 1
        map[1] = 1
        // Slot 0 stores the split points.
        while (map.getLimitCondition(part) < limit) {
            time++
            // Grow each kind of branch 1 bigger
            for (i in growth downTo 2) {
                map[i] = map[i - 1]!!
            }
            map[1] = 0
            // A split results in 2 new branches half the length, and 1 new branch size 1.
            val numSplits = map[growth]!!
            if (numSplits != 0) {
                val nextLength = (growth - 1) / 2
                map[growth] = 0
                map[0] = map[0]!! + numSplits
                map[nextLength] = map[nextLength]!! + (numSplits * 2)
                map[1] = map[1]!! + numSplits
            }
        }
        return time
    }

    fun Map<Int, Int>.getLimitCondition(part: Part): Int {
        if (part.isOne()) {
            return (this[0]!! + this.map { it.key * it.value }.sum())
        }
        return this[0]!! + 1
    }
}

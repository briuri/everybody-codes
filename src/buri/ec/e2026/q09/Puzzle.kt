package buri.ec.e2026.q09

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractLongs
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
        assertRun(849046170062769, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val plants = mutableListOf<Pair<Long, Long>>()
        for (line in input) {
            val numbers = line.extractLongs()
            val growth = numbers[1]
            val limit = numbers[2]
            plants.add(Pair(growth, limit))
        }

        if (part.isOne()) {
            return plants.sumOf { grow(it) }
        }
        return plants.sumOf { simulateGrow(it) }
    }

    fun grow(plant: Pair<Long, Long>): Int {
        val (growth, limit) = plant

        val branches = mutableMapOf<Long, Long>()
        for (i in 0..<growth) {
            branches[i] = 0
        }

        var time = 1
        branches[1] = 1
        // Slot 0 stores the split points.
        while (branches[0]!! + branches.map { it.key * it.value }.sum() < limit) {
            time++
            // Grow each kind of branch 1 bigger
            for (i in growth downTo 2) {
                branches[i] = branches[i - 1]!!
            }
            branches[1] = 0
            // A split results in 2 new branches half the length, and 1 new branch size 1.
            val numSplits = branches[growth]!!
            if (numSplits > 0) {
                val nextLength = growth / 2
                branches[growth] = 0
                branches[0] = branches[0]!! + numSplits
                branches[nextLength] = branches[nextLength]!! + (numSplits * 2)
                branches[1] = branches[1]!! + numSplits
            }
        }
        return time
    }

    fun simulateGrow(plant: Pair<Long, Long>): Long {
        val (growth, limit) = plant
            
        var branches = sortedMapOf<Long, Long>()
        branches[1] = 1
        var time = 1L
        var leaves = 1L
        while (leaves < limit) {
            // Jump to the next time a split occurs.
            val timeSkip = growth - branches.keys.last()
            val nextBranches = sortedMapOf<Long, Long>()
            for ((length, count) in branches) {
                nextBranches[length + timeSkip] = count
            }
            // A split results in 2 new branches half the length, and 1 new branch size 1.
            val numSplits = nextBranches[growth]!!
            val nextLength = growth / 2
            nextBranches.putIfAbsent(1, 0)
            nextBranches.putIfAbsent(nextLength, 0)
            nextBranches[nextLength] = nextBranches[nextLength]!! + (numSplits * 2)
            nextBranches[1] = nextBranches[1]!! + numSplits
            leaves += numSplits
            time += timeSkip
            nextBranches.remove(growth)
            branches = nextBranches
        }     
        return time
    }
}

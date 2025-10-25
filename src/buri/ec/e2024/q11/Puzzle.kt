package buri.ec.e2024.q11

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(8, true)
        assertRun(42, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(332461, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(268815, true)
        assertRun(738270514446, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rules = mutableMapOf<String, List<String>>()
        for (line in input) {
            val id = line.split(":")[0]
            rules[id] = line.split(":")[1].split(",").toList()
        }

        if (part.isOne()) {
            return getCount("A", 4, rules)
        }
        if (part.isTwo()) {
            return getCount("Z", 10, rules)
        }
        // Part 3
        val tries = mutableListOf<Long>()
        for (key in rules.keys) {
            tries.add(getCount(key, 20, rules))
        }
        return tries.max() - tries.min()
    }

    private fun getCount(start: String, days: Int, rules: Map<String, List<String>>): Long {
        var counts = mutableMapOf<String, Long>()
        counts[start] = 1
        repeat(days) {
            val newCounts = mutableMapOf<String, Long>()
            for (key in counts.keys) {
                val count = counts[key]!!
                for (next in rules[key]!!) {
                    newCounts.putIfAbsent(next, 0)
                    newCounts[next] = newCounts[next]!! + count
                }
            }
            counts = newCounts
        }
        return counts.values.sum()
    }
}
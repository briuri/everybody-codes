package buri.ec.e2026.q14

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
import org.junit.Test
import kotlin.math.min

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(67, 1)
        assertRun(280, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(12, 1)
        assertRun(63, 2)
        assertRun(475, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, 1)
        assertRun(21, 2)
        assertRun(1860, 0, true)
    }

    // This char is not used in the data.
    val outside = '`'

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val zones = mutableSetOf<Char>()
        zones.add(outside)

        val costs = mutableMapOf<String, Int>()
        for (y in grid.yRange) {
            for (x in grid.xRange) {
                val pos = Point2D(x, y)
                val zone = grid[pos]
                zones.add(zone)
                for (neighbor in pos.getNeighbors()) {
                    val key = if (!grid.isInBounds(neighbor)) {
                        getSortedKey(zone, outside)
                    } else {
                        getSortedKey(zone, grid[neighbor])
                    }
                    if (key[0] != key[1]) {
                        costs.putIfAbsent(key, 0)
                        costs[key] = costs[key]!! + 1
                    }
                }
            }
        }
        // Halve the internal edges that were counted twice.
        for ((key, value) in costs) {
            if (!key.contains(outside)) {
                costs[key] = value / 2
            }
        }

        if (part.isOne()) {
            return costs.values.sum()
        }
        if (part.isTwo()) {
            return getMinBindings(zones, costs)
        }
        // Part 3

        var bestMin = Int.MAX_VALUE
        for (free1 in zones.filter { it != outside }) {
            for (free2 in zones.filter { it != outside && it != free1 }) {
                val copiedCosts = costs.toMutableMap()
                for (key in copiedCosts.keys) {
                    if (key.contains(free1) || key.contains(free2)) {
                        copiedCosts[key] = 0
                    }
                }
                bestMin = min(bestMin, getMinBindings(zones, copiedCosts))
            }
        }
        return bestMin
    }

    fun getMinBindings(zones: Set<Char>, costs: Map<String, Int>): Int {
        val parents = mutableMapOf<Char, Char>()
        zones.forEach { parents[it] = it }
        var bindings = 0
        for ((key, cost) in costs.entries.sortedBy { it.value }) {
            val parent1 = parents.findParent(key[0])
            val parent2 = parents.findParent(key[1])
            if (parent1 != parent2) {
                val mergeParent = parents.findParent(parent1)
                parents[mergeParent] = parent2
                bindings += cost
            }
        }
        return bindings
    }

    fun Map<Char, Char>.findParent(child: Char): Char {
        if (this[child] == child) {
            return child
        }
        return this.findParent(this[child]!!)
    }

    fun getSortedKey(zone1: Char, zone2: Char): String {
        val sortedZones = listOf(zone1, zone2).sorted()
        return sortedZones.joinToString("")
    }
}

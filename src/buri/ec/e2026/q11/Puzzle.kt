package buri.ec.e2026.q11

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.Pathfinder
import buri.ec.common.countSteps
import buri.ec.common.extractInts
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(60, 1)
        assertRun(1790, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(6, 1)
        assertRun(2604, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(854, 1)
        assertRun(9634, 2)
        assertRun(28670, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val portals = mutableSetOf<Pair<Int, Int>>()
        val numPortals = input[0].extractInts()[0]
        for (i in 1..numPortals) {
            val numJumps = if (part.isOne()) {
                1
            } else {
                3
            }
            for (jump in 1..numJumps) {
                var next = i + jump
                if (next > numPortals) {
                    next -= numPortals
                }
                portals.add(Pair(i, next))
            }
        }
        // Generate initial strain
        val strain = getStrain(portals)
        
        // Add fixed portals
        for (line in input.drop(2)) {
            val numbers = line.extractInts()
            portals.add(Pair(numbers[0], numbers[1]))
        }
        val originalPortals = portals.toSet()

        if (part.isOne()) {
            return strain - getStrain(portals)
        }

        // Set up the possible new portals between crystals.
        val slices = mutableSetOf<List<Int>>()
        if (part.isTwo()) {
            for (crystal1 in 1..numPortals) {
                for (crystal2 in 1..numPortals) {
                    slices.add(listOf(crystal1, crystal2))
                }
            }
        }
        else {
            val spacing = numPortals / 8
            for (i in 1..spacing + 1) {
                val list = mutableListOf<Int>()
                var next = i
                while (next <= numPortals) {
                    list.add(next)
                    next += spacing
                }
                slices.add(list)
            }
        }
        
        // Iterate through the possible connections to find the best strain.
        var minStrain = Int.MAX_VALUE
        for (slice in slices) {
            for (pairing in generatePortalPairs(slice)) {
                portals.clear()
                portals.addAll(originalPortals)
                portals.addAll(pairing)
                minStrain = min(minStrain, getStrain(portals))
            }
        }
        return strain - minStrain
    }

    fun getStrain(portals: Set<Pair<Int, Int>>): Int {
        val pathfinder = Pathfinder<Int> { current ->
            portals.filter { it.first == current }.map { it.second } +
                    portals.filter { it.second == current }.map { it.first }
        }
        val numPortals = max(portals.maxOf { it.first }, portals.maxOf { it.second })
        var strain = 0
        for (start in 1..numPortals) {
            val cameFrom = pathfinder.exploreFrom(start)
            for (end in 1..numPortals) {
                if (start != end) {
                    strain += cameFrom.countSteps(start, end)
                }
            }
        }
        return strain
    }

    fun generatePortalPairs(portals: List<Int>): List<List<Pair<Int, Int>>> {
        if (portals.size % 2 != 0) {
            return emptyList()
        }
        if (portals.isEmpty()) {
            return listOf(emptyList())
        }
        val pairings = mutableListOf<List<Pair<Int, Int>>>()
        val first = portals[0]
        for (i in 1 until portals.size) {
            val currentPair = Pair(first, portals[i])
            val remaining = portals.filterIndexed { index, _ -> index != 0 && index != i }
            for (subPairing in generatePortalPairs(remaining)) {
                pairings.add(listOf(currentPair) + subPairing)
            }
        }        
        return pairings
    }
}
package buri.ec.e2026.q07

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
        assertRun(102, 1)
        assertRun(282, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1316, 1)
        assertRun(1214633, 2)
        assertRun(115447204, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(187, 1)
        assertRun(34777023, 2)
        assertRun(23043878, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val clouds = mutableListOf<Cloud>()
        for (line in input) {
            val tokens = line.split(", ")
            val id = tokens[0].split("=")[1].toInt()
            val initialEnergy = tokens[1].split("=")[1].toLong()
            val nextIds = tokens[2].split("=")[1].split(",").map { it.toInt() }
            clouds.add(Cloud(id, initialEnergy, nextIds))
        }
        clouds.forEach { it.load(clouds) }

        val startingClouds = mutableSetOf<Cloud>()
        if (!part.isThree()) {
            startingClouds.add(clouds.maxBy { it.nextIds.size })
        }
        else {
            startingClouds.addAll(clouds)
        }

        val threshold = if (input.size < 100) { 100L } else { 1_000_000L }
        val results = mutableMapOf<Cloud, Long>()
        for (maxCloud in startingClouds) {
            clouds.forEach { it.reset() }
            var charges = mutableListOf<Triple<Cloud, Long, Long>>()
            charges.add(Triple(maxCloud, 1, if (part.isOne()) { 5 } else { 10 }))
            while (charges.isNotEmpty()) {
                val newCharges = mutableListOf<Triple<Cloud, Long, Long>>()
                for ((cloud, times, charge) in charges) {
                    merge(newCharges, cloud.zap(times, charge))
                }
                if (part.isThree()) {
                    for (cloud in clouds) {
                        merge(newCharges, cloud.explode(threshold))
                    }
                }
                charges = newCharges
            }
            results[maxCloud] = clouds.sumOf { it.energy }
        }
        return results.maxOf { it.value }
    }
    
    fun merge(charges: MutableList<Triple<Cloud, Long, Long>>, newCharges: List<Triple<Cloud, Long, Long>>) {
        for (charge in newCharges) {
            if (!charges.any { it.first == charge.first }) {
                charges.add(charge)
            }
            else {
                val existing = charges.firstOrNull { it.first == charge.first && it.third == charge.third }
                if (existing == null) {
                    charges.add(charge)
                }
                else {
                    charges.remove(existing)
                    charges.add(Triple(existing.first, existing.second + charge.second, existing.third))
                }
            }
        }
    }
}

data class Cloud(val id: Int, val initialEnergy: Long, val nextIds: List<Int>) {
    val nextClouds = mutableListOf<Cloud>()
    fun load(clouds: List<Cloud>) {
        nextClouds.addAll(clouds.filter { it.id in nextIds })
    }
    
    var energy = initialEnergy
    var exploded = false

    override fun toString() = "Cloud $id"
    
    fun reset() {
        energy = initialEnergy
        exploded = false
    }

    fun zap(times: Long, charge: Long): List<Triple<Cloud, Long, Long>> {
        if (!exploded) {
            energy += (times * charge)
            if (charge > 1) {
                return nextClouds.map { Triple(it, times, charge - 1) }
            }
        }
        return emptyList()
    }
    
    fun explode(threshold: Long): List<Triple<Cloud, Long, Long>> {
        if (energy >= threshold) {
            exploded = true
            energy = 0
            return nextClouds.map { Triple(it, 1, 10) }
        }
        return emptyList()
    }
}
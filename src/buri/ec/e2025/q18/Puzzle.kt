package buri.ec.e2025.q18

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractLongs
import org.junit.Test
import kotlin.math.pow

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(774, 1)
        assertRun(2056950, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(324, 1)
        assertRun(10225660993, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(946, 1)
        assertRun(134078, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val plants = mutableMapOf<Long, Plant>()
        val freeBranches = mutableListOf<Branch>()
        val freePlants = mutableListOf<Plant>()
        val tests = mutableListOf<List<Long>>()

        var i = 0
        while (i in input.indices) {
            var line = input[i]
            if (line.isNotEmpty()) {
                if (line.startsWith("Plant")) {
                    var numbers = line.extractLongs(false)
                    val plant = Plant(numbers[0], numbers[1])
                    plants[plant.id] = plant

                    i++
                    while (i in input.indices && input[i].isNotEmpty()) {
                        line = input[i].drop(2)
                        numbers = line.extractLongs(true)
                        if (line.startsWith("free")) {
                            val freeBranch = Branch(null, numbers[0])
                            plant.branches.add(freeBranch)
                            freeBranches.add(freeBranch)
                            freePlants.add(plant)
                        } else {
                            plant.branches.add(Branch(plants[numbers[0]]!!, numbers[1]))
                        }
                        i++
                    }
                } else {
                    tests.add(input[i].extractLongs(false))
                }
            }
            i++
        }
        val lastPlant = plants.values.last()

        if (part.isOne()) {
            return lastPlant.getEnergy()
        } else if (part.isTwo()) {
            var sum = 0L
            for (test in tests) {
                for ((index, energy) in test.withIndex()) {
                    freeBranches[index].thickness = energy
                }
                val energy = lastPlant.getEnergy()
                sum += energy
            }
            return sum
        } else {
            // Main input has plants that are always negative.
            val ignorePlants = mutableListOf<Plant>()
            for (freePlant in freePlants) {
                var hasPositive = false
                for (plant in plants.values) {
                    val branches = plant.branches.filter { it.to == freePlant }
                    if (branches.isNotEmpty()) {
                        hasPositive = hasPositive || branches.any { it.thickness > 0 }
                    }
                }
                if (!hasPositive) {
                    ignorePlants.add(freePlant)
                }
            }

            var max = 0L
            // Naive approach, try all test cases. Works for example (where plants can't be ignored).
            if (ignorePlants.isEmpty()) {
                val size = tests[0].size
                for (partialTest in 0 until 2.toDouble().pow(size).toInt()) {
                    val binary = Integer.toBinaryString(partialTest).padStart(size, '0')

                    var plantIndex = 0
                    var testIndex = 0
                    while (plantIndex in freePlants.indices) {
                        val plant = freePlants[plantIndex]
                        if (plant !in ignorePlants) {
                            plant.branches.first().thickness = binary[testIndex].digitToInt().toLong()
                            testIndex++
                        } else {
                            plant.branches.first().thickness = 0
                        }
                        plantIndex++
                    }
                    val energy = lastPlant.getEnergy()
                    max = max.coerceAtLeast(energy)
                }
            }
            // Only one case where a max is possible in the real input.
            else {
                for (plant in freePlants) {
                    plant.branches.first().thickness = if (plant in ignorePlants) {
                        0
                    } else {
                        1
                    }
                }
                max = lastPlant.getEnergy()
            }

            var sum = 0L
            for (test in tests) {
                for ((index, energy) in test.withIndex()) {
                    freeBranches[index].thickness = energy
                }
                val energy = lastPlant.getEnergy()
                if (energy > 0) {
                    sum += (max - energy)
                }
            }
            return sum
        }
    }
}

data class Plant(val id: Long, val thickness: Long) {
    val branches = mutableListOf<Branch>()

    fun getEnergy(): Long {
        val incoming = branches.sumOf { it.getEnergy() }
        return if (incoming < thickness) {
            0
        } else {
            incoming
        }
    }
}

data class Branch(val to: Plant?, var thickness: Long) {
    fun getEnergy(): Long {
        return if (to == null) {
            thickness
        } else {
            to.getEnergy() * thickness
        }
    }
}
package buri.ec.e2025.q09

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
        assertRun(414, true)
        assertRun(6512, isExample = false, toConsole = true)
    }

    @Test
    fun runPart2() {
        assertRun(1245, true)
        assertRun(317403, isExample = false, toConsole = true)
    }

    @Test
    fun runPart3() {
        assertRun(36, true)
        assertRun(36834, isExample = false, toConsole = true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val scales = mutableMapOf<Int, List<Char>>()
        for (line in input) {
            val tokens = line.split(":")
            scales[tokens[0].toInt()] = tokens[1].toCharArray().toList()
        }
        val childToParents = mutableMapOf<Int, List<Int>>()
        for (id in scales.keys) {
            childToParents[id] = findParents(scales, id)
        }

        if (!part.isThree()) {
            var sum = 0L
            for (childId in childToParents.keys) {
                val parentIds = childToParents[childId]!!
                if (parentIds.isNotEmpty()) {
                    var product = 1L
                    for (parentId in parentIds) {
                        var similarity = 0
                        for ((index, letter) in scales[childId]!!.withIndex()) {
                            if (scales[parentId]!![index] == letter) {
                                similarity++
                            }
                        }
                        product *= similarity
                    }
                    sum += product
                }
            }
            return sum
        }

        val families = mutableSetOf<MutableSet<Int>>()
        for (entry in childToParents.filter { it.value.isNotEmpty() }) {
            families.add(mutableSetOf(entry.key, entry.value.first(), entry.value.last()))
        }
        for (familyA in families) {
            for (familyB in families.filter { it != familyA }) {
                if (familyA.intersect(familyB).isNotEmpty()) {
                    familyA.addAll(familyB)
                    familyB.clear()
                }
            }
        }
        val maxFamilySize = families.maxOf { it.size }
        for (family in families) {
            if (family.size == maxFamilySize) {
                return family.sum()
            }
        }
        throw Exception("No largest family.")
    }

    fun findParents(scales: Map<Int, List<Char>>, childId: Int): List<Int> {
        val possibleParentIds = scales.keys.filter { it != childId }
        for (parentA in scales.filter { it.key in possibleParentIds }) {
            for (parentB in scales.filter { it.key in possibleParentIds && it.key != parentA.key }) {
                var isMatch = true
                for ((index, letter) in scales[childId]!!.withIndex()) {
                    isMatch = isMatch && (letter == parentA.value[index] || letter == parentB.value[index])
                }
                if (isMatch) {
                    return listOf(parentA.key, parentB.key)
                }
            }
        }
        return emptyList()
    }
}

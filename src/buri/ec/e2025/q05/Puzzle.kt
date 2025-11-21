package buri.ec.e2025.q05

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
        assertRun("581078", 1)
        assertRun("7685438387", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("77053", 1)
        assertRun("8994512994861", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("260", 1)
        assertRun("31769825", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val fishbones = mutableListOf<Fishbone>()
        for (line in input) {
            val tokens = line.split(":")
            val fishbone = Fishbone(tokens[0].toInt())
            for (number in tokens[1].extractInts()) {
                fishbone.add(number)
            }
            fishbones.add(fishbone)
        }
        if (part.isOne()) {
            return fishbones[0].quality().toString()
        } else if (part.isTwo()) {
            val qualities = fishbones.map { it.quality() }
            return (qualities.max() - qualities.min()).toString()
        }

        // Part Three
        fishbones.sortDescending()
        val checksum = fishbones.mapIndexed { index, it -> (index + 1) * it.id }.sum()
        return checksum.toString()
    }
}

data class Fishbone(val id: Int) : Comparable<Fishbone> {
    val lefts = mutableMapOf<Int, Int>()
    val rights = mutableMapOf<Int, Int>()
    val spines = mutableListOf<Int>()

    fun add(number: Int) {
        for ((index, spine) in spines.withIndex()) {
            if (number < spine && !lefts.contains(index)) {
                lefts[index] = number
                return
            }
            if (number > spine && !rights.contains(index)) {
                rights[index] = number
                return
            }
        }
        spines.add(number)
    }

    fun quality() = spines.joinToString("").toLong()

    fun complexQuality(): List<Long> {
        val list = mutableListOf<Long>()
        for ((index, spine) in spines.withIndex()) {
            val builder = StringBuilder()
            if (lefts.contains(index)) {
                builder.append(lefts[index]!!)
            }
            builder.append(spine)
            if (rights.contains(index)) {
                builder.append(rights[index]!!)
            }
            list.add(builder.toString().toLong())
        }
        return list
    }

    override fun compareTo(other: Fishbone): Int {
        var compare = quality().compareTo(other.quality())
        if (compare == 0) {
            val otherComplex = other.complexQuality()
            for ((index, value) in complexQuality().withIndex()) {
                compare = value.compareTo(otherComplex[index])
                if (compare != 0) {
                    break
                }
            }
        }
        if (compare == 0) {
            compare = id.compareTo(other.id)
        }
        return compare
    }
}
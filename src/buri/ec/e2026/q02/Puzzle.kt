package buri.ec.e2026.q02

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
        assertRun(122157, 1)
        assertRun(350457, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(122157, 1)
        assertRun(57423502323, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(122157, 1)
        assertRun(588028682493, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rules = mutableListOf<MutableMap<Int, MutableSet<Int>>>()
        for (line in input) {
            val xGreaterThanY = mutableMapOf<Int, MutableSet<Int>>()
            for (rule in line.split(" ")) {
                val left = rule[0].digitToInt()
                val right = rule[2].digitToInt()
                when (rule[1]) {
                    '<' -> {
                        if (xGreaterThanY[right] == null) {
                            xGreaterThanY[right] = mutableSetOf()
                        }
                        xGreaterThanY[right]!!.add(left)
                    }
                    else -> {
                        if (xGreaterThanY[left] == null) {
                            xGreaterThanY[left] = mutableSetOf()
                        }
                        xGreaterThanY[left]!!.add(right)
                    }
                }
            }
            rules.add(xGreaterThanY)
        }

        var sum = 0L
        for (xGreaterThanY in rules) {
            val allValues = xGreaterThanY.flatMap { (key, value) -> listOf(key) + value }.toSet()
            while (hasRepeatedSizes(xGreaterThanY.map { it.value.size })) {
                for (y in allValues) {
                    val xs = xGreaterThanY.filter { it.value.contains(y) }.keys
                    for (x in xs) {
                        xGreaterThanY.filter { it.value.contains(x) }.forEach { it.value.add(y) }
                    }
                }
            }
            val sortedKeys = xGreaterThanY.entries.sortedByDescending { it.value.size }.map { it.key }.toMutableList()
            val leftover = (allValues - xGreaterThanY.keys).first()
            sortedKeys.add(leftover)
            sum += sortedKeys.joinToString("").toLong()
        }
        return sum
    }

    fun hasRepeatedSizes(list: List<Int>): Boolean {
        val seen = mutableSetOf<Int>()
        for (item in list.sorted()) {
            if (!seen.add(item)) {
                return true
            }
        }
        return false
    }
}
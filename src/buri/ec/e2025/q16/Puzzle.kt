package buri.ec.e2025.q16

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
        assertRun(193, 1)
        assertRun(242, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(270, 1)
        assertRun(149991948288, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(94439495762954, 1)
        assertRun(96828737989005, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        if (part.isOne()) {
            val length = 90
            val wall = buildWall(input[0].split(",").map { it.toInt() }, length)
            return wall.sum()
        } else if (part.isTwo()) {
            val wall = input[0].split(",").map { it.toInt() }
            var product = 1L
            for (step in findSpell(wall)) {
                product *= step
            }
            return product
        } else {
            val totalBlocks = 202_520_252_025_000
            val wall = input[0].split(",").map { it.toInt() }
            val spell = findSpell(wall)

            var min = wall.size.toLong()
            var max = 100_000_000_000_000
            var mid = 0L
            while (min < max) {
                mid = (min + max) / 2
                if (getBlocksUsed(spell, mid) < totalBlocks) {
                    min = mid
                } else {
                    max = mid - 1
                }
            }
            return mid
        }
    }

    fun getBlocksUsed(spell: List<Int>, length: Long): Long {
        var count = 0L
        for (step in spell) {
            count += (length - (step - 1)) / step + 1
        }
        return count
    }

    fun buildWall(spell: List<Int>, length: Int): List<Int> {
        val wall = mutableListOf<Int>()
        repeat(length) {
            wall.add(0)
        }
        for (step in spell) {
            var x = -1
            while (true) {
                x += step
                if (x >= length) {
                    break
                }
                wall[x] = wall[x] + 1
            }
        }
        return wall
    }

    fun findSpell(rawWall: List<Int>): List<Int> {
        val wall = mutableListOf<Int>()
        wall.addAll(rawWall)
        val spell = mutableListOf<Int>()
        while (wall.sum() > 0) {
            for (i in wall.indices) {
                if (wall[i] != 0) {
                    spell.add(i + 1)
                    for (j in i..<wall.size step (i + 1)) {
                        wall[j] = (wall[j] - 1).coerceAtLeast(0)
                    }
                    break
                }
            }
        }
        return spell
    }
}
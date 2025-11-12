package buri.ec.e2.q03

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
        assertRun("844", true)
        assertRun("616", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("1,3,4,2", true)
        assertRun("5,2,7,9,6,8,3,1,4", false, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, true)
        assertRun(0, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val diceSeparator = input.indexOf("")
        val diceInput = if (diceSeparator == -1) {
            input
        } else {
            input.subList(0, diceSeparator)
        }
        val racetrack = if (diceSeparator == -1) {
            emptyList()
        } else {
            input.last().toCharArray().toList().map { it.digitToInt() }
        }

        val dice = mutableMapOf<Int, Die>()
        // 1: faces=[1,2,3,4,5,6] seed=7
        for (line in diceInput) {
            val numbers = line.extractInts(true)
            val faces = numbers.drop(1).dropLast(1)
            dice[numbers[0]] = Die(faces, numbers.last().toLong())
        }

        if (part.isOne()) {
            var points = 0
            while (points < 10_000) {
                val localPoints = dice.values.sumOf { it.roll() }
                points += localPoints
            }
            return dice.values.first().rollCount.toString()
        }

        // Simulate a race
        val simluationLength = 12_000
        val races = mutableMapOf<Int, MutableList<Int>>()
        for (id in dice.keys) {
            races[id] = mutableListOf<Int>()
            repeat(simluationLength) {
                races[id]!!.add(dice[id]!!.roll())
            }
        }

        // Figure out order.
        val finishesAt = mutableMapOf<Int, Int>()
        for (key in races.keys) {
            val race = races[key]!!
            var progress = racetrack
            while (progress.isNotEmpty()) {
                val next = race.removeFirst()
                if (progress.first() == next) {
                    progress = progress.drop(1)
                }
            }
            finishesAt[key] = simluationLength - race.size
        }
        return finishesAt.toList().sortedBy { it.second }.map { it.first }.joinToString(",")
    }
}

data class Die(val faces: List<Int>, val seed: Long) {
    var pulse = seed
    var rollCount = 0
    var currentFace = 0

    fun roll(): Int {
        rollCount++
        val spin = rollCount * pulse
        pulse = ((pulse + spin) % seed) + 1 + rollCount + seed
        currentFace = ((currentFace + spin) % faces.size).toInt()
        return faces[currentFace]
    }
}
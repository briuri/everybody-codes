package buri.ec.e2024.q16

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
        assertRun(">.- -.- ^,-", 1)
        assertRun("<:- ^_^ <:< <.>", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("280014668134", 1)
        assertRun("129567491554", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("627 128", 1)
        assertRun("572 64", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val wheels = mutableListOf<Wheel>()
        val deltas = input.first().extractInts()
        for (i in deltas.indices) {
            val faces = mutableListOf<String>()
            for (line in input.drop(2)) {
                val index = i * 4
                if (index < line.length && line[index] != ' ') {
                    faces.add(line.substring(index, index + 3))
                }
            }
            wheels.add(Wheel(deltas[i], faces))
        }

        if (part.isOne()) {
            repeat(100) {
                wheels.forEach { it.spin() }
            }
            return wheels.joinToString(" ") { it.getFace() }
        }
        if (part.isTwo()) {
            val totalPulls = 202_420_242_024
            var score = 0L
            var pull = 0L
            while (pull < totalPulls) {
                pull++
                wheels.forEach { it.spin() }
                score += wheels.getScore()
                if (wheels.all { it.position == 0 }) {
                    val cycleLength = pull
                    val remaining = totalPulls - pull
                    val cyclesToSkip = remaining / cycleLength
                    pull += cyclesToSkip * cycleLength
                    score += cyclesToSkip * score
                }
            }
            return score.toString()
        }

        // Part THREE
        val pulls = 256
        val startingPositions = wheels.map { it.position }
        val min = getScore(mutableMapOf(), wheels, pulls, startingPositions, true)
        val max = getScore(mutableMapOf(), wheels, pulls, startingPositions, false)
        return "$max $min"
    }

    private fun getScore(
        cache: MutableMap<String, Long>,
        wheels: List<Wheel>,
        pulls: Int,
        positions: List<Int>,
        isMin: Boolean
    ): Long {
        if (pulls == 0) {
            return 0L
        }
        val key = "$pulls $positions $isMin"
        if (key !in cache.keys) {
            var score = if (isMin) Long.MAX_VALUE else Long.MIN_VALUE
            for (leftShift in listOf(-1, 0, 1)) {
                wheels.forEachIndexed { index, wheel ->
                    wheel.position = positions[index]
                    wheel.spin(leftShift)
                }
                val nextPositions = wheels.map { it.position }
                val nextScore = wheels.getScore() + getScore(cache, wheels, pulls - 1, nextPositions, isMin)
                score = if (isMin) {
                    score.coerceAtMost(nextScore)
                } else {
                    score.coerceAtLeast(nextScore)
                }
            }
            cache[key] = score
        }
        return cache[key]!!
    }

    private fun List<Wheel>.getScore(): Long {
        val frequency = mutableMapOf<Char, Int>()
        for (c in this.joinToString("") { it.getMaskedFace() }) {
            frequency.putIfAbsent(c, 0)
            frequency[c] = frequency[c]!! + 1
        }
        var score = 0L
        for (match in frequency.filter { it.value >= 3 }.values) {
            score += 1 + (match - 3)
        }
        return score
    }
}

data class Wheel(val delta: Int, val faces: List<String>) {
    var position = 0

    fun getFace() = faces[position]

    fun getMaskedFace() = "${getFace().first()}${getFace().last()}"

    /**
     * Spins the wheel some amount, including any shift from pulling the left lever.
     */
    fun spin(leftShift: Int = 0) {
        position += leftShift
        position += delta
        while (position >= faces.size) {
            position -= faces.size
        }
    }
}
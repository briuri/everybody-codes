package buri.ec.e2024.q07

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
        assertRun("BDCA", true)
        assertRun("GFDKAJIHB", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("DCBA", true)
        assertRun("CKJBIGDEH", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("6421", false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        // I modified each input file to store the track flattened on the first line. The S square is at the end and
        // represented by =. For Part One, the track is a single square that never overrides the plan (=).
        val track = input.first().toInts()  // A single square  in Part One.

        // In Part 3, I did a one-time generation of all possible permutations of +++++---===
        // that weren't the same as the rival knight's (9239 total) and stored in input file.
        val plans = mutableMapOf<String, List<Int>>()
        for (line in input.drop(1)) {
            val id = line.split(":")[0]
            plans[id] = line.split(":")[1].toInts()
        }

        val loops = if (part.isThree()) 2024 else 10
        val steps = track.size * loops
        val scores = mutableMapOf<String, Long>()
        for (plan in plans) {
            var score = 0L
            var currentStep = 10L
            for (step in 0..<steps) {
                val trackIndex = step % track.size
                val move = if (track[trackIndex] != 0) {
                    track[trackIndex]
                } else {
                    plan.value[step % plan.value.size]!!
                }
                currentStep = (currentStep + move).coerceAtLeast(0)
                score += currentStep
            }
            scores[plan.key] = score
        }

        if (part.isThree()) {
            val scoreToBeat = scores["A"]!!
            return scores.filter { it.value > scoreToBeat }.size.toString()
        }
        val result = scores.toList().sortedByDescending { it.second }
        return result.joinToString("") { it.first }
    }

    private fun String.toInts(): List<Int> {
        val string = this.replace("+", "1").replace("-", "-1").replace("=", "0")
        return string.split(",").map { it.toInt() }
    }
}
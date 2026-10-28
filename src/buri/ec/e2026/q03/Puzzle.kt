package buri.ec.e2026.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
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
        assertRun(1478, 1)
        assertRun(16075, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1161463, 1)
        assertRun(19373823709, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(50987, 1)
        assertRun(299673, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var sum = 0L
        for (line in input) {
            val iso = line.split("-")[1].split(" ")[0].reversed()
            val vote = line.split("-")[1].split(" ")[1].reversed()

            val powers = mutableListOf<Long>()
            for (i in vote.indices) {
                val l = iso[i % iso.length].digitToInt().toDouble()
                powers.add(l.pow(i).toLong())
            }
            val score = powers.mapIndexed { index, it -> vote[index].digitToInt() * it }.sum()
            if (!part.isThree() || isValid(powers, vote, score)) {
                sum += score
            }
        }
        return sum
    }

    // A vote is valid if there are no lower numbered votes that lead to the same score.
    fun isValid(powers: MutableList<Long>, vote: String, score: Long): Boolean {
        val voteAsLong = vote.reversed().toLong()
        for (testVoteAsLong in (voteAsLong - 1) downTo 0) {
            val testVote = testVoteAsLong.toString().padStart(vote.length, '0').reversed()
            val testScore = powers.mapIndexed { index, it -> testVote[index].digitToInt() * it }.sum()
            if (testScore == score) {
                return false
            }
        }
        return true
    }
}
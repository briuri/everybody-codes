package buri.ec.e2025.q11

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
        assertRun(109, 1)
        assertRun(302, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1579, 1)
        assertRun(3887051, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(147097688246708, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val ducks = input.map { it.toLong() }.toMutableList()
        if (!part.isThree()) {
            var changed = true
            var round = 0L
            while (changed) {
                changed = false
                for (i in 0..<ducks.lastIndex) {
                    if (ducks[i] > ducks[i + 1]) {
                        changed = true
                        ducks[i] = ducks[i] - 1
                        ducks[i + 1] = ducks[i + 1] + 1
                    }
                }
                if (changed) {
                    round++
                }
            }

            changed = true
            while (changed) {
                if (round == 10L && part.isOne()) {
                    return (ducks.mapIndexed { index, it -> (index + 1) * it }.sum())
                }
                changed = false
                for (i in 0..<ducks.lastIndex) {
                    if (ducks[i] < ducks[i + 1]) {
                        changed = true
                        ducks[i] = ducks[i] + 1
                        ducks[i + 1] = ducks[i + 1] - 1
                    }
                }
                if (changed) {
                    round++
                }
            }
            return round
        }

        // Part Three
        val equalFlocks = ducks.sum() / ducks.size
        return ducks.filter { it < equalFlocks }.sumOf { equalFlocks - it }
    }
}
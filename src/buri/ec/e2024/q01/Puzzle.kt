package buri.ec.e2024.q01

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
        assertRun(5, 1)
        assertRun(1334, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(28, 1)
        assertRun(5383, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(30, 1)
        assertRun(28000, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val battles = input[0].chunked(part.number)
        var sum = 0
        for (battle in battles) {
            for (mob in battle) {
                sum += when (mob) {
                    'B' -> 1
                    'C' -> 3
                    'D' -> 5
                    else -> 0   // A or x
                }
            }
            val mobCount = part.number - battle.count { it == 'x' }
            sum += when (mobCount) {
                2 -> 2
                3 -> 6
                else -> 0       // 0 or 1 mobs
            }
        }
        return sum
    }
}
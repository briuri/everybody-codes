package buri.ec.y24.q01

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
        assertRun(5, true)
        assertRun(1334, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(28, true)
        assertRun(5383, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(30, true)
        assertRun(28000, false, true)
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
package buri.ec.e2025.q04

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test
import kotlin.math.ceil

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(15888, true)
        assertRun(18243, isExample = false, toConsole = true)
    }

    @Test
    fun runPart2() {
        assertRun(1274509803922, true)
        assertRun(2852760736197, isExample = false, toConsole = true)
    }

    @Test
    fun runPart3() {
        assertRun(6818, true)
        assertRun(128784641190, isExample = false, toConsole = true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        if (part.isOne()) {
            val gears = input.map { it.toLong() }
            return ((gears.first() / gears.last().toDouble()) * 2025).toLong()
        } else if (part.isTwo()) {
            val gears = input.map { it.toLong() }
            val targetTurns = 10_000_000_000_000
            return ceil(targetTurns * gears.last() / gears.first().toDouble()).toLong()
        }

        val firstGear = input.first().toLong()
        val lastGear = input.last().toLong()
        var ratio = 1L
        for (middleGear in input.drop(1).dropLast(1)) {
            val gear = middleGear.split("|")
            ratio *= gear[1].toLong() / gear[0].toLong()
        }
        return (100 * ratio * firstGear / lastGear)
    }
}
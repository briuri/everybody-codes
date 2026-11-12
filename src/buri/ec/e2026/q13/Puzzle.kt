package buri.ec.e2026.q13

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import buri.ec.common.extractLongs
import buri.ec.common.lcm
import org.junit.Test
import kotlin.math.abs

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(5, 1)
        assertRun(7, 2)
        assertRun(9, 3)
        assertRun(27, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(21, 1)
        assertRun(135370, 2)
        assertRun(135233, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(984329851511, 1)
        assertRun(1083720010044, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val clocks = input.chunked(5)
        return clocks.sumOf { runClock(it) }
    }

    fun runClock(input: List<String>): Long {
        val handSlices = input[0].extractInts()
        val handDirections = input[1].split(" ")[1].split(":")
        val handStarts = input[2].extractInts()
        val handDurations = input[3].extractLongs()
        val clock = Clock(handSlices, handDirections, handStarts, handDurations)
        val maxTime = clock.hands.maxOf { it.duration }

        var signals = 0L
        var time = 1L
        val visited = mutableMapOf<String, Pair<Long, Long>>()
        while (time <= maxTime) {
            // Clear visited when a hand stops moving.
            if (clock.hands.any { it.duration == time }) {
                visited.clear()
            }
            // Handle cycle detection
            if (clock.toString() in visited.keys) {
                val (firstCycle, oldSignals) = visited[clock.toString()]!!
                val cycleLength = time - firstCycle
                // Don't exceed the next stopped hand.
                val nextTime = clock.hands.sortedBy { it.duration }.firstOrNull { it.duration > time }?.duration
                if (nextTime != null) {
                    val skips = (nextTime - time - 1) / cycleLength
                    time += cycleLength * skips
                    signals += (signals - oldSignals) * skips
                }
            }
            visited[clock.toString()] = Pair(time, signals)
            val sweeps = clock.hands.map { it.tick(time) }

            // All hands meet if they all started in different places.
            if (sweeps.map { it.start }.size == 3 && sweeps.map { it.end }.size == 1) {
                signals += 3
            } else {
                var crossCount = 0
                for (sweepA in sweeps) {
                    for (sweepB in sweeps.filter { it != sweepA }) {
                        if (sweepA.crosses(sweepB)) {
                            crossCount++
                        }
                    }
                }
                signals += crossCount / 2
            }
            time++
        }
        return signals
    }
}

data class Clock(
    val handSlices: List<Int>,
    val handDirections: List<String>,
    val handStarts: List<Int>,
    val handDurations: List<Long>
) {
    override fun toString() = "Clock(${hands[0]}, ${hands[1]}, ${hands[2]})"

    val clockSize = lcm(lcm(handSlices[0], handSlices[1]), handSlices[2])
    val hands = mutableListOf<Hand>()

    init {
        for (i in 0..2) {
            hands.add(Hand(handSlices[i], clockSize, handDirections[i], handStarts[i], handDurations[i]))
        }
    }
}

data class Hand(val slice: Int, val clockSize: Int, val direction: String, val start: Int, val duration: Long) {
    override fun toString() = "$direction${abs(tickIncrement)} at=$current speed=$tickIncrement"

    val dir = if (direction == "CW") {
        1
    } else {
        -1
    }
    var tickIncrement = dir * (clockSize / slice)
    var current = abs(tickIncrement) * start

    fun tick(time: Long): Sweep {
        if (time > duration) {
            tickIncrement = 0
        }
        val sweep = Sweep(clockSize, current, tickIncrement)
        current = ((current + tickIncrement) % clockSize + clockSize) % clockSize
        return sweep
    }
}

data class Sweep(val clockSize: Int, val rawStart: Int, val tickIncrement: Int) {
    override fun toString() = "Sweep($start + $tickIncrement = $end)"

    val isClockwise = tickIncrement > 0
    val ticks = mutableSetOf<Int>()
    val start: Int
    val end: Int

    init {
        // Stopped hand
        if (tickIncrement == 0) {
            start = rawStart
            end = start
            ticks.add(start)
        }
        // Ascending sweep
        else if (isClockwise) {
            start = rawStart
            end = start + tickIncrement
            ticks.addAll(start..end)
        }
        // Descending sweep
        else {
            start = if (rawStart == 0) {
                clockSize
            } else {
                rawStart
            }
            end = start + tickIncrement
            ticks.addAll(start downTo end)
        }
    }

    fun crosses(other: Sweep): Boolean {
        val start1 = ticks.first()
        val end1 = ticks.last()
        val start2 = other.ticks.first()
        val end2 = other.ticks.last()

        // If hands start at same place, they will never cross.
        if (start1 == start2) {
            return false
        }
        // If hands end at same place, but didn't start at same place,they cross.
        if (end1 % clockSize == end2 % clockSize) {
            return true
        }
        // If hands go in same direction, one must completely overtake the other
        if (isClockwise && other.isClockwise) {
            return (start1 > start2 && end1 < end2) || (start2 > start1 && end2 < end1)
        }
        if (!isClockwise && !other.isClockwise) {
            return (start1 < start2 && end1 > end2) || (start2 < start1 && end2 > end1)
        }
        // Base case: hand going in opposite directions
        return ticks.intersect(other.ticks).isNotEmpty()
    }
}
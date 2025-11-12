package buri.ec.e1.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import buri.ec.common.position.Point2D
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(1310, true)
        assertRun(3239, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(14, true)
        assertRun(1050362, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(99130092822, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val snails = mutableListOf<Snail>()
        for (line in input) {
            val numbers = line.extractInts()
            snails.add(Snail(numbers[0], numbers[1]))
        }
        if (part.isOne()) {
            val positions = snails.map { it.getPositionOnDay(100) }
            return positions.sumOf { it.x + (100 * it.y) }
        }

        var increment = 1L
        var day = 0L
        if (part.isTwo()) {
            while (true) {
                val ys = snails.map { it.getPositionOnDay(day).y }.toSet()
                if (ys.size == 1 && ys.first() == 1) {
                    return day
                }
                day += increment
            }
        }

        // Part Three
        for (snail in snails) {
            while (snail.getPositionOnDay(day).y != 1) {
                day += increment
            }
            increment *= snail.length
        }
        return day
    }
}

data class Snail(val x: Int, val y: Int) {
    val length = x + y - 1

    fun getPositionOnDay(day: Long): Point2D<Int> {
        var movingX = x
        var movingY = y
        repeat((day % length).toInt()) {
            if (movingY == 1) {
                val temp = movingY
                movingY = movingX
                movingX = temp
            } else {
                movingX++
                movingY--
            }
        }
        return Point2D(movingX, movingY)
    }
}
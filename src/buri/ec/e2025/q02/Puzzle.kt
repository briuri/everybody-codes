package buri.ec.e2025.q02

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractLongs
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun("[357,862]", true)
        assertRun("[117945,598088]", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("4076", true)
        assertRun("566", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("406954", true)
        assertRun("53799", false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val numbers = mutableMapOf<Char, ComplexNum>()
        for (line in input) {
            val rawNumbers = line.extractLongs()
            numbers[line.first()] = ComplexNum(rawNumbers[0], rawNumbers[1])
        }
        if (part.isOne()) {
            var result = ComplexNum(0, 0)
            repeat(3) {
                result = result.multiply(result)
                result = result.divide(ComplexNum(10, 10))
                result = result.add(numbers['A']!!)
            }
            return result.toString()
        }

        val gridSize = ComplexNum(1000, 1000)
        val dividend = ComplexNum(100_000, 100_000)
        val engraveRange = -1_000_000..1000000
        val ul = numbers['A']!!
        val lr = ul.add(gridSize)

        var engraveCount = 0
        val resolution = if (part.isTwo()) {
            10L
        } else {
            1L
        }
        for (y in (ul.y)..(lr.y) step resolution) {
            for (x in (ul.x)..(lr.x) step resolution) {
                var result = ComplexNum(0, 0)
                repeat(100) {
                    result = result.multiply(result)
                    result = result.divide(dividend)
                    result = result.add(ComplexNum(x, y))
                }
                if (result.x in engraveRange && result.y in engraveRange) {
                    engraveCount++
                }
            }
        }
        return engraveCount.toString()
    }
}

data class ComplexNum(val x: Long, val y: Long) {

    override fun toString(): String = "[$x,$y]"

    // [X1,Y1] + [X2,Y2] = [X1 + X2, Y1 + Y2]
    fun add(to: ComplexNum): ComplexNum {
        return ComplexNum(x + to.x, y + to.y)
    }

    // [X1,Y1] * [X2,Y2] = [X1 * X2 - Y1 * Y2, X1 * Y2 + Y1 * X2]
    fun multiply(to: ComplexNum): ComplexNum {
        return ComplexNum(x * to.x - y * to.y, x * to.y + y * to.x)
    }

    // [X1,Y1] / [X2,Y2] = [X1 / X2, Y1 / Y2]
    fun divide(to: ComplexNum): ComplexNum {
        return ComplexNum(x / to.x, y / to.y)
    }
}
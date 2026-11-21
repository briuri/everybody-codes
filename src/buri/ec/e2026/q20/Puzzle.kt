package buri.ec.e2026.q20

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
        assertRun("75", 1)
        assertRun("374", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("101", 1)
        assertRun("45231", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("125", 1)
        assertRun("693", 2)
        assertRun("48568", 0, true)
    }

    val digitsToSegments = mapOf(
        '0' to 6, '1' to 2, '2' to 5, '3' to 5, '4' to 4, '5' to 5,
        '6' to 6, '7' to 3, '8' to 7, '9' to 6, 'A' to 6, ' ' to 0,
        '+' to 2, '-' to 1, '=' to 2
    )
    val digitsToRemovals = mapOf(
        '6' to setOf('5'),
        '7' to setOf('1'),
        '8' to setOf('0', '6', '9', 'A'),
        '9' to setOf('3', '5'),
        '+' to setOf('-'),
    )
    val digitsToChanges = mapOf(
        '0' to setOf('6', '9', 'A'),
        '2' to setOf('3'),
        '3' to setOf('2', '5'),
        '5' to setOf('3'),
        '6' to setOf('0', '9', 'A'),
        '9' to setOf('0', '6', 'A'),
        'A' to setOf('0', '6', '9')
    )
    val digitsToAdditions = mapOf(
        '0' to setOf('8'),
        '1' to setOf('7'),
        '3' to setOf('9'),
        '5' to setOf('6', '9'),
        '6' to setOf('8'),
        '9' to setOf('8'),
        'A' to setOf('8'),
        '-' to setOf('+')
    )

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val base = if (part.isOne()) {
            10
        } else {
            11
        }
        var count = 0L
        for (line in input) {
            if (isValidEquation(line, base) || canShift(part, line, base)) {
                for (v in line) {
                    count += digitsToSegments[v]!!
                }
            }
        }
        return count.toString(base)
    }

    fun isValidEquation(equation: String, base: Int): Boolean {
        val tokens = equation.split(" ")
        val leftOperand = tokens[1][0]
        val rightOperand = tokens[5][0]
        val left = if (leftOperand == '+') {
            tokens[0].toInt(base) + tokens[2].toInt(base)
        } else {
            tokens[0].toInt(base) - tokens[2].toInt(base)
        }
        val right = if (rightOperand == '+') {
            tokens[4].toInt(base) + tokens[6].toInt(base)
        } else {
            tokens[4].toInt(base) - tokens[6].toInt(base)
        }
        return left == right
    }

    fun canShift(part: Part, line: String, base: Int): Boolean {
        if (!part.isThree()) {
            return false
        }
        for ((i, valueA) in line.withIndex()) {
            for ((j, valueB) in line.withIndex()) {
                val modified = line.toCharArray()
                if (i == j) {
                    for (valueChanged in digitsToChanges[valueA].orEmpty()) {
                        modified[j] = valueChanged
                        if (isValidEquation(modified.concatToString(), base)) {
                            return true
                        }
                    }
                } else {
                    for (valueMinusOne in digitsToRemovals[valueA].orEmpty()) {
                        for (valuePlusOne in digitsToAdditions[valueB].orEmpty()) {
                            modified[i] = valueMinusOne
                            modified[j] = valuePlusOne
                            if (isValidEquation(modified.concatToString(), base)) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }
}

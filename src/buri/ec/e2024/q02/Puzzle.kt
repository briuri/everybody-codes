package buri.ec.e2024.q02

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Direction
import buri.ec.common.position.Grid
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
        assertRun(4, true)
        assertRun(33, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(42, true)
        assertRun(5139, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(10, true)
        assertRun(11137, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val words = input[0].split(":")[1].split(",").toMutableList()
        val strings = input.drop(2)
        if (part.isOne()) {
            var count = 0
            for (word in words) {
                count += strings[0].countOccurences(word)
            }
            return count
        } else if (part.isTwo()) {
            words.addAll(words.map { it.reversed() })
            var count = 0
            for (string in strings) {
                val positions = mutableSetOf<Int>()
                for (word in words) {
                    positions.addAll(string.markPositions(word))
                }
                count += positions.size
            }
            return count
        } else {
            words.addAll(words.map { it.reversed() })
            val grid = Grid.fromCharInput(strings)
            val marks = Grid(grid.width, grid.height, 0)

            // Build traversable strings in each direction (east/south) from the current position.
            for (y in 0..<grid.height) {
                for (x in 0..<grid.width) {
                    val xLine = grid.getTraversableString(x, y, Direction.EAST)
                    val yLine = grid.getTraversableString(x, y, Direction.SOUTH)
                    for (word in words) {
                        if (xLine.startsWith(word)) {
                            for (i in word.indices) {
                                marks[xLine[i].position] = 1
                            }
                        }
                        if (yLine.startsWith(word)) {
                            for (i in word.indices) {
                                marks[yLine[i].position] = 1
                            }
                        }
                    }
                }
            }
            return marks.count { it == 1 }
        }
    }

    /**
     * Counts how many times a token appears in a string.
     */
    private fun String.countOccurences(token: String): Int {
        var count = 0
        var start = 0
        var next = this.indexOf(token, start)
        while (next != -1) {
            count++
            start = next + 1
            next = this.indexOf(token, start)
        }
        return count
    }

    /**
     * Marks the positions of a token in a string.
     */
    private fun String.markPositions(token: String): Set<Int> {
        val positions = mutableSetOf<Int>()

        var start = 0
        var next = this.indexOf(token, start)
        while (next != -1) {
            for (i in 0..token.lastIndex) {
                positions.add(next + i)
            }
            start = next + 1
            next = this.indexOf(token, start)
        }
        return positions
    }

    /**
     * Builds a traversable string on the grid from the current position. Left-to-right wraps, Top-to-Bottom doesn't.
     */
    private fun Grid<Char>.getTraversableString(x: Int, y: Int, direction: Direction): List<MarkedChar> {
        val line = mutableListOf<MarkedChar>()
        if (direction == Direction.EAST) {
            for (i in 0..<this.width) {
                val nextX = (x + i) % this.width
                line.add(MarkedChar(this[nextX, y], Point2D(nextX, y)))
            }
        } else {
            for (nextY in y..<this.height) {
                line.add(MarkedChar(this[x, nextY], Point2D(x, nextY)))
            }
        }
        return line
    }

    /**
     * Compares the start of a traversable string with the test value
     */
    private fun List<MarkedChar>.startsWith(string: String): Boolean {
        for (i in string.indices) {
            if (i >= this.size || this[i].value != string[i]) {
                return false
            }
        }
        return true
    }
}

/**
 * Representation of a character and its absolute position in the grid.
 */
data class MarkedChar(val value: Char, val position: Point2D<Int>)
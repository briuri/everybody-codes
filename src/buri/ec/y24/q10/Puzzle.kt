package buri.ec.y24.q10

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Bounds2D
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
        assertRun("PTBVRCZHFLJWGMNS", true)
        assertRun("ZNDFQKMGSTLJVHXW", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("198124", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("3889", true)
        assertRun("209778", false, true)
    }

    private val letterScores = "_ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val gridSize = 8

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val grid = Grid.fromCharInput(input)
        if (part.isOne()) {
            val start = Point2D(0, 0)
            grid.solve(start)
            return grid.getWord(start)
        } else {
            val gridsPerRow = if (part.isTwo()) {
                input.first().length / (gridSize + 1) + 1
            } else {
                (input.first().length - 2) / 6
            }
            val gridsPerColumn = if (part.isTwo()) {
                input.size / (gridSize + 1) + 1
            } else {
                (input.size - 2) / 6
            }
            val gridStarts = mutableListOf<Point2D<Int>>()
            for (y in 0..<gridsPerColumn) {
                for (x in 0..<gridsPerRow) {
                    val startX = if (part.isTwo()) {
                        x * (gridSize + 1)
                    } else {
                        x * 6
                    }
                    val startY = if (part.isTwo()) {
                        y * (gridSize + 1)
                    } else {
                        y * 6
                    }
                    gridStarts.add(Point2D(startX, startY))
                }
            }
            repeat(2) {
                for (start in gridStarts) {
                    grid.solve(start)
                }
            }

            var scores = 0
            for (start in gridStarts) {
                scores += grid.getWord(start).getScore()
            }
            return scores.toString()
        }
    }

    private fun Bounds2D.contains(point: Point2D<Int>): Boolean {
        return (point.x in this.x && point.y in this.y)
    }

    private fun Grid<Char>.solve(start: Point2D<Int>) {
        val bounds = Bounds2D(setOf(start, Point2D(start.x + gridSize - 1, start.y + gridSize - 1)))
        val allSpaces = this.filter { it == '.' }.filter { bounds.contains(it) }
        for (emptySpace in allSpaces) {
            val row = mutableSetOf<Char>()
            for (x in bounds.x) {
                row.add(this[x, emptySpace.y])
            }
            val column = mutableSetOf<Char>()
            for (y in bounds.y) {
                column.add(this[emptySpace.x, y])
            }
            val intersection = row.intersect(column).toMutableSet().filter { it !in listOf('.', '?') }
            if (intersection.size == 1) {
                this[emptySpace] = intersection.first()
            }
        }

        // Part Three
        val emptyCount = this.filter { it == '.' }.filter { bounds.contains(it) }.size
        val questionCount = this.filter { it == '?' }.filter { bounds.contains(it) }.size
        if (emptyCount != questionCount) {
            return
        }

        for (emptySpace in this.filter { it == '.' }.filter { bounds.contains(it) }) {
            val row = mutableListOf<Char>()
            val rowPositions = mutableListOf<Point2D<Int>>()
            for (x in bounds.x) {
                val point = Point2D(x, emptySpace.y)
                row.add(this[point])
                rowPositions.add(point)
            }
            val column = mutableListOf<Char>()
            val columnPositions = mutableListOf<Point2D<Int>>()
            for (y in bounds.y) {
                val point = Point2D(emptySpace.x, y)
                column.add(this[point])
                columnPositions.add(point)
            }
            val rowChar = solveLine(row)
            val columnChar = solveLine(column)
            if (rowChar != '?') {
                this[rowPositions[row.indexOf('.')]] = rowChar
                this[columnPositions[column.indexOf('?')]] = rowChar
            } else if (columnChar != '?') {
                this[columnPositions[column.indexOf('.')]] = columnChar
                this[rowPositions[row.indexOf('?')]] = columnChar
            }
        }
    }

    private fun solveLine(list: List<Char>): Char {
        if (list.count { it == '.' } > 1) {
            return '?'
        }
        val frequency = mutableMapOf<Char, Int>()
        for (c in list.filter { it != '.' }) {
            frequency.putIfAbsent(c, 0)
            frequency[c] = frequency[c]!! + 1
        }
        return frequency.filter { it.value == 1 }.keys.first()
    }

    private fun Grid<Char>.getWord(start: Point2D<Int>): String {
        var word = ""
        for (y in (start.y + 2)..(start.y + 5)) {
            for (x in (start.x + 2)..(start.x + 5)) {
                word += this[x, y]
            }
        }
        return word
    }

    private fun String.getScore(): Int {
        if (this.count { it == '.' } > 0) {
            return 0
        }
        var score = 0
        for (i in this.indices) {
            score += letterScores.indexOf(this[i]) * (i + 1)
        }
        return score
    }
}
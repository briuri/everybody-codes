package buri.ec.y24.q10

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
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
        assertRun("", false, true)
    }

    private val letterScores ="_ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val gridSize = 8

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val grid = Grid.fromCharInput(input)
        if (part.isOne()) {
            return grid.findWord()
        }
        else {
            var scores = 0
            val gridsPerRow = input.first().length / (gridSize + 1)
            val gridsPerColumn = input.size / (gridSize + 1)
            for (y in 0..gridsPerColumn) {
                for (x in 0..gridsPerRow) {
                    val startX = x * (gridSize + 1)
                    val startY = y * (gridSize + 1)
                    val subGrid = grid.getSubGrid(Point2D(startX, startY), gridSize,  gridSize)
                    scores += getScore(subGrid.findWord())
                }
            }
            return scores.toString()
        }
    }

    private fun Grid<Char>.findWord(): String {
        var word = ""
        for (emptySpace in this.filter { it == '.' }) {
            val row = mutableSetOf<Char>()
            val column = mutableSetOf<Char>()
            for (x in this.xRange) {
                row.add(this[x, emptySpace.y])
            }
            for (y in this.yRange) {
                column.add(this[emptySpace.x, y])
            }
            val intersection = row.intersect(column).toMutableSet()
            intersection.remove('.')
            word += intersection.first()
        }
        return word
    }

    private fun getScore(word: String): Int {
        var score = 0
        for (i in word.indices) {
            score += letterScores.indexOf(word[i]) * (i + 1)
        }
        return score
    }
}
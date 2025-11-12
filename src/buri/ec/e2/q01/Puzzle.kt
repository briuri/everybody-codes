package buri.ec.e2.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.generatePermutations
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
        assertRun("26", true)
        assertRun("42", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("115", true)
        assertRun("1115", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("13 43", true)
        assertRun("46 116", false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val gridSeparator = input.indexOf("")
        val grid = Grid.fromCharInput(input.subList(0, gridSeparator))
        val paths = input.subList(gridSeparator + 1, input.size)

        if (part.isOne()) {
            var score = 0
            for (i in paths.indices) {
                val path = paths[i]
                val slotNum = i + 1
                score += getScore(grid, slotNum, path)
            }
            return score.toString()
        }
        // Token to (Slot, Score)
        val results = mutableMapOf<Int, MutableMap<Int, Int>>()
        val totalSlots = input[0].count { it == '*' }
        for (i in paths.indices) {
            val path = paths[i]
            results[i] = mutableMapOf<Int, Int>()
            for (j in 1..totalSlots) {
                results[i]!![j] = getScore(grid, j, path)
            }
        }

        if (part.isTwo()) {
            var score = 0
            for (result in results) {
                score += result.value.map { it.value }.max()
            }
            return score.toString()
        }

        val numSlots = (1..totalSlots).toList()
        val numTokens = paths.size
        val allCombinations = numSlots.generateCombinations(numTokens)
        println("Number of combinations: ${allCombinations.size}")

        var minScore = Int.MAX_VALUE
        var maxScore = Int.MIN_VALUE
        for (combination in allCombinations) {
            for (permutation in generatePermutations(combination)) {
                var score = 0
                val slotAssignments = permutation.split(",").map { it.toInt() }
                for (i in slotAssignments.indices) {
                    score += results[i]!![slotAssignments[i]]!!
                }
                minScore = minScore.coerceAtMost(score)
                maxScore = maxScore.coerceAtLeast(score)
            }
        }
        return "$minScore $maxScore"
    }

    fun getScore(grid: Grid<Char>, slotNum: Int, path: String): Int {
        val startX = slotNum * 2 - 2
        var pathIndex = 0
        var coords = Point2D(startX, 0)
        while (grid.isInBounds(coords)) {
            if (grid[coords] == '*') {
                var direction = path[pathIndex++]
                if (direction == 'R' && coords.x + 1 !in grid.xRange) {
                    direction = 'L'
                }
                if (direction == 'L' && coords.x == 0) {
                    direction = 'R'
                }
                when (direction) {
                    'L' -> coords = Point2D(coords.x - 1, coords.y + 1)
                    'R' -> coords = Point2D(coords.x + 1, coords.y + 1)
                }
            } else {
                coords = Point2D(coords.x, coords.y + 1)
            }
        }
        val endSlotNum = (coords.x + 2) / 2
        val localScore = ((endSlotNum * 2) - slotNum).coerceAtLeast(0)
        return localScore
    }

    fun <T> List<T>.generateCombinations(k: Int): List<List<T>> {
        if (k == 0)
            return listOf(emptyList())
        if (k == size)
            return listOf(this)
        if (k > size)
            return emptyList()

        val result = mutableListOf<List<T>>()
        for (i in 0..size - k) {
            val head = this[i]
            val tail = this.subList(i + 1, size)
            tail.generateCombinations(k - 1).forEach {
                result.add(listOf(head) + it)
            }
        }
        return result
    }
}
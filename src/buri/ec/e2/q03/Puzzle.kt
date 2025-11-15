package buri.ec.e2.q03

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
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
        assertRun("844", 1)
        assertRun("616", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("1,3,4,2", 1)
        assertRun("5,2,7,9,6,8,3,1,4", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("33", 1)
        assertRun("154547", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val diceSeparator = input.indexOf("")
        val diceInput = if (diceSeparator == -1) {
            input
        } else {
            input.subList(0, diceSeparator)
        }
        val dice = mutableMapOf<Int, Die>()
        // 1: faces=[1,2,3,4,5,6] seed=7
        for (line in diceInput) {
            val numbers = line.extractInts(true)
            val faces = numbers.drop(1).dropLast(1)
            dice[numbers[0]] = Die(faces, numbers.last().toLong())
        }

        if (part.isOne()) {
            var points = 0
            while (points < 10_000) {
                val localPoints = dice.values.sumOf { it.roll() }
                points += localPoints
            }
            return dice.values.first().rollCount.toString()
        }

        if (part.isTwo()) {
            // Simulate a race
            val simluationLength = 12_000
            val paths = mutableMapOf<Int, MutableList<Int>>()
            for (id in dice.keys) {
                paths[id] = mutableListOf()
                repeat(simluationLength) {
                    paths[id]!!.add(dice[id]!!.roll())
                }
            }
            val racetrack = input.last().toCharArray().toList().map { it.digitToInt() }
            // Figure out order.
            val finishesAt = mutableMapOf<Int, Int>()
            for (key in paths.keys) {
                val path = paths[key]!!
                var remaining = racetrack
                while (remaining.isNotEmpty()) {
                    val next = path.removeFirst()
                    if (remaining.first() == next) {
                        remaining = remaining.drop(1)
                    }
                }
                finishesAt[key] = simluationLength - path.size
            }
            return finishesAt.toList().sortedBy { it.second }.map { it.first }.joinToString(",")
        }

        // Part Three
        val grid = Grid.fromIntInput(input.subList(diceSeparator + 1, input.size))
        val coinGrid = Grid(grid.width, grid.height, 0)
        val starts = mutableSetOf<Point2D<Int>>()
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                starts.add(Point2D(x, y))
            }
        }
        for (die in dice.values) {
            var frontier = starts
            while (frontier.isNotEmpty()) {
                val nexts = mutableSetOf<Point2D<Int>>()
                val roll = die.roll()
                for (position in frontier) {
                    if (grid[position] != roll) {
                        continue
                    }
                    coinGrid[position] = 1
                    nexts.add(position)
                    nexts.addAll(grid.getNeighbors(position))
                }
                frontier = nexts
            }
        }
        return coinGrid.sum().toString()
    }
}

data class Die(val faces: List<Int>, val seed: Long) {
    var pulse = seed
    var rollCount = 0
    var currentFace = 0

    fun roll(): Int {
        rollCount++
        val spin = rollCount * pulse
        pulse = ((pulse + spin) % seed) + 1 + rollCount + seed
        currentFace = ((currentFace + spin) % faces.size).toInt()
        return faces[currentFace]
    }
}
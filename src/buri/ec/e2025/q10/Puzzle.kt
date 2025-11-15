package buri.ec.e2025.q10

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
        assertRun(27, true)
        assertRun(145, isExample = false, toConsole = true)
    }

    @Test
    fun runPart2() {
        assertRun(27, true)
        assertRun(1727, isExample = false, toConsole = true)
    }

    @Test
    fun runPart3() {
        assertRun(15, true)
        assertRun(0, isExample = false, toConsole = true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val start = grid.filter { it == 'D' }.first()
        val sheeps = grid.filter { it == 'S' }
        val hideouts = grid.filter { it == '#' }
        var dragonSquares = mutableSetOf<Point2D<Int>>()
        dragonSquares.add(start)

        if (part.isOne()) {
            val rounds = if (input.size < 14) {
                3
            } else {
                4
            }
            repeat(rounds) {
                val newDragonSquares = mutableSetOf<Point2D<Int>>()
                for (square in dragonSquares) {
                    newDragonSquares.add(square)
                    newDragonSquares.addAll(getDragonMoves(grid, square))
                }
                dragonSquares = newDragonSquares
            }
            return dragonSquares.intersect(sheeps).size
        } else {
            val rounds = if (input.size < 14) {
                3
            } else {
                20
            }
            var livingSheep = mutableListOf<Point2D<Int>>()
            livingSheep.addAll(sheeps)

            var eatenSheep = 0
            repeat(rounds) {
                var localEatenSheep = 0

                // Dragon's Turn
                val newDragonSquares = mutableSetOf<Point2D<Int>>()
                for (square in dragonSquares) {
                    newDragonSquares.addAll(getDragonMoves(grid, square))
                }
                var vulnerableSheep = livingSheep.intersect(newDragonSquares).toMutableList()
                vulnerableSheep.removeIf { it in hideouts }
                localEatenSheep += vulnerableSheep.size
                livingSheep.removeAll(vulnerableSheep)

                // Sheep's Turn
                livingSheep = moveSheep(grid, livingSheep)
                vulnerableSheep = livingSheep.intersect(newDragonSquares).toMutableList()
                vulnerableSheep.removeIf { it in hideouts }
                localEatenSheep += vulnerableSheep.size
                livingSheep.removeAll(vulnerableSheep)

                dragonSquares = newDragonSquares
                eatenSheep += localEatenSheep
            }
            return eatenSheep
        }
    }

    fun getDragonMoves(grid: Grid<Char>, current: Point2D<Int>): List<Point2D<Int>> {
        val list = mutableListOf<Point2D<Int>>()
        list.add(Point2D(current.x + 1, current.y - 2))
        list.add(Point2D(current.x + 1, current.y + 2))
        list.add(Point2D(current.x - 1, current.y - 2))
        list.add(Point2D(current.x - 1, current.y + 2))
        list.add(Point2D(current.x - 2, current.y - 1))
        list.add(Point2D(current.x - 2, current.y + 1))
        list.add(Point2D(current.x + 2, current.y - 1))
        list.add(Point2D(current.x + 2, current.y + 1))
        return list.filter { grid.isInBounds(it) }
    }

    fun moveSheep(grid: Grid<Char>, sheeps: List<Point2D<Int>>): MutableList<Point2D<Int>> {
        val newSheep = mutableListOf<Point2D<Int>>()
        for (sheep in sheeps) {
            newSheep.add(Point2D(sheep.x, sheep.y + 1))
        }
        return newSheep.filter { grid.isInBounds(it) }.toMutableList()
    }
}

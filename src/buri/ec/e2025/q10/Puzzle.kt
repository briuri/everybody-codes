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
        val sheeps = grid.filter { it == 'S' }.toSet()
        val hideouts = grid.filter { it == '#' }

        if (part.isOne()) {
            val rounds = if (input.size < 14) {
                3
            } else {
                4
            }

            val dragonMoves = mutableSetOf<Point2D<Int>>()
            dragonMoves.add(start)
            repeat(rounds) {
                dragonMoves.addAll(grid.getDragonMoves(dragonMoves))
            }
            return dragonMoves.intersect(sheeps).size
        } else if (part.isTwo()) {
            val rounds = if (input.size < 14) {
                3
            } else {
                20
            }

            var sheepLeft = sheeps.toMutableSet()
            var eatenSheep = 0
            var dragonMoves = setOf(start)
            repeat(rounds) {
                // Dragon's Turn
                val nextDragonMoves = grid.getDragonMoves(dragonMoves)

                // Count Sheep
                var vulnerableSheep = sheepLeft.intersect(nextDragonMoves).toMutableSet()
                vulnerableSheep.removeIf { it in hideouts }
                eatenSheep += vulnerableSheep.size
                sheepLeft.removeAll(vulnerableSheep)

                // Sheep's Turn
                sheepLeft = grid.moveAllSheep(sheepLeft)

                // Count Sheep
                vulnerableSheep = sheepLeft.intersect(nextDragonMoves).toMutableSet()
                vulnerableSheep.removeIf { it in hideouts }
                eatenSheep += vulnerableSheep.size
                sheepLeft.removeAll(vulnerableSheep)

                dragonMoves = nextDragonMoves
            }
            return eatenSheep
        }

        // Part Three
        return -1
    }

    fun Grid<Char>.getMoveName(current: Point2D<Int>): String {
        val letter = "ABCDEFGHIJKL"[current.x]
        val number = current.y + 1
        return "$letter$number"
    }

    fun Grid<Char>.getDragonMoves(currents: Set<Point2D<Int>>): Set<Point2D<Int>> {
        return currents.map { getDragonMoves(it) }.flatten().toSet()
    }

    fun Grid<Char>.getDragonMoves(current: Point2D<Int>): Set<Point2D<Int>> {
        val moves = mutableSetOf<Point2D<Int>>()
        moves.add(Point2D(current.x + 1, current.y - 2))
        moves.add(Point2D(current.x + 1, current.y + 2))
        moves.add(Point2D(current.x - 1, current.y - 2))
        moves.add(Point2D(current.x - 1, current.y + 2))
        moves.add(Point2D(current.x - 2, current.y - 1))
        moves.add(Point2D(current.x - 2, current.y + 1))
        moves.add(Point2D(current.x + 2, current.y - 1))
        moves.add(Point2D(current.x + 2, current.y + 1))
        return moves.filter { isInBounds(it) }.toSet()
    }

    fun Grid<Char>.moveAllSheep(sheeps: Set<Point2D<Int>>): MutableSet<Point2D<Int>> {
        val newSheep = mutableSetOf<Point2D<Int>>()
        for (sheep in sheeps) {
            newSheep.add(Point2D(sheep.x, sheep.y + 1))
        }
        return newSheep.filter { isInBounds(it) }.toMutableSet()
    }
}

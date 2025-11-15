package buri.ec.e2024.q20

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
        assertRun(1045, 1)
        assertRun(1030, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(78, 1)
        assertRun(572, 0, true)
    }

    @Test
    fun runPart3() {
//        assertRun(384400, 1)
        assertRun(768795, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val s = grid.filter { it == 'S' }.first()
        val startState = State(s, Direction.NORTH, "", 0, if (part.isOne()) 1000 else 10_000)

        val bestTrip = mutableMapOf<Key, Int>()
        val frontier = mutableListOf<State>()
        frontier.add(startState)

        if (part.isOne()) {
            val totalTime = 100

            while (frontier.isNotEmpty()) {
                val current = frontier.removeFirst()
                if (current.key in bestTrip.keys && bestTrip[current.key]!! >= current.alt) {
                    continue
                }
                bestTrip[current.key] = current.alt
                if (current.time <= totalTime) {
                    frontier.addAll(0, grid.getNextStates(current))
                }
            }
            return bestTrip.filter { it.key.time == totalTime }.values.max()
        }

        // Part TWO
        if (part.isTwo()) {
            val timeThresholds = if (grid.yRange.last < 20) {
                mapOf(Pair("", 30), Pair("A", 60), Pair("AB", 90), Pair("ABC", 120))
            } else {
                mapOf(Pair("", 150), Pair("A", 300), Pair("AB", 450), Pair("ABC", 600))
            }
            while (frontier.isNotEmpty()) {
                val current = frontier.removeFirst()
                if (current.key in bestTrip.keys && bestTrip[current.key]!! >= current.alt) {
                    continue
                }
                bestTrip[current.key] = current.alt

                val altRange = (startState.alt - 100)..(startState.alt + 20)
                if (current.alt in altRange && current.time < timeThresholds[current.checkpoints]!!) {
                    frontier.addAll(0, grid.getNextStates(current))
                }
            }
            val ends =
                bestTrip.filter { it.key.pos == s && it.key.backDir == Direction.SOUTH && it.key.checkpoints == "ABC" && it.value >= startState.alt }
            return ends.keys.minOf { it.time }
        }

        // Part THREE
        // Go left 3 then go south.
        val shift = -3
        var altitude = 384400 + shift
        val column = s.x + shift
        var southCount = 0
        var y = 0
        while (altitude != 0) {
            y++
            if (y > grid.yRange.last) {
                y = 0
            }
            southCount++
            val delta = when (grid[column, y]) {
                '.' -> -1
                else -> 1
            }
            altitude += delta
        }
        return southCount
    }

    private fun Grid<Char>.getNextStates(current: State): List<State> {
        val nextStates = mutableListOf<State>()
        val neighbors = this.getNeighbors(current.pos).filter { this[it] != '#' }.toMutableList()
        when (current.backDir) {
            Direction.NORTH -> neighbors.removeIf { it.y < current.pos.y }
            Direction.SOUTH -> neighbors.removeIf { it.y > current.pos.y }
            Direction.WEST -> neighbors.removeIf { it.x < current.pos.x }
            Direction.EAST -> neighbors.removeIf { it.x > current.pos.x }
        }
        for (neighbor in neighbors) {
            val delta = when (this[neighbor]) {
                '+' -> 1
                '-' -> -2
                else -> -1
            }
            val nextDir = if (neighbor.y < current.pos.y) {
                Direction.SOUTH
            } else if (neighbor.y > current.pos.y) {
                Direction.NORTH
            } else if (neighbor.x < current.pos.x) {
                Direction.EAST
            } else {
                Direction.WEST
            }
            val nextCheck = if (this[neighbor] in listOf('A', 'B', 'C')) {
                if ((this[neighbor] == 'A' && current.checkpoints != "") || (this[neighbor] == 'B' && current.checkpoints != "A") || (this[neighbor] == 'C' && current.checkpoints != "AB")) {
                    continue
                }
                (current.checkpoints + this[neighbor]).toCharArray().sorted().joinToString("")
            } else {
                current.checkpoints
            }
            nextStates.add(State(neighbor, nextDir, nextCheck, current.time + 1, current.alt + delta))
        }
        return nextStates
    }
}

data class State(val pos: Point2D<Int>, val backDir: Direction, val checkpoints: String, val time: Int, val alt: Int) {
    val key = Key(pos, backDir, checkpoints, time)
    override fun toString() = "State($key, $alt)"
}

data class Key(val pos: Point2D<Int>, val backDir: Direction, val checkpoints: String, val time: Int) {
    override fun toString() = "$pos, ${backDir.icon}, $checkpoints, $time"
}


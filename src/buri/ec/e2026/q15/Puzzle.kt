package buri.ec.e2026.q15

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Direction
import buri.ec.common.position.Grid
import buri.ec.common.position.Point2D
import org.junit.Test
import java.util.SortedMap
import kotlin.collections.ArrayDeque
import kotlin.collections.sortBy
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(184, 1)
        assertRun(281, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(5448, 1)
        assertRun(20297, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(540320698784, 1)
        assertRun(265502412359, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val graph = Graph.fromCharInput(input)
        println(graph)
        if (part.isOne()) {
            return graph.getMinStepsAllOre()
        }
        if (part.isTwo()) {
            return -1
        }
        // Part 3
        return -1
    }
}

class Graph(val start: Int, val end: Int, edges: Set<Edge>) {
    val edgeMap = sortedMapOf<Int, MutableSet<Edge>>()
    val edgeMapNoLoops: SortedMap<Int, MutableSet<Edge>>

    init {
        for (edge in edges) {
            edgeMap.putIfAbsent(edge.start, mutableSetOf())
            edgeMap[edge.start]!!.add(edge)
        }
        edgeMapNoLoops = edgeMap.toSortedMap()
        for ((_, value) in edgeMapNoLoops) {
            value.removeAll { it.start == it.end }
        }
    }

    override fun toString() = edgeMap.entries.joinToString("\n")

    fun getMinStepsAllOre(): Int {
        val totalUniqueOre = uniqueOre.values.flatten().size
        var minSteps = Int.MAX_VALUE
        val soFar = mutableMapOf<Pair<Int, Direction>, Pair<Int, Int>>()
        val frontier = ArrayDeque<State1>()
        frontier.add(State1(start, Direction.EAST, 0, mutableSetOf()))
        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            val key = Pair(current.x, current.facing)
            if (key in soFar.keys) {
                val previous = soFar[key]!!
                if (previous.first <= current.steps && previous.second >= current.ore.size) {
                    continue
                }
            }
            soFar[key] = Pair(current.steps, current.ore.size)

            if (current.x == end) {
                if (current.ore.size == totalUniqueOre) {
                    minSteps = min(minSteps, current.steps)
                    frontier.removeAll { it.steps > minSteps }
                }
                continue
            }

            val nexts = mutableSetOf<State1>()
            for (edge in edgeMapNoLoops[current.x]!!.filter { it.ore > 0 && it.canEnter(current.facing) }) {
                if (current.ore.size < totalUniqueOre) {
                    val testOre = if (edge in uniqueOre.keys) {
                        uniqueOre[edge]!!
                    } else {
                        uniqueOre[edge.reverse()]!!
                    }
                    if (testOre.none { it in current.ore }) {
                        val newOre = current.ore.toMutableSet()
                        newOre.addAll(testOre)
                        nexts.add(State1(edge.end, edge.endFacing, current.steps + edge.steps, newOre))
                    }
                }
            }
            // Only move if there are no available ore runs.
            if (nexts.isEmpty()) {
                for (edge in edgeMapNoLoops[current.x]!!.filter { it.ore == 0 && it.canEnter(current.facing) }) {
                    nexts.add(State1(edge.end, edge.endFacing, current.steps + edge.steps, current.ore))
                }
            }
            frontier.addAll(nexts)
            frontier.sortBy { it.x }
        }
        return minSteps
    }

    companion object {
        val uniqueOre = mutableMapOf<Edge, Set<Point2D<Int>>>()

        fun fromCharInput(input: List<String>): Graph {
            uniqueOre.clear()
            val grid = Grid.fromCharInput(input)
            val start = grid.filter { it == 'S' }.first().x
            val end = grid.filter { it == 'E' }.last().x
            val y = grid.filter { it == 'S' }.first().y
            val crossroads = ((start + 1)..<end).filter { grid[it, y - 1] != '#' }

            val edges = mutableSetOf<Edge>()
            // One-way east from start
            edges.add(Edge(start, Direction.EAST, crossroads[0], crossroads[0] - start, 0))
            for (i in crossroads.indices) {
                val x = crossroads[i]
                if (i != crossroads.lastIndex) {
                    val east = Edge(x, Direction.EAST, crossroads[i + 1], crossroads[i + 1] - x, 0)
                    edges.add(east)
                    if (i != 0) {
                        edges.add(east.reverse())
                    }
                }
                // Don't recompute ore runs in reverse
                if (edges.none { it.end == x && it.ore > 0 }) {
                    val runStart = Point2D(x, y)
                    val north = getOreRun(grid, runStart, true)
                    if (north != null) {
                        edges.add(north)
                        edges.add(north.reverse())
                    }
                    val south = getOreRun(grid, runStart, false)
                    if (south != null) {
                        edges.add(south)
                        edges.add(south.reverse())
                    }
                    if (north != null && south != null) {
                        val loop = Edge(
                            north.start,
                            north.startFacing,
                            north.start,
                            north.steps + south.steps,
                            north.ore + south.ore
                        )
                        edges.add(loop)
                        edges.add(loop.reverse())
                    }
                }
            }
            // One-way east from last crossroads.
            edges.add(Edge(crossroads.last(), Direction.EAST, end, (end - crossroads.last()), 0))
            return Graph(start, end, edges)
        }

        fun getOreRun(grid: Grid<Char>, start: Point2D<Int>, isNorth: Boolean): Edge? {
            val away = if (isNorth) {
                Point2D(0, -1)
            } else {
                Point2D(0, 1)
            }
            val east = Point2D(1, 0)
            val back = if (isNorth) {
                Point2D(0, 1)
            } else {
                Point2D(0, -1)
            }
            val facing = if (isNorth) {
                Direction.NORTH
            } else {
                Direction.SOUTH
            }

            var change = away
            var steps = 0
            val ore = mutableSetOf<Point2D<Int>>()
            var pos = start
            do {
                val next = Point2D(pos.x + change.x, pos.y + change.y)
                if (grid[next] == '#') {
                    if (change == away) {
                        change = east
                    } else if (change == east) {
                        change = back
                    }
                }
                pos = Point2D(pos.x + change.x, pos.y + change.y)
                if (grid[pos] == '@') {
                    ore.add(pos)
                }
                steps++
            } while (pos.y != start.y)
            if (ore.isNotEmpty()) {
                val edge = Edge(start.x, facing, pos.x, steps, ore.size)
                uniqueOre[edge] = ore
                return edge
            }
            return null
        }
    }
}

data class Edge(val start: Int, val startFacing: Direction, val end: Int, val steps: Int, val ore: Int) {
    val value = (ore * 100.0 / steps).roundToInt() / 100.0
    val endFacing = when (startFacing) {
        Direction.NORTH -> {
            if (start == end) {
                Direction.NORTH
            } else {
                Direction.SOUTH
            }
        }
        Direction.SOUTH -> {
            if (start == end) {
                Direction.SOUTH
            } else {
                Direction.NORTH
            }
        }
        else -> startFacing
    }

    override fun toString() = "$start,$startFacing to $end,$endFacing steps=$steps ore=$ore ($value)"

    fun canEnter(cartFacing: Direction): Boolean {
        return (startFacing.reverse() != cartFacing)
    }

    fun reverse(): Edge {
        val newStartFacing = when (startFacing) {
            Direction.NORTH -> {
                if (start == end) {
                    Direction.SOUTH
                } else {
                    Direction.NORTH
                }
            }
            Direction.SOUTH -> {
                if (start == end) {
                    Direction.NORTH
                } else {
                    Direction.SOUTH
                }
            }
            Direction.WEST -> Direction.EAST
            else -> Direction.WEST
        }
        return Edge(end, newStartFacing, start, steps, ore)
    }
}

data class State1(val x: Int, val facing: Direction, val steps: Int, val ore: MutableSet<Point2D<Int>>) {
    override fun toString() = "$x,$facing steps=$steps ore=$ore"
}
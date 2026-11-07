package buri.ec.e2026.q10

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Direction
import buri.ec.common.position.Direction.*
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
        assertRun(29, 1)
        assertRun(51, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(50000, 1)
        assertRun(141855, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(13314695352588, 1)
        assertRun(14620833332805, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val walls = grid.filter { it == '#' }
        val ghosts = mutableSetOf<Ghost>()
        for (point in grid.filter { it in listOf('<', '>', '^', 'v') }) {
            val direction = when (grid[point]) {
                '<' -> WEST
                '>' -> EAST
                '^' -> NORTH
                else -> SOUTH
            }
            ghosts.add(Ghost(point, direction))
        }
        val threshold = if (part.isTwo()) {
            10_000
        } else {
            1_000_000_000_000
        }

        var round = 0L
        val visited = mutableMapOf<String, Pair<Long, List<Snapshot>>>()
        while (true) {
            // Handle cycle detection
            if (part.isThree()) {
                if (ghosts.toString() in visited.keys) {
                    val (firstCycle, snapshots) = visited[ghosts.toString()]!!

                    // Find the number of repeats that won't cross any thresholds.
                    val ghostStates = mutableMapOf<Ghost, Pair<Long, Long>>()
                    for (ghost in ghosts) {
                        val change = ghost.collisions - snapshots.first { it.id == ghost.id }.collisions
                        val remaining = threshold - ghost.collisions
                        ghostStates[ghost] = Pair(change, remaining)
                    }
                    val repeats = ghostStates.values.minOfOrNull { (it.second - 1) / it.first }!!

                    for (ghost in ghostStates.keys) {
                        ghost.collisions += ghostStates[ghost]!!.first * repeats
                    }
                    val cycleLength = round - firstCycle
                    round += cycleLength * repeats
                }
                visited[ghosts.toString()] = Pair(round, ghosts.map { it.toSnapshot() })
            }

            // Move ghosts
            ghosts.handleWallCollisions(walls)
            ghosts.removeIf { it.collisions >= threshold }
            ghosts.handleGhostCollisions()
            ghosts.removeIf { it.collisions >= threshold }
            round++

            // Exit conditions
            if (part.isOne() && round == 10L) {
                return ghosts.sumOf { it.collisions }
            }
            if (!part.isOne() && ghosts.isEmpty()) {
                return round
            }
        }
    }

    fun Set<Ghost>.handleWallCollisions(walls: List<Point2D<Int>>) {
        for (ghost in this) {
            val nextPos = ghost.peekMove()
            if (nextPos in walls) {
                ghost.turnRight()
                ghost.collisions++
            } else {
                ghost.move()
            }
        }
    }

    fun Set<Ghost>.handleGhostCollisions() {
        for (ghost in this) {
            if (this.count { it.coords == ghost.coords } > 1) {
                ghost.collisions++
                ghost.turnAround()
            }
        }
    }
}

data class Ghost(private val initialPos: Point2D<Int>, private val initialDirection: Direction) {
    val id = initialPos.toString()

    var coords = initialPos
    var facing = initialDirection
    var collisions = 0L

    fun toSnapshot(): Snapshot = Snapshot(id, collisions)
    override fun toString() = "($coords $facing)"

    fun peekMove(): Point2D<Int> {
        return when (facing) {
            NORTH -> coords.copy(y = coords.y - 1)
            EAST -> coords.copy(x = coords.x + 1)
            SOUTH -> coords.copy(y = coords.y + 1)
            WEST -> coords.copy(x = coords.x - 1)
        }
    }

    fun move() {
        coords = peekMove()
    }

    fun turnRight() {
        facing = when (facing) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    fun turnAround() {
        facing = when (facing) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

data class Snapshot(val id: String, val collisions: Long)
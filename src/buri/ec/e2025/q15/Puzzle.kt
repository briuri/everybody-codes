package buri.ec.e2025.q15

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Direction
import buri.ec.common.position.MutablePosition
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
        assertRun(16, 1)
        assertRun(128, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(3805, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(543877123, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val start = Point2D(0L, 0L)
        val firstCorner = when (input[0].first()) {
            'L' -> Point2D(-1L, 0L)
            else -> Point2D(1L, 0L)
        }
        val corners = mutableListOf(firstCorner)
        val pos = MutablePosition(start, Direction.NORTH)
        for (instruction in input[0].split(",")) {
            val turn = instruction.first()
            val length = instruction.drop(1).toLong()
            when (turn) {
                'L' -> pos.turnLeft()
                else -> pos.turnRight()
            }
            pos.move(length)
            corners.add(pos.coords)
        }
        val end = corners.removeLast()
        pos.turnAround()
        pos.move()
        corners.add(pos.coords)

        val walls = mutableListOf<Wall>()
        for (i in 0..<corners.lastIndex) {
            walls.add(Wall.fromInput(corners[i], corners[i + 1]))
        }

        val moves = mutableSetOf(start, end)
        for (corner in corners) {
            moves.add(Point2D(corner.x - 1, corner.y - 1))
            moves.add(Point2D(corner.x + 1, corner.y - 1))
            moves.add(Point2D(corner.x - 1, corner.y + 1))
            moves.add(Point2D(corner.x + 1, corner.y + 1))
        }

        val frontier = ArrayDeque<State>()
        frontier.add(State(start, 0L))
        val bestSteps = mutableMapOf<Point2D<Long>, Long>()

        var current: State?
        while (frontier.isNotEmpty()) {
            current = frontier.removeFirst()
            if (current.coords !in bestSteps || bestSteps[current.coords]!! > current.steps) {
                bestSteps[current.coords] = current.steps
                for (move in moves.filter {
                    it != current.coords && walls.none { it2 ->
                        it2.intersects(
                            current.coords,
                            it
                        )
                    }
                }) {
                    frontier.add(State(move, current.steps + current.coords.getManhattanDistance(move)))
                }
            }
            frontier.sortBy { it.steps }
        }
        return bestSteps[end]!!
    }
}

data class State(val coords: Point2D<Long>, val steps: Long)
data class Wall(val start: Point2D<Long>, val end: Point2D<Long>) {

    override fun toString(): String = "[$start,$end]"

    fun intersects(pathStart: Point2D<Long>, pathEnd: Point2D<Long>): Boolean {
        val o1 = getOrientation(start, end, pathStart)
        val o2 = getOrientation(start, end, pathEnd)
        val o3 = getOrientation(pathStart, pathEnd, start)
        val o4 = getOrientation(pathStart, pathEnd, end)
        if (o1 != 0 && o1 != o2 && o3 != 0 && o3 != o4) {
            return true
        }

        // Special cases (collinear points)
        // p1, q1, p2 are collinear and p2 lies on segment p1q1
        if (o1 == 0 && isOnSegment(start, pathStart, end)) {
            return true
        }
        // p1, q1, q2 are collinear and q2 lies on segment p1q1
        if (o2 == 0 && isOnSegment(start, pathEnd, end)) {
            return true
        }
        // p2, q2, p1 are collinear and p1 lies on segment p2q2
        if (o3 == 0 && isOnSegment(pathStart, start, pathEnd)) {
            return true
        }
        // p2, q2, q1 are collinear and q1 lies on segment p2q2
        if (o4 == 0 && isOnSegment(pathStart, end, pathEnd)) {
            return true
        }
        return false
    }

    fun getOrientation(p: Point2D<Long>, q: Point2D<Long>, r: Point2D<Long>): Int {
        val value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
        // Collinear
        if (value == 0L) {
            return 0
        }
        // CW / CCW
        return if (value > 0L) {
            1
        } else {
            2
        }
    }

    fun isOnSegment(p: Point2D<Long>, q: Point2D<Long>, r: Point2D<Long>): Boolean {
        return q.x <= maxOf(p.x, r.x) && q.x >= minOf(p.x, r.x) &&
                q.y <= maxOf(p.y, r.y) && q.y >= minOf(p.y, r.y)
    }

    companion object {
        fun fromInput(first: Point2D<Long>, second: Point2D<Long>): Wall {
            val list = mutableListOf(first, second)
            if (first.x != second.x && first.y != second.y) {
                throw Exception("Diagonal walls not allowed.")
            }
            list.sort()
            return Wall(list.first(), list.last())
        }
    }
}
package buri.ec.e2026.q08

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.position.Point2D
import buri.ec.common.position.Point3D
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(32, 1)
        assertRun(166, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(187, 1)
        assertRun(1018, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(899889, 1)
        assertRun(899301, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val radius = input[0].length / 4
        val grid = Grid(radius)
        for (y in input.indices) {
            for (x in input[0].indices) {
                val ent = input[y][x]
                if (ent != '.') {
                    grid[Point2D(x, y)] = ent.digitToInt()
                }
            }
        }

        if (part.isOne()) {
            return grid.shine(part, Direction.SE)
        }
        if (part.isTwo()) {
            return Direction.entries.sumOf { grid.shine(part, it) }
        }

        val directionQueue = Direction.entries.toMutableList()
        var energy = 0L
        while (true) {
            val direction = directionQueue.removeFirst()
            directionQueue.add(direction)
            val roundEnergy = grid.shine(part, direction)
            energy += roundEnergy
            if (grid.entsGrown >= 100_000) {
                return energy
            }
        }
    }
}

data class Grid(val radius: Int) {
    private val map = mutableMapOf<Point3D<Int>, Int>()
    private val nwCorner = Point3D(0, -radius, radius)
    private val neCorner = Point3D(radius, -radius, 0)
    private val wCorner = Point3D(-radius, 0, radius)
    private val eCorner = Point3D(radius, 0, -radius)
    private val swCorner = Point3D(-radius, radius, 0)
    private val seCorner = Point3D(0, radius, -radius)
    private val edgeCache = mutableMapOf<Direction, Set<Point3D<Int>>>()

    var entsGrown = 0

    fun shine(part: Part, direction: Direction): Int {
        var energy = 0
        for (sunSpot in getSunSpots(direction)) {
            energy += getEnergy(part, sunSpot, direction)
        }
        return energy
    }

    private fun getEnergy(part: Part, sunSpot: Point3D<Int>, direction: Direction): Int {
        var entsHit = 0
        var current = sunSpot
        var shadow = this[current]!!
        while (this.contains(current)) {
            val entHeight = this[current]!!
            if (entHeight > 0 && entHeight >= shadow) {
                entsHit++
                shadow = entHeight
                if (part.isThree()) {
                    if (entHeight == 9) {
                        this[current] = -2
                        entsGrown++
                    } else {
                        this[current] = entHeight + 1
                    }
                }
            } else {
                shadow--
                if (part.isThree() && entHeight <= 0) {
                    this[current] = entHeight + 1
                }
            }
            current = direction.move(current)
        }
        return entsHit
    }

    private fun getSunSpots(direction: Direction): Set<Point3D<Int>> {
        if (direction !in edgeCache) {
            val spots = mutableSetOf<Point3D<Int>>()
            when (direction) {
                Direction.NW -> {
                    spots.addAll(getEdge(eCorner, seCorner))
                    spots.addAll(getEdge(seCorner, swCorner))
                }
                Direction.NE -> {
                    spots.addAll(getEdge(seCorner, swCorner))
                    spots.addAll(getEdge(swCorner, wCorner))
                }
                Direction.W -> {
                    spots.addAll(getEdge(neCorner, eCorner))
                    spots.addAll(getEdge(eCorner, seCorner))
                }
                Direction.E -> {
                    spots.addAll(getEdge(nwCorner, wCorner))
                    spots.addAll(getEdge(wCorner, swCorner))
                }
                Direction.SW -> {
                    spots.addAll(getEdge(nwCorner, neCorner))
                    spots.addAll(getEdge(neCorner, eCorner))
                }
                else -> {
                    spots.addAll(getEdge(wCorner, nwCorner))
                    spots.addAll(getEdge(nwCorner, neCorner))
                }
            }
            edgeCache[direction] = spots
        }
        return edgeCache[direction]!!
    }

    private fun getEdge(p1: Point3D<Int>, p2: Point3D<Int>): Set<Point3D<Int>> {
        val edge = mutableSetOf<Point3D<Int>>()
        val dx = if (p2.x > p1.x) {
            1
        } else if (p2.x < p1.x) {
            -1
        } else {
            0
        }
        val dy = if (p2.y > p1.y) {
            1
        } else if (p2.y < p1.y) {
            -1
        } else {
            0
        }
        val dz = if (p2.z > p1.z) {
            1
        } else if (p2.z < p1.z) {
            -1
        } else {
            0
        }
        var current = p1
        while (this.contains(current)) {
            edge.add(current)
            current = Point3D(current.x + dx, current.y + dy, current.z + dz)
        }
        return edge
    }

    operator fun get(point: Point3D<Int>): Int? = map[point]
    operator fun set(point: Point3D<Int>, value: Int) {
        map[point] = value
    }

    operator fun set(point: Point2D<Int>, value: Int) {
        val hexX = (point.x - point.y - radius) / 2
        val hexY = point.y - radius
        val hexZ = (hexX + hexY) * -1
        map[Point3D(hexX, hexY, hexZ)] = value
    }

    fun contains(point: Point3D<Int>): Boolean = map.containsKey(point)
}

enum class Direction(val change: Point3D<Int>) {
    SE(Point3D(0, 1, -1)),
    SW(Point3D(-1, 1, 0)),
    W(Point3D(-1, 0, 1)),
    NW(Point3D(0, -1, 1)),
    NE(Point3D(1, -1, 0)),
    E(Point3D(1, 0, -1));

    fun move(p: Point3D<Int>): Point3D<Int> {
        return Point3D(p.x + change.x, p.y + change.y, p.z + change.z)
    }
}
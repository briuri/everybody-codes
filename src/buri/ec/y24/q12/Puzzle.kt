package buri.ec.y24.q12

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
        assertRun(13, true)
        assertRun(180, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(22, true)
        assertRun(20490, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(13, true)
        assertRun(745995, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = if (part.isThree()) {
            null
        } else {
            Grid.fromCharInput(input)
        }
        val groundY = if (part.isThree()) {
            3
        } else {
            grid!!.filter { it == '=' }.first().y
        }
        val a = if (part.isThree()) {
            Point2D(0, 2)
        } else {
            grid!!.filter { it == 'A' }.first()
        }
        val b = if (part.isThree()) {
            Point2D(0, 1)
        } else {
            grid!!.filter { it == 'B' }.first()
        }
        val c = if (part.isThree()) {
            Point2D(0, 0)
        } else {
            grid!!.filter { it == 'C' }.first()
        }
        val powerThreshold = if (part.isThree()) {
            input.maxOf { it.extractInts().last() }
        } else {
            40
        }

        val paths = mutableMapOf<Char, MutableMap<Int, List<Point2D<Int>>>>()
        paths['A'] = mutableMapOf()
        paths['B'] = mutableMapOf()
        paths['C'] = mutableMapOf()
        for (power in 1..powerThreshold) {
            paths['A']!![power] = getProjectilePath(a, power, groundY)
            paths['B']!![power] = getProjectilePath(b, power, groundY)
            paths['C']!![power] = getProjectilePath(c, power, groundY)
        }

        if (!part.isThree()) {
            var rankingValue = 0
            for (target in grid!!.filter { it in listOf('T', 'H') }) {
                val hardScore = if (grid[target] == 'H') 2 else 1
                val tries = mutableSetOf<Pair<Int, Int>?>()
                tries.add(paths.findPower('A', target))
                tries.add(paths.findPower('B', target))
                tries.add(paths.findPower('C', target))
                val (segment, power) = tries.filterNotNull().first()
                rankingValue += segment * power * hardScore
            }
            return rankingValue
        }

        // Part Three
        val meteors = mutableListOf<Point2D<Int>>()
        for (meteor in input) {
            val numbers = meteor.extractInts()
            meteors.add(Point2D(a.x + numbers[0], a.y - numbers[1]))
        }
        var rankingValue = 0
        for (meteor in meteors) {
            val meteorPath = getMeteorPath(meteor, groundY)
            val tries = mutableSetOf<Pair<Int, Int>?>()
            tries.add(paths.findPower('A', meteorPath))
            tries.add(paths.findPower('B', meteorPath))
            tries.add(paths.findPower('C', meteorPath))
            rankingValue += tries.filterNotNull().minOf { it.first * it.second }
        }
        return rankingValue
    }

    /**
     * Returns the lowest possible power that can strike the target from the starting point. Returns null
     * if the catapult cannot strike at all.
     */
    private fun Map<Char, Map<Int, List<Point2D<Int>>>>.findPower(
        startChar: Char,
        target: Point2D<Int>
    ): Pair<Int, Int>? {
        val segment = when (startChar) {
            'A' -> 1
            'B' -> 2
            else -> 3
        }
        for (powerPaths in this[startChar]!!) {
            if (target in powerPaths.value) {
                return Pair(segment, powerPaths.key)
            }
        }
        return null
    }

    /**
     * Returns the lowest possible power that can strike the meteor from the starting point. Returns null
     * if the catapult cannot strike at all.
     */
    private fun Map<Char, Map<Int, List<Point2D<Int>>>>.findPower(
        startChar: Char,
        meteorPath: List<Point2D<Int>>
    ): Pair<Int, Int>? {
        if (meteorPath.isNotEmpty()) {
            val segment = when (startChar) {
                'A' -> 1
                'B' -> 2
                else -> 3
            }
            for (powerPaths in this[startChar]!!) {
                val projectilePath = powerPaths.value
                val intersection = projectilePath.intersect(meteorPath.toSet())
                if (intersection.isNotEmpty()) {
                    val pIndex = projectilePath.indexOf(intersection.last())
                    val mIndex = meteorPath.indexOf(intersection.last())
                    if (pIndex == mIndex || pIndex + 1 == mIndex) {
                        return Pair(segment, powerPaths.key)
                    }
                }
            }
        }
        return null
    }

    /**
     * Gets the path of a projectile.
     */
    private fun getProjectilePath(start: Point2D<Int>, power: Int, groundY: Int): List<Point2D<Int>> {
        val path = mutableListOf<Point2D<Int>>()
        var current = start
        path.add(current)
        for (i in 0..<power) {
            current = current.copy(x = current.x + 1, y = current.y - 1)
            path.add(current)
        }
        for (i in 0..<power) {
            current = current.copy(x = current.x + 1)
            path.add(current)
        }
        for (i in 0..<power) {
            current = current.copy(x = current.x + 1, y = current.y + 1)
            path.add(current)
        }
        while (current.y < groundY) {
            current = current.copy(x = current.x + 1, y = current.y + 1)
            path.add(current)
        }
        return path.dropLast(1)
    }

    /**
     * Gets the path of a meteor.
     */
    private fun getMeteorPath(start: Point2D<Int>, groundY: Int): List<Point2D<Int>> {
        val path = mutableListOf<Point2D<Int>>()
        var current = start
        path.add(current)
        while (current.y < groundY) {
            current = current.copy(x = current.x - 1, y = current.y + 1)
            path.add(current)
        }
        return path.dropLast(1)
    }
}
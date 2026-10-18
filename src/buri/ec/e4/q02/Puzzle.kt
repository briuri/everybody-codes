package buri.ec.e4.q02

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import buri.ec.common.position.Point2D
import buri.ec.common.position.getNeighbors
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(8, 1)
        assertRun(71, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(25, 1)
        assertRun(46, 2)
        assertRun(1669, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(42, 1)
        assertRun(432, 2)
        assertRun(0, 0, true)
    }
    
    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rawStart = input[0].extractInts()
        val rawA = input[1].extractInts()
        val rawB = input[2].extractInts()
        val rawC = input[3].extractInts()
        
        val start = Point2D(rawStart[0], rawStart[1])
        val beacons = mutableMapOf<Char, Point2D<Int>>()
        beacons['A'] = Point2D(rawA[0], rawA[1])
        beacons['B'] = Point2D(rawB[0], rawB[1])
        beacons['C'] = Point2D(rawC[0], rawC[1])
        val beaconIds = if (part.isThree()) { "" } else { input[4].split("=")[1] }
        
        var pos = start
        val visited = mutableListOf<Point2D<Int>>()
        visited.add(start)
        if (!part.isThree()) {
            for (beaconId in beaconIds) {
                val beacon = beacons[beaconId]!!
                val dx = (pos.x - beacon.x) / 2.0
                val dy = (pos.y - beacon.y) / 2.0
                pos = Point2D((pos.x - dx).toInt(), (pos.y - dy).toInt())
                visited.add(pos)
            }
        }
        else {
            // TODO: Deterministically find limit of Sierpinski triangle points and add to visited set.
        }
        
        if (part.isOne()) {
            return visited.toSet().size
        } else {
            val neighbors = mutableSetOf<Point2D<Int>>()
            for (pos in visited) {
                neighbors.addAll(pos.getNeighbors().filter { it !in visited })
            }
            return neighbors.size
        }
    }

}
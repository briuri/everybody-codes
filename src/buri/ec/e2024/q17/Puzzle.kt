package buri.ec.e2024.q17

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
        assertRun(16, true)
        assertRun(135, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(1292, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(15624, true)
        assertRun(5452477422, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val grid = Grid.fromCharInput(input)
        val stars = grid.filter { it == '*' }.toSet()
        if (!part.isThree()) {
            return getSize(stars)
        }

        val starBags = mutableListOf<MutableSet<Point2D<Int>>>()
        val brilliantMds = stars.getMds().filter { it.key < 6 }
        for (md in brilliantMds.keys.sorted()) {
            for (mdStars in brilliantMds[md]!!) {
                val bagA = starBags.getBagWith(mdStars.first)
                val bagB = starBags.getBagWith(mdStars.second)
                if (bagA == bagB) {
                    continue
                }
                bagA.addAll(bagB)
                bagB.clear()
            }
        }

        val sizes = starBags.filter { it.isNotEmpty() }.map { getSize(it) }.sortedDescending()
        var product = 1L
        for (size in sizes.subList(0, 3)) {
            product *= size
        }
        return (product)
    }

    /**
     * Builds a constellation with all the stars.
     */
    private fun getSize(stars: Set<Point2D<Int>>): Long {
        var distances = 0L
        val starBags = mutableListOf<MutableSet<Point2D<Int>>>()
        val mds = stars.getMds()
        for (md in mds.keys.sorted()) {
            for (mdStars in mds[md]!!) {
                val bagA = starBags.getBagWith(mdStars.first)
                val bagB = starBags.getBagWith(mdStars.second)
                if (bagA == bagB) {
                    continue
                }
                bagA.addAll(bagB)
                bagB.clear()
                distances += md
            }
        }
        return (stars.size + distances)
    }

    /**
     * Calculates MDs for all stars
     */
    private fun Set<Point2D<Int>>.getMds(): Map<Long, List<Pair<Point2D<Int>, Point2D<Int>>>> {
        val mds = mutableMapOf<Long, MutableList<Pair<Point2D<Int>, Point2D<Int>>>>()
        for (starA in this) {
            for (starB in this.filter { it != starA }) {
                val md = starA.getManhattanDistance(starB)
                mds.putIfAbsent(md, mutableListOf())
                mds[md]!!.add(Pair(starA, starB))
            }
        }
        return mds
    }

    /**
     * Finds the bag containing a star, or creates a new bag.
     */
    private fun MutableList<MutableSet<Point2D<Int>>>.getBagWith(star: Point2D<Int>): MutableSet<Point2D<Int>> {
        var bag = this.firstOrNull { star in it }
        if (bag == null) {
            bag = mutableSetOf()
            bag.add(star)
            this.add(bag)
        }
        return bag
    }
}
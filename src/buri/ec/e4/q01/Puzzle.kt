package buri.ec.e4.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(66, 1)
        assertRun(34, 2)
        assertRun(298, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(43, 1)
        assertRun(32711, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(27, 1)
        assertRun(35, 2)
        assertRun(40212, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val finalSpots = mutableListOf<Int>()
        for (line in input) {
            val steps = line.extractInts()

            var current = 0
            val visited = mutableListOf<Int>()
            visited.add(current)
            for (step in steps) {
                val next = getNext(part, current, step, visited)
                // Don't record part 3 steps where we ignore a jump entirely
                if (current != next) {
                    visited.add(next)
                }
                current = next
            }
            finalSpots.add(current)
        }
        return finalSpots.sum()
    }

    fun getNext(part: Part, current: Int, step: Int, visited: MutableList<Int>): Int {
        var test = current - step
        if (part.isOne()) {
            if (test < 0 || test in visited) {
                test = current + step
            }
            return test
        }
        if (part.isTwo()) {
            if (test < 0 || test in visited) {
                test = current + step
                while (test in visited) {
                    test++
                }
            }
        } else {
            val isNextBottomArc = (visited.size % 2 == 1)
            val startIndex = if (isNextBottomArc) {
                0
            } else {
                1
            }
            val arcs = mutableListOf<Pair<Int, Int>>()
            for (index in startIndex..<visited.lastIndex step 2) {
                val first = visited[index]
                val second = visited[index + 1]
                if (first <= second) {
                    arcs.add(Pair(first, second))
                } else {
                    arcs.add(Pair(second, first))
                }
            }

            // First try to move backwards by its specified length. 
            //      If the destination is > 0, not visited before, and does not cross an existing arc, proceed.
            if (test < 0 || test in visited || crossesArc(arcs, Pair(test, current))) {
                // Otherwise move forward by the same distance.
                //      If the destination has been visited, increment by 1 until an unvisited spot is reached.
                //      If the path crosses an existing arc, increment by 1 until no arcs are crossed.
                //      If no valid jumps exist, skip this jump completely and continue with the next in sequence.
                test = current + step
                while (test in visited) {
                    test++
                }
                while (crossesArc(arcs, Pair(current, test))) {
                    test++
                    // Will always be crossing an arc if we continue past max() + 1.
                    if (test >= visited.max() + 2) {
                        return current
                    }
                }
            }
        }
        return test
    }

    fun crossesArc(arcs: List<Pair<Int, Int>>, testArc: Pair<Int, Int>): Boolean {
        for ((first, second) in arcs) {
            val isLeft = testArc.first <= first || testArc.second <= first
            val isRight = testArc.first >= second || testArc.second >= second
            val isBetween = testArc.first in first..second || testArc.second in first..second
            if (isBetween && (isLeft || isRight)) {
                return true
            }
        }
        return false
    }
}
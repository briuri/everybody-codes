package buri.ec.e2026.q16

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(3, 1)
        assertRun(26, 2)
        assertRun(57, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(1053, 1)
        assertRun(24893541, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(135154, 1)
        assertRun(24751430435972, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val rawTablets = input[0].chunked(1).map { it[0] }.toMutableList()
        val repeats = if (part.isOne()) {
            1
        } else if (part.isTwo()) {
            10
        } else {
            100
        }
        val (numFlips, tabletsMoved) = sort(rawTablets, repeats)
        return if (part.isOne()) {
            numFlips
        } else {
            tabletsMoved
        }
    }

    fun sort(rawTablets: List<Char>, repeats: Int): Pair<Long, Long> {
        var tablets = ArrayDeque<Char>()
        repeat(repeats) {
            tablets.addAll(rawTablets)
        }

        var numFlips = 0L
        var tabletsMoved = 0L
        while (tablets.isNotEmpty()) {
            val current = tablets.first()
            val remaining = ArrayDeque<Char>()
            var inReverse = true
            flip(tablets, remaining, false, current)
            while (tablets.isNotEmpty()) {
                tabletsMoved += tablets.size
                numFlips++
                flip(tablets, remaining, inReverse, current)                
                inReverse = !inReverse
            }
            tablets = remaining
        }
        return Pair(numFlips, tabletsMoved)
    }
    
    fun flip(tablets: ArrayDeque<Char>, remaining: ArrayDeque<Char>, inReverse: Boolean, current: Char) {
        if (inReverse) {
            while (tablets.isNotEmpty() && current == tablets.last()) {
                tablets.removeLast()
            }
            while (tablets.isNotEmpty() && current != tablets.first()) {
                remaining.addFirst(tablets.removeFirst())
            }
        } else {
            while (tablets.isNotEmpty() && current == tablets.first()) {
                tablets.removeFirst()
            }
            while (tablets.isNotEmpty() && current != tablets.last()) {
                remaining.addFirst(tablets.removeLast())
            }
        }
    }
}
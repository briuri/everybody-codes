package buri.ec.e2025.q08

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
import org.junit.Test
import kotlin.math.abs

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(4, 1)
        assertRun(61, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(21, 1)
        assertRun(2924895, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(7, 1)
        assertRun(2791, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val nails = input[0].extractInts()

        if (part.isOne()) {
            var count = 0
            // Account for the smaller example
            val center = if (nails.size == 9) {
                4
            } else {
                16
            }
            for (i in 0..<nails.lastIndex) {
                if (abs(nails[i + 1] - nails[i]) == center) {
                    count++
                }
            }
            return count
        }

        val edges = mutableListOf<Edge>()
        for (i in 0..<nails.lastIndex) {
            edges.add(Edge.fromInput(nails[i], nails[i + 1]))
        }
        if (part.isTwo()) {
            var count = 0
            for (edge in edges) {
                for (otherEdge in edges.filter { it != edge }) {
                    if (edge.intersects(otherEdge)) {
                        count++
                    }
                }
            }
            // Each knot counted twice.
            return (count / 2)
        } else {
            // Account for the smaller example
            val size = if (nails.size == 10) {
                8
            } else {
                256
            }
            var maxCuts = 0
            val triedSwords = mutableSetOf<Edge>()
            for (start in 1..size) {
                for (end in 1..size) {
                    if (start == end) {
                        continue
                    }
                    val sword = Edge.fromInput(start, end)
                    if (sword !in triedSwords) {
                        val cuts = edges.count { it.intersects(sword) } + edges.count { it == sword }
                        maxCuts = maxCuts.coerceAtLeast(cuts)
                        triedSwords.add(sword)
                    }
                }
            }
            return maxCuts
        }
    }
}

data class Edge(val start: Int, val end: Int) {

    override fun toString(): String = "[$start,$end]"

    fun intersects(edge: Edge): Boolean {
        val oneWay = (edge.start in (start + 1)..<end) && (edge.end !in start..end)
        val otherWay = (edge.end in (start + 1)..<end) && (edge.start !in start..end)
        return (oneWay || otherWay)
    }

    companion object {
        fun fromInput(first: Int, second: Int): Edge {
            val list = mutableListOf(first, second)
            list.sort()
            return Edge(list.first(), list.last())
        }
    }
}
package buri.ec.e2024.q06

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
        assertRun("RRB@", true)
        assertRun("RRVKKPGTLZTX@", false, true)
    }

    @Test
    fun runPart2() {
        assertRun("RLFKHKDZRC@", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("RCDSHBSVDPRG@", false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val branches = mutableMapOf<String, List<String>>()
        for (line in input) {
            val id = line.split(":")[0]
            val children = line.split(":")[1].split(",")
            branches[id] = children
        }
        val finishedPaths = mutableSetOf<String>()
        branches.visit(finishedPaths, "RR-", branches["RR"]!!)

        val lengths = mutableMapOf<Int, MutableList<String>>()
        for (path in finishedPaths) {
            if (path.length !in lengths) {
                lengths[path.length] = mutableListOf()
            }
            lengths[path.length]!!.add(path)
        }
        val path = lengths.filter { it.value.size == 1 }.values.first().first()
        if (part.isOne()) {
            return path.replace("-", "")
        }
        return path.split("-").map { it.first() }.joinToString("")
    }

    /**
     * Recursively visits down the tree until it reaches a fruit.
     */
    private fun MutableMap<String, List<String>>.visit(
        finishedPaths: MutableSet<String>,
        pathSoFar: String,
        children: List<String>
    ) {
        for (child in children) {
            if (child in listOf("BUG", "ANT")) {
                continue
            }
            if (child == "@" || this[child] == null) {
                finishedPaths.add("$pathSoFar$child")
            } else {
                this.visit(finishedPaths, "$pathSoFar$child-", this[child]!!)
            }
        }
    }
}

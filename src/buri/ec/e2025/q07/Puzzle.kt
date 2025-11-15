package buri.ec.e2025.q07

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
        assertRun("Oroneth", 1)
        assertRun("Azardith", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("23", 1)
        assertRun("3128", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("1154", 1)
        assertRun("2137846", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val rules = mutableMapOf<Char, List<Char>>()
        for (line in input.drop(2)) {
            val tokens = line.split(" > ")
            rules[tokens[0][0]] = tokens[1].split(",").map { it[0] }
        }
        if (!part.isThree()) {
            var sum = 0
            for ((index, name) in input[0].split(",").withIndex()) {
                if (matchesRules(rules, name)) {
                    if (part.isOne()) {
                        return name
                    }
                    sum += (index + 1)
                }
            }
            return sum.toString()
        }

        val allNames = mutableSetOf<String>()
        for (name in input[0].split(",")) {
            if (!matchesRules(rules, name)) {
                continue
            }
            var names = mutableSetOf<String>()
            names.add(name)
            repeat(11 - name.length) {
                val newNames = mutableSetOf<String>()
                for (name in names) {
                    newNames.add(name)
                    val rule = rules[name.last()]
                    if (rule != null) {
                        for (next in rule) {
                            newNames.add("$name$next")
                        }
                    }
                }
                names = newNames
            }
            allNames.addAll(names)
        }
        return allNames.count { it.length in 7..11 }.toString()
    }

    fun matchesRules(rules: Map<Char, List<Char>>, name: String): Boolean {
        for (i in 0..<name.lastIndex) {
            val letter = name[i]
            val next = rules[letter]!!
            if (name[i + 1] !in next) {
                return false
            }
        }
        return true
    }
}
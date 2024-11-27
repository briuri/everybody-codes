package buri.ec.common

import org.junit.Assert.assertEquals
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.util.*

/**
 * Base class with shared functionality for all puzzles
 *
 * @author Brian Uri!
 */
abstract class BasePuzzle {

    /**
     * Abstract function for running a part of a puzzle
     */
    abstract fun run(part: Part, input: List<String>): Any

    /**
     * Runs part of a puzzle and compares the result to the expected value.
     */
    fun assertRun(expected: Any, isExample: Boolean, toConsole: Boolean = false) {
        val year = getYear()
        val quest = getQuest()
        val part = when (getPart()) {
            "1" -> Part.ONE
            "2" -> Part.TWO
            else -> Part.THREE
        }

        val fileSuffix = if (isExample) "-ex" else ""
        val path = "data/y${year}/everybody_codes_e20${year}_q${quest}_p${part.number}$fileSuffix.txt"
        val input = File(path).readLines()

        val actual = this.run(part, input)
        if (toConsole) {
            toConsole(actual)
        }
        if (actual is Long && expected is Int) {
            assertEquals(expected.toLong(), actual)
        } else {
            assertEquals(expected, actual)
        }
    }

    /**
     * Saves the answer to the console and the system clipboard.
     */
    private fun toConsole(value: Any) {
        val year = getYear()
        val quest = getQuest()
        val part = getPart()
        println("### 20$year Quest $quest Part $part ###")
        println(value)

        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(value.toString()), null)
    }

    /**
     * Extracts the puzzle year from the package name
     */
    private fun getYear(): String {
        with(this.javaClass) {
            return name.substring(name.indexOf(".y") + 2, name.indexOf(".q"))
        }
    }

    /**
     * Extracts the puzzle quest number from the package name
     */
    private fun getQuest(): String {
        with(this.javaClass) {
            return name.substring(name.indexOf(".q") + 2, name.indexOf(".Puzzle"))
        }
    }

    /**
     * Extracts the puzzle part from the test method name
     */
    private fun getPart(): String {
        val trace = Thread.currentThread().stackTrace
        var testName = ""
        for (stackTraceElement in trace) {
            if (stackTraceElement.methodName.contains("runPart")) {
                testName = stackTraceElement.methodName
                break
            }
        }
        return testName.substring(testName.indexOf("Part") + 4)
    }
}

/**
 * Generates every permutation for a list of objects.
 */
fun <T> generatePermutations(input: List<T>, index: Int = 0): MutableSet<String> {
    val permutations = mutableSetOf<String>()
    if (index == input.lastIndex) {
        permutations.add(input.toList().joinToString(","))
    }
    for (i in index..input.lastIndex) {
        Collections.swap(input, index, i)
        permutations.addAll(generatePermutations(input, index + 1))
        Collections.swap(input, i, index)
    }
    return permutations
}

/**
 * Enumeration of puzzle parts
 */
enum class Part(val number: Int) {
    ONE(1), TWO(2), THREE(3);

    /**
     * Returns true if this is Part ONE.
     */
    fun isOne(): Boolean = (number == 1)

    /**
     * Returns true if this is Part TWO.
     */
    fun isTwo(): Boolean = (number == 2)

    /**
     * Returns true if this is Part THREE.
     */
    fun isThree(): Boolean = (number == 3)
}

/**
 * Extension function for extracting the numbers out of a line of input.
 */
fun String.extractLongs(allowNegative: Boolean = true): List<Long> {
    val pattern = if (allowNegative) "[^0-9\\-]" else "[^0-9]"
    val string = this.replace(pattern.toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
    return (string.split(" ").map { it.toLong() })
}

fun String.extractInts(allowNegative: Boolean = true): List<Int> {
    return this.extractLongs(allowNegative).map { it.toInt() }
}
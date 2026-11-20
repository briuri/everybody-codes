package buri.ec.e2026.q19

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.generatePermutations
import buri.ec.common.position.Grid
import org.junit.Test
import kotlin.math.max

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(144, 1)
        assertRun(3314196, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(180, 1)
        assertRun(277156455000, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(8000, 3)
        assertRun(2043870193890, 0, true)
    }

    val originalColors = listOf('M', 'Y', 'C', 'K')
    val allColors = originalColors + listOf('R', 'G', 'B')
    
    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val endGridLine = if (!part.isThree()) {
            input.indexOf("")
        } else {
            input.size
        }
        val gridA = Grid.fromCharInput(input.subList(0, endGridLine))
        val gridB = Grid.fromCharInput(input.subList(input.indexOf("") + 1, input.size))
        
        if (part.isOne()) {
            val gridC = Grid(gridA.width, gridB.height, '.')
            for (y in gridA.yRange) {
                for (x in gridA.xRange) {
                    gridC[x, y] = replaceColor(gridA[x, y], gridB[x, y])
                }
            }
            val colorCounts = mutableMapOf<Char, Int>()
            allColors.forEach { colorCounts[it] = gridC.count { it2 -> it2 == it } }
            val counts = colorCounts.values.sortedDescending()
            return counts[0] * counts[1] * counts[2]
        }
        if (part.isTwo()) {
            val gridC = Grid(gridA.width, gridB.height, '.')
            var best = Long.MIN_VALUE
            for (p in generatePermutations(originalColors).map { it.replace(",", "") }) {
                for (y in gridA.yRange) {
                    for (x in gridA.xRange) {
                        gridC[x, y] = replaceColor(gridA[x, y], p[gridB[x, y].digitToInt() - 1])
                    }
                }
                val colorCounts = mutableMapOf<Char, Long>()
                allColors.forEach { colorCounts[it] = gridC.count { it2 -> it2 == it }.toLong() } 
                val values = colorCounts.values.sortedDescending()
                best = max(best, values[0] * values[1] * values[2])
            }
            return best
        }
        // Part 3
        val colorCounts = mutableMapOf<Char, Long>()
        originalColors.forEach { colorCounts[it] = gridA.count { it2 -> it2 == it }.toLong() }
        val sortedCounts = colorCounts.toList().sortedByDescending { (_, value) -> value }
        val biggestThree = (sortedCounts[0].second + sortedCounts[1].second + sortedCounts[2].second)
        val bestFirstFactor = biggestThree / 3
        val bestSecondFactor = (biggestThree - bestFirstFactor) / 2
        return (bestFirstFactor * bestSecondFactor * (biggestThree - bestFirstFactor - bestSecondFactor))
    }

    fun replaceColor(colorA: Char, colorB: Char): Char {
        val ab = listOf(colorA, colorB).sorted().joinToString("")
        return if ("K" in ab) {
            'K'
        } else if (ab == "CM") {
            'B'
        } else if (ab == "CY") {
            'G'
        } else if (ab == "MY") {
            'R'
        } else {
            ab[0]
        }
    }
}
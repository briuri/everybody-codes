package buri.ec.e2024.q08

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(21, true)
        assertRun(9879560, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(27, true)
        assertRun(146862375, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(2, true)
        assertRun(37396, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        if (part.isOne()) {
            val blocks = input.first().toLong()
            val heightD = sqrt(blocks.toDouble())
            val heightI = if (floor(heightD) < heightD) {
                heightD.toLong() + 1
            } else {
                heightD.toLong()
            }
            val missingBlocks = (heightI * heightI) - blocks
            val bottomWidth = 2 * heightI - 1
            return bottomWidth * missingBlocks
        } else {
            val tokens = input.first().split(",")
            val numPriests = tokens[0].toInt()
            val numAcolytes = tokens[1].toInt()
            val blocks = tokens[2].toInt()

            val blocksPerLayer = mutableMapOf<Int, Long>()
            blocksPerLayer[1] = 1
            val columnHeights = mutableListOf<Long>()
            columnHeights.add(1)

            var layer = 1
            var currentThickness = 1L
            var bottomWidth = 1

            while (true) {
                if (part.isTwo()) {
                    val totalBlocksUsed = blocksPerLayer.values.sum()
                    if (totalBlocksUsed > blocks) {
                        return bottomWidth * (totalBlocksUsed - blocks)
                    }
                } else {
                    var totalBlocksUsed = 0L
                    for (i in columnHeights.indices) {
                        if (i == 0 || i == columnHeights.lastIndex) {
                            totalBlocksUsed += columnHeights[i]
                        } else {
                            val emptySpace = (numPriests * columnHeights[i] * bottomWidth) % numAcolytes
                            totalBlocksUsed += columnHeights[i] - emptySpace
                        }
                    }
                    if (totalBlocksUsed > blocks) {
                        return (totalBlocksUsed - blocks)
                    }
                }
                layer++
                currentThickness = (currentThickness * numPriests) % numAcolytes
                if (part.isThree()) {
                    currentThickness += numAcolytes
                }
                bottomWidth = 2 * layer - 1
                blocksPerLayer[layer] = currentThickness * bottomWidth
                for (i in columnHeights.indices) {
                    columnHeights[i] = columnHeights[i] + currentThickness
                }
                columnHeights.add(0, currentThickness)
                columnHeights.add(currentThickness)
            }
        }
    }
}
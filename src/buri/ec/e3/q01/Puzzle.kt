package buri.ec.e3.q01

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import org.junit.Test
import kotlin.text.replace

/**
 * Entry point for a daily puzzle
 *
 * @author Brian Uri!
 */
class Puzzle : BasePuzzle() {

    @Test
    fun runPart1() {
        assertRun(9166, 1)
        assertRun(55359, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(2456, 1)
        assertRun(74635, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(292320, 1)
        assertRun(10862430, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val scales = mutableMapOf<Int, Scale>()
        for (line in input) {
            val id = line.split(":")[0].toInt()
            val colors = line.split(":")[1].split(" ")
            val shine = if (colors.size > 3) {
                colors[3]
            } else {
                "ssssss"
            }
            scales[id] = Scale(id, colors[0], colors[1], colors[2], shine)
        }
        if (part.isOne()) {
            return scales.filter { it.value.getDominantColor() == Color.GREEN }.keys.sum()
        } else if (part.isTwo()) {
            val maxShine = scales.maxOf { it.value.shine }
            val maxScales = scales.filter { it.value.shine == maxShine }
            val darkestColor = maxScales.minOf { it.value.colorSum }
            val darkestScales = maxScales.filter { it.value.colorSum == darkestColor }
            return darkestScales.keys.first()
        } else {
            val groupCounts = mutableMapOf<Group, Int>()
            for (group in Group.entries) {
                groupCounts[group] = scales.count { it.value.getGroup() == group }
            }
            val biggestGroup = groupCounts.maxBy { it.value }
            val groupedScales = scales.filter { it.value.getGroup() == biggestGroup.key }
            return groupedScales.keys.sum()
        }
    }
}

data class Scale(val id: Int, val rawRed: String, val rawGreen: String, val rawBlue: String, val rawShine: String) {
    val red = rawRed.replace('r', '0').replace('R', '1').toInt(2)
    val green = rawGreen.replace('g', '0').replace('G', '1').toInt(2)
    val blue = rawBlue.replace('b', '0').replace('B', '1').toInt(2)
    val shine = rawShine.replace('s', '0').replace('S', '1').toInt(2)
    val colorSum = red + green + blue

    fun getDominantColor(): Color {
        if (red > green && red > blue) {
            return Color.RED
        }
        if (green > red && green > blue) {
            return Color.GREEN
        }
        if (blue > red && blue > green) {
            return Color.BLUE
        }
        return Color.NONE
    }

    fun getShineLevel(): ShineLevel {
        if (shine <= 30) {
            return ShineLevel.MATTE
        } else if (shine >= 33) {
            return ShineLevel.SHINY
        }
        return ShineLevel.UNKNOWN
    }

    fun getGroup(): Group {
        if (getDominantColor() == Color.RED) {
            return when (getShineLevel()) {
                ShineLevel.MATTE -> Group.RED_MATTE
                ShineLevel.SHINY -> Group.RED_SHINY
                else -> Group.NONE
            }
        }
        if (getDominantColor() == Color.GREEN) {
            return when (getShineLevel()) {
                ShineLevel.MATTE -> Group.GREEN_MATTE
                ShineLevel.SHINY -> Group.GREEN_SHINY
                else -> Group.NONE
            }
        }
        if (getDominantColor() == Color.BLUE) {
            return when (getShineLevel()) {
                ShineLevel.MATTE -> Group.BLUE_MATTE
                ShineLevel.SHINY -> Group.BLUE_SHINY
                else -> Group.NONE
            }
        }
        return Group.NONE
    }
}

enum class Color {
    RED, GREEN, BLUE, NONE
}

enum class ShineLevel {
    MATTE, SHINY, UNKNOWN
}

enum class Group {
    RED_MATTE, RED_SHINY, GREEN_MATTE, GREEN_SHINY, BLUE_MATTE, BLUE_SHINY, NONE
}

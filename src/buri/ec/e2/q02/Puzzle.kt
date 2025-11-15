package buri.ec.e2.q02

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
        assertRun(7, 1)
        assertRun(128, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(2955, 1)
        assertRun(21570, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(21550686, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var bolts = 0
        val boltColors = "RGB"
        if (part.isOne()) {
            var balloons = input[0]
            while (balloons.isNotEmpty()) {
                val bolt = boltColors[bolts % boltColors.length]
                while (balloons.startsWith(bolt)) {
                    balloons = balloons.drop(1)
                }
                balloons = balloons.drop(1)
                bolts++
            }
        } else {
            val repeats = if (part.isTwo()) {
                100
            } else {
                100000
            }
            // Use two deques. The point between them is the balloon across the circle.
            val balloons = input[0].repeat(repeats)
            val balloonsBefore = ArrayDeque<Char>()
            val balloonsAfter = ArrayDeque<Char>()
            balloonsBefore.addAll(balloons.take(balloons.length / 2).toCharArray().toList())
            balloonsAfter.addAll(balloons.takeLast(balloons.length / 2).toCharArray().toList())

            while (balloonsBefore.size + balloonsAfter.size > 0) {
                val bolt = boltColors[bolts % boltColors.length]
                val isEven = ((balloonsBefore.size + balloonsAfter.size) % 2 == 0)
                val matchesFirstBalloon = (bolt == balloonsBefore.first())
                if (matchesFirstBalloon && isEven) {
                    balloonsAfter.removeFirst()
                }
                balloonsBefore.removeFirst()
                if (balloonsAfter.size > balloonsBefore.size) {
                    balloonsBefore.add(balloonsAfter.removeFirst())
                }
                bolts++
            }
        }
        return bolts
    }
}
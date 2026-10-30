package buri.ec.e2026.q04

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
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
        assertRun(27598, 1)
        assertRun(195401, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(6470, 1)
        assertRun(624964479, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(2998, 1)
        assertRun(3004, 2)
        assertRun(6232480, 3)
        assertRun(6885774, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val players = mutableMapOf<Int, Player>()
        for (line in input) {
            val tokens = line.split(" ")
            val id = tokens[0].split("=")[1].toInt()
            val quest = tokens[1].split("=")[1].toInt()
            val part = tokens[2].split("=")[1].toInt()
            val time = tokens[3].split("=")[1].toInt()
            if (players[id] == null) {
                players[id] = Player(id)
            }
            players[id]!!.times.add(Time(id, quest, part, time))
        }

        if (part.isOne()) {
            val sortedPlayers = players.values.sortedBy { it.getTotalTime() }
            return sortedPlayers.mapIndexed { index, player -> (index + 1) * player.getTotalTime() }.sum()
        }
        if (part.isTwo()) {
            val sortedPlayers = players.values.sortedWith(compareBy({ it.times.size * -1 }, { it.getTotalTime() }))
            return sortedPlayers.mapIndexed { index, player -> (index + 1) * player.getTotalTime() }.sum()
        }

        // Part 3
        val scores = mutableMapOf<Int, Int>()
        players.keys.forEach { scores[it] = 0 }
        for (quest in 1..20) {
            for (part in 1..3) {
                val sortedPlayers =
                    players.toSortedMap(compareBy({ players[it]!!.getTimeOrNull(quest, part) }, { it }))

                val usedPoints = mutableListOf<Int>()
                usedPoints.add(part * 50 + 1)
                var previousTime = -1
                for ((id, player) in sortedPlayers) {
                    val playerTime = player.getTimeOrNull(quest, part) ?: continue
                    val placesToSkip = if (playerTime == previousTime) {
                        0
                    } else {
                        usedPoints.count { it == usedPoints.last() }
                    }
                    usedPoints.add(usedPoints.last() - placesToSkip)
                    scores[id] = scores[id]!! + max(0, usedPoints.last())
                    previousTime = playerTime
                }
            }
        }

        var checksum = 0L
        val usedPlaces = mutableListOf<Int>()
        usedPlaces.add(0)
        var previousScore = -1
        for (playerScore in scores.values.sortedByDescending { it }) {
            val placesToSkip = if (playerScore == previousScore) {
                0
            } else {
                usedPlaces.count { it == usedPlaces.last() }
            }
            usedPlaces.add(usedPlaces.last() + placesToSkip)
            checksum += (usedPlaces.last()) * playerScore
            previousScore = playerScore
        }
        return checksum
    }
}

data class Player(val id: Int) {
    val times = mutableListOf<Time>()

    fun getTimeOrNull(quest: Int, part: Int): Int? {
        val thisPart = times.firstOrNull { it.quest == quest && it.part == part }?.time
        if (part == 3 && thisPart != null) {
            return getTimeOrNull(quest, 2)!! + thisPart
        }
        if (part == 2 && thisPart != null) {
            return getTimeOrNull(quest, 1)!! + thisPart
        }
        return thisPart
    }

    fun getTotalTime(): Int = times.sumOf { it.time }
}

data class Time(val id: Int, val quest: Int, val part: Int, val time: Int)

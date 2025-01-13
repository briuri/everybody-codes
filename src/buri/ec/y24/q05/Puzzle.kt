package buri.ec.y24.q05

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
        assertRun(2323, true)
        assertRun(4223, false, true)
    }

    @Test
    fun runPart2() {
        assertRun(50877075, true)
        assertRun(20580403334550, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(6584, true)
        assertRun(9956100610001003, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val game = mutableListOf<Column>()
        repeat(input[0].split(" ").size) {
            game.add(Column())
        }
        for (row in input) {
            val people = row.split(" ")
            for (i in people.indices) {
                game[i].addPerson(people[i].toInt())
            }
        }

        var round = 0
        val shouts = mutableMapOf<Long, Int>()
        val states = mutableSetOf<String>()

        while (true) {
            val clapperColumn = round % game.size
            val nextColumn = (round + 1) % game.size
            val clapper = game[clapperColumn].removeClapper()
            game[nextColumn].clap(clapper)
            val value = game.map { it.first() }.joinToString("").toLong()
            round++
            val state = "$clapperColumn-${game}"
            if (part.isThree()) {
                if (state in states) {
                    return shouts.keys.max()
                } else {
                    states.add(state)
                }
            }
            if (part.isOne() && round == 10) {
                return value
            } else {
                if (value !in shouts) {
                    shouts[value] = 0
                }
                shouts[value] = shouts[value]!! + 1
                if (part.isTwo() && shouts[value] == 2024) {
                    return round.toLong() * value
                }
            }
        }
    }
}

class Column {
    private val people = mutableListOf<Int>()

    fun clap(num: Int) {
        var index = (num - 1) % (people.size * 2)
        if (index >= people.size) {
            index = (people.size * 2) - index
        }
        people.add(index, num)
    }

    fun addPerson(num: Int) {
        people.add(num)
    }

    fun removeClapper(): Int {
        return people.removeAt(0)
    }

    fun first(): Int {
        return people[0]
    }

    override fun toString(): String {
        return people.toString()
    }
}
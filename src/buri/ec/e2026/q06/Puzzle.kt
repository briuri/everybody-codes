package buri.ec.e2026.q06

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
        assertRun(42, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(6, 1)
        assertRun(809, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(3, 1)
        assertRun(1642, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        val recipes = mutableMapOf<String, List<String>>()
        for (line in input) {
            val tokens = line.split(":")
            recipes[tokens[0]] = tokens[1].split(",")
        }

        if (part.isOne()) {
            return recipes.getIngredientsFor("NULL").size
        }
        if (part.isTwo()) {
            return recipes.getThingsMadeOf("NULL").size
        }
        // Part 3
        val v1 = recipes.getThingsMadeOf("NULLv1")
        val v2 = recipes.getThingsMadeOf("NULLv2")
        val v3 = recipes.getThingsMadeOf("NULLv3")

        val intersect12 = v1.intersect(v2)
        val intersect23 = v2.intersect(v3)
        val intersect31 = v3.intersect(v1)
        return (intersect12 + intersect23 + intersect31).toSet().size
    }

    fun Map<String, List<String>>.getIngredientsFor(food: String): Set<String> {
        val set = mutableSetOf<String>()
        set.add(food)
        var previousSize = set.size
        while (true) {
            for (ingredient in set.toSet()) {
                val children = this[ingredient]
                if (children != null) {
                    set.addAll(children)
                }
            }
            if (set.size == previousSize) {
                break
            }
            previousSize = set.size
        }
        set.remove(food)
        return set
    }

    fun Map<String, List<String>>.getThingsMadeOf(ingredient: String): Set<String> {
        val set = mutableSetOf<String>()
        set.add(ingredient)
        var previousSize = set.size
        while (true) {
            for (ingredient in set.toSet()) {
                set.addAll(filter { it.value.contains(ingredient) }.map { it.key })
            }
            if (set.size == previousSize) {
                break
            }
            previousSize = set.size
        }
        set.remove(ingredient)
        return set
    }
}
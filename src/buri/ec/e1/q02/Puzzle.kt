package buri.ec.e1.q02

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
        assertRun("CFGNLK", true)
        assertRun("QUACK!NBZTMGVX", false, true)
    }

    @Test
    fun runPart2() {
        assertRun(0, true)
        assertRun(0, false, true)
    }

    @Test
    fun runPart3() {
        assertRun(0, true)
        assertRun(0, false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val left = Tree()
        val right = Tree()
        // ADD id=1 left=[10,A] right=[30,H]
        for (line in input) {
            val tokens = line.split(" ")
            val rawLeft = tokens[2].split("=")[1].drop(1).dropLast(1).split(",")
            val rawRight = tokens[3].split("=")[1].drop(1).dropLast(1).split(",")
            left.add(Node(rawLeft[0].toInt(), rawLeft[1]))
            right.add(Node(rawRight[0].toInt(), rawRight[1]))
        }

        val widestLeft = left.getNodesAtWidestDepth()
        val widestRight = right.getNodesAtWidestDepth()
        return widestLeft.joinToString("") { it.symbol } + widestRight.joinToString("") { it.symbol }
    }
}

class Tree() {
    var root: Node? = null
    val allNodes = mutableSetOf<Node>()

    fun add(node: Node) {
        if (root == null) {
            root = node
        } else {
            root!!.add(node)
        }
        allNodes.add(node)
    }

    fun getNodesAtWidestDepth(): List<Node> {
        val depthMap = mutableMapOf<Int, Int>()
        for (node in allNodes) {
            depthMap.putIfAbsent(node.depth, 0)
            depthMap[node.depth] = depthMap[node.depth]!! + 1
        }
        val mostAtDepth = depthMap.values.max()
        val widestDepth = depthMap.filterValues { it == mostAtDepth }.keys.first()
        return allNodes.filter { it.depth == widestDepth }.sortedBy { it.rank }
    }
}

data class Node(val rank: Int, val symbol: String) {
    var left: Node? = null
    var right: Node? = null
    var depth = 0

    fun add(node: Node) {
        if (node.rank < this.rank) {
            if (left == null) {
                node.depth = depth + 1
                left = node
            } else {
                node.depth++
                left!!.add(node)
            }
        } else {
            if (right == null) {
                node.depth = depth + 1
                right = node
            } else {
                node.depth++
                right!!.add(node)
            }
        }
    }
}
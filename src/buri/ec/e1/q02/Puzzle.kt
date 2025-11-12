package buri.ec.e1.q02

import buri.ec.common.BasePuzzle
import buri.ec.common.Part
import buri.ec.common.extractInts
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
        assertRun("MGFLNK", true)
        assertRun("QUACK!MSXJLBRGMMHRGZ", false, true)
    }

    @Test
    fun runPart3() {
        assertRun("DJMGL", true)
        assertRun("", false, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val leftTree = Tree()
        val rightTree = Tree()

        // ADD id=1 left=[10,A] right=[30,H]
        for (line in input) {
            if (line.startsWith("SWAP")) {
                val id = line.extractInts()[0]
                swap(part, leftTree, rightTree, id)
            } else {
                val tokens = line.split(" ")
                val id = tokens[1].split("=")[1].toInt()
                val rawLeft = tokens[2].split("=")[1].drop(1).dropLast(1).split(",")
                val rawRight = tokens[3].split("=")[1].drop(1).dropLast(1).split(",")
                val leftNode = Node(id, rawLeft[0].toInt(), rawLeft[1])
                val rightNode = Node(id, rawRight[0].toInt(), rawRight[1])
                leftTree.add(leftNode)
                rightTree.add(rightNode)
            }
        }
        return leftTree.getLettersAtWidestDepth() + rightTree.getLettersAtWidestDepth()
    }

    fun swap(part: Part, leftTree: Tree, rightTree: Tree, id: Int) {
        val leftNode = leftTree.getNode(id)
        val rightNode = rightTree.getNode(id)
        val leftNodeCopy = leftNode.copy()
        leftTree.replaceNode(part, leftNode, rightNode)
        rightTree.replaceNode(part,rightNode, leftNodeCopy)
    }
}

class Tree() {
    var root: Node? = null
    val nodes = mutableMapOf<Int, Node>()

    fun add(node: Node) {
        if (root == null) {
            root = node
        } else {
            root!!.add(node)
        }
        nodes[node.id] = node
    }

    fun getNode(id: Int): Node {
        return nodes[id]!!
    }

    fun replaceNode(part: Part, oldNode: Node, newNode: Node) {
        if (part.isTwo()) {
            oldNode.rank = newNode.rank
            oldNode.symbol = newNode.symbol
        }
        else {

        }
    }

    fun getLettersAtWidestDepth(): String {
        val depthMap = mutableMapOf<Int, Int>()
        for (node in nodes.values) {
            depthMap.putIfAbsent(node.depth, 0)
            depthMap[node.depth] = depthMap[node.depth]!! + 1
        }
        val mostAtDepth = depthMap.values.max()
        val widestDepth = depthMap.filterValues { it == mostAtDepth }.keys.first()

        val builder = StringBuilder()
        root!!.getLettersAtDepth(builder, widestDepth)
        return builder.toString()
    }
}

data class Node(val id: Int, var rank: Int, var symbol: String) {
    var parent: Node? = null
    var left: Node? = null
    var right: Node? = null
    var depth = 0

    override fun toString(): String {
        return ("$id[$rank $symbol p=${parent?.symbol}, l=${left?.symbol}, r=${right?.symbol}]")
    }

    fun add(node: Node) {
        if (node.rank < this.rank) {
            if (left == null) {
                node.parent = this
                node.depth = depth + 1
                left = node
            } else {
                left!!.add(node)
            }
        } else {
            if (right == null) {
                node.parent = this
                node.depth = depth + 1
                right = node
            } else {
                right!!.add(node)
            }
        }
    }

    fun getLettersAtDepth(builder: StringBuilder, depth: Int) {
        if (this.depth == depth) {
            builder.append(this.symbol)
        }
        else {
            left?.getLettersAtDepth(builder, depth)
            right?.getLettersAtDepth(builder, depth)
        }
    }
}
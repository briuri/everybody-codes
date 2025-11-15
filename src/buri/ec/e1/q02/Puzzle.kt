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
        assertRun("CFGNLK", 1)
        assertRun("QUACK!NBZTMGVX", 0, true)
    }

    @Test
    fun runPart2() {
        assertRun("MGFLNK", 1)
        assertRun("QUACK!MSXJLBRGMMHRGZ", 0, true)
    }

    @Test
    fun runPart3() {
        assertRun("DJCGL", 1)
        assertRun("QUACK!YBNMTWPVZLLTHPTZLTWSHXSXBLSG", 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): String {
        val trees = Trees()
        for (line in input) {
            // SWAP 1
            if (line.startsWith("SWAP")) {
                val id = line.extractInts()[0]
                trees.swap(part, id)
            }
            // ADD id=1 left=[10,A] right=[30,H]
            else {
                val tokens = line.split(" ")
                val id = tokens[1].split("=")[1].toInt()
                val rawLeft = tokens[2].split("=")[1].drop(1).dropLast(1).split(",")
                val rawRight = tokens[3].split("=")[1].drop(1).dropLast(1).split(",")
                val leftNode = Node(id, rawLeft[0].toInt(), rawLeft[1])
                val rightNode = Node(id, rawRight[0].toInt(), rawRight[1])
                trees.add(leftNode, rightNode)
            }
        }
        return trees.getLettersAtWidestDepth()
    }
}

class Trees() {
    var leftTree: Node? = null
    var rightTree: Node? = null

    fun getLettersAtWidestDepth(): String {
        return leftTree!!.getLettersAtWidestDepth() + rightTree!!.getLettersAtWidestDepth()
    }

    private fun getNodes(id: Int): MutableList<Node> {
        val nodes = mutableListOf<Node>()
        nodes.addAll(leftTree!!.getAllNodes().filter { it.id == id })
        nodes.addAll(rightTree!!.getAllNodes().filter { it.id == id })
        return nodes
    }

    fun add(leftNode: Node, rightNode: Node) {
        // Root nodes
        if (leftTree == null) {
            leftTree = leftNode
            rightTree = rightNode
        }
        // All future nodes
        else {
            leftTree!!.add(leftNode)
            rightTree!!.add(rightNode)
        }
    }

    fun swap(part: Part, id: Int) {
        // In part 3, first and second node may both be in left or right tree.
        val nodes = getNodes(id)
        val firstNode = nodes[0]
        val secondNode = nodes[1]
        val firstNodeCopy = firstNode.copy()
        // Part Two: Just swap values.
        if (part.isTwo()) {
            firstNode.rank = secondNode.rank
            firstNode.symbol = secondNode.symbol
            secondNode.rank = firstNodeCopy.rank
            secondNode.symbol = firstNodeCopy.symbol
        }
        // Part Three: Swap entire subtrees.
        else {
            val oldLeftParent = firstNode.parent
            val oldRightParent = secondNode.parent

            // Move first into second's place.
            firstNode.swapParent(oldRightParent)
            if (oldRightParent != null) {
                if (oldRightParent.left == secondNode) {
                    oldRightParent.left = firstNode
                } else {
                    oldRightParent.right = firstNode
                }
            } else {
                rightTree = firstNode
            }

            // Move second into first's place.
            secondNode.swapParent(oldLeftParent)
            if (oldLeftParent != null) {
                if (oldLeftParent.left == firstNode) {
                    oldLeftParent.left = secondNode
                } else {
                    oldLeftParent.right = secondNode
                }
            } else {
                leftTree = secondNode
            }
        }
    }
}

data class Node(val id: Int, var rank: Int, var symbol: String) {
    var parent: Node? = null
    var left: Node? = null
    var right: Node? = null
    var depth = 0

    fun getAllNodes(): MutableList<Node> {
        val nodes = mutableListOf<Node>()
        addSubTreeTo(nodes)
        return nodes
    }

    fun getLettersAtWidestDepth(): String {
        val depthMap = mutableMapOf<Int, Int>()
        for (node in getAllNodes()) {
            depthMap.putIfAbsent(node.depth, 0)
            depthMap[node.depth] = depthMap[node.depth]!! + 1
        }
        val mostAtDepth = depthMap.values.max()
        val widestDepth = depthMap.filterValues { it == mostAtDepth }.keys.first()

        val builder = StringBuilder()
        getLettersAtDepth(builder, widestDepth)
        return builder.toString()
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

    private fun getLettersAtDepth(builder: StringBuilder, depth: Int) {
        if (this.depth == depth) {
            builder.append(this.symbol)
        } else {
            left?.getLettersAtDepth(builder, depth)
            right?.getLettersAtDepth(builder, depth)
        }
    }

    fun swapParent(newParent: Node?) {
        if (newParent != null) {
            this.parent = newParent
            updateSubTreeDepth(newParent.depth + 1)
        }
    }

    private fun addSubTreeTo(nodes: MutableList<Node>) {
        nodes.add(this)
        left?.addSubTreeTo(nodes)
        right?.addSubTreeTo(nodes)
    }

    private fun updateSubTreeDepth(newDepth: Int) {
        this.depth = newDepth
        left?.updateSubTreeDepth(newDepth + 1)
        right?.updateSubTreeDepth(newDepth + 1)
    }
}
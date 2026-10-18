package buri.ec.e3.q03

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
        assertRun(43, 1)
        assertRun(5781, 0, true)
    }

    @Test
    fun runPart2() {
        assertRun(50, 1)
        assertRun(319735, 0, true)
    }

    @Test
    fun runPart3() {
        assertRun(38, 1)
        assertRun(60, 2)
        assertRun(421164, 0, true)
    }

    /**
     * Executes a part of the puzzle using the specified input.
     */
    override fun run(part: Part, input: List<String>): Number {
        var root: Node? = null
        for (line in input) {
            val tokens = line.split(", ")
            val id = tokens[0].split("=")[1].toInt()
            val plug = tokens[1].split("=")[1]
            val leftSocket = tokens[2].split("=")[1]
            val rightSocket = tokens[3].split("=")[1]
            val data = tokens[4].split("=")[1]
            val node = Node(id, plug, leftSocket, rightSocket, data)
            if (root == null) {
                root = node
            } else {
                var nodeToConnect = root.connect(part, node)
                // Handle nodes that have been pushed off the right side without finding a match.
                while (nodeToConnect != null) {
                    nodeToConnect = root.connect(part, nodeToConnect)
                }
            }
        }

        val readNodes = mutableListOf<Int>()
        root!!.read(readNodes)
        return readNodes.mapIndexed { index, value -> value * (index + 1) }.sum()
    }
}

data class Node(val id: Int, val plug: String, val leftSocket: String, val rightSocket: String, val data: String) {
    var parent: Node? = null
    var leftNode: Node? = null
    var rightNode: Node? = null

    fun read(listSoFar: MutableList<Int>) {
        if (leftNode != null) {
            leftNode!!.read(listSoFar)
        }
        listSoFar.add(id)
        if (rightNode != null) {
            rightNode!!.read(listSoFar)
        }
    }

    fun connect(part: Part, node: Node?): Node? {
        var nodeToConnect: Node? = node
        if (!part.isThree()) {
            if (nodeToConnect != null) {
                if (leftNode != null) {
                    nodeToConnect = leftNode!!.connect(part, nodeToConnect)
                } else if (isStrong(nodeToConnect.plug, leftSocket)
                    || (part.isTwo() && isWeak(nodeToConnect.plug, leftSocket))
                ) {
                    nodeToConnect.parent = this
                    leftNode = nodeToConnect
                    nodeToConnect = null
                }
            }
            if (nodeToConnect != null) {
                if (rightNode != null) {
                    nodeToConnect = rightNode!!.connect(part, nodeToConnect)
                } else if (isStrong(nodeToConnect.plug, rightSocket)
                    || (part.isTwo() && isWeak(nodeToConnect.plug, rightSocket))
                ) {
                    nodeToConnect.parent = this
                    rightNode = nodeToConnect
                    nodeToConnect = null
                }
            }
        } else {
            if (nodeToConnect != null) {
                val strongCondition = isStrong(nodeToConnect.plug, leftSocket)
                        && (leftNode == null || (leftNode != null && isWeak(leftNode!!.plug, leftSocket)))
                // Weak can fill empty sockets.
                val weakCondition = isWeak(nodeToConnect.plug, leftSocket) && leftNode == null
                if (strongCondition || weakCondition) {
                    val oldLeft = leftNode
                    oldLeft?.parent = null
                    nodeToConnect.parent = this
                    leftNode = nodeToConnect
                    nodeToConnect = oldLeft
                } else if (leftNode != null) {
                    nodeToConnect = leftNode!!.connect(part, nodeToConnect)
                }
            }

            if (nodeToConnect != null) {
                // Strong can replace empty or weak (but not other strong).
                val strongCondition = isStrong(nodeToConnect.plug, rightSocket)
                        && (rightNode == null || (rightNode != null && isWeak(rightNode!!.plug, rightSocket)))
                // Weak can fill empty sockets.
                val weakCondition = isWeak(nodeToConnect.plug, rightSocket) && rightNode == null
                if (strongCondition || weakCondition) {
                    val oldRight = rightNode
                    oldRight?.parent = null
                    nodeToConnect.parent = this
                    rightNode = nodeToConnect
                    nodeToConnect = oldRight
                } else if (rightNode != null) {
                    nodeToConnect = rightNode!!.connect(part, nodeToConnect)
                }
            }
        }
        return nodeToConnect
    }

    fun isStrong(plug: String, socket: String): Boolean {
        return (plug == socket)
    }

    fun isWeak(plug: String, socket: String): Boolean {
        val plugTokens = plug.split(" ")
        val socketTokens = socket.split(" ")
        return (plug != socket && (plugTokens[0] == socketTokens[0] || plugTokens[1] == socketTokens[1]))
    }
}

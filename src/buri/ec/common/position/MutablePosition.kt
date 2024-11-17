package buri.ec.common.position

import buri.ec.common.position.Direction.*

/**
 * Helper class for a 2D point and directional facing.
 *
 * @author Brian Uri!
 */
data class MutablePosition(var coords: Point2D<Int>, var facing: Direction = NORTH) {

    constructor(x: Int, y: Int, facing: Direction) : this(Point2D(x, y), facing)

    /**
     * Adjusts facing and moves in that direction.
     */
    fun move(direction: Direction) {
        facing = direction
        move()
    }

    /**
     * Move one square in the current direction.
     */
    fun move() {
        coords = when (facing) {
            NORTH -> coords.copy(y = coords.y - 1)
            EAST -> coords.copy(x = coords.x + 1)
            SOUTH -> coords.copy(y = coords.y + 1)
            WEST -> coords.copy(x = coords.x - 1)
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnLeft() {
        facing = when (facing) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnRight() {
        facing = when (facing) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
    }

    /**
     * Change the facing on this position
     */
    fun turnAround() {
        facing = when (facing) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

enum class Direction(val icon: Char) {
    NORTH('^'), EAST('>'), SOUTH('v'), WEST('<');

    /**
     * Returns a direction matching the given icon.
     */
    companion object {
        infix fun from(value: Char): Direction {
            return entries.first { it.icon == value }
        }
    }
}
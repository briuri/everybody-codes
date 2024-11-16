package buri.ec.y24.q01

import org.junit.Test

class Test {

    @Test
    fun runPart2() {
        var sum = 0
        for (mobs in "ABC".chunked(2)) {
            val extra = if (mobs.contains('x')) 0 else 2
            var localSum = 0
            for (mob in mobs.toCharArray()) {
                localSum += when (mob) {
                    'B' -> 1
                    'C' -> 3
                    'D' -> 5
                    else -> 0
                }
            }
            localSum += extra
            println("$mobs=$localSum")
            sum += localSum
        }
        println(sum)
    }

    @Test
    fun runPart3() {
        var sum = 0
        for (mobs in "ABC".chunked(3).map { it ->
            it.filterNot { it2 -> it2 == 'x' }}) {
            var localSum = 0
            for (mob in mobs.toCharArray()) {
                localSum += when (mob) {
                    'A' -> 0
                    'B' -> 1
                    'C' -> 3
                    'D' -> 5
                    else -> 0
                }
            }
            localSum += when (mobs.length) {
                2 -> 2
                3 -> 6
                else -> 0
            }
            println("$mobs=$localSum")
            sum += localSum
        }
        println(sum)
    }
}
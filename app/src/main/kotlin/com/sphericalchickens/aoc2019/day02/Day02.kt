package com.sphericalchickens.aoc2019.day02

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.util.*

/*
class Day2 {
    companion object {
        val sampleInput = """
            1,9,10,3,2,3,11,0,99,30,40,50
        """.trimIndent().trim().split(",").map(String::toLong)

        val sample2 = """
            1,1,1,4,99,5,6,0,99
        """.trimIndent()

        val sample3 = """
            2,3,0,3,99
        """.trimIndent()

        val sample4 = "1,1,1,4,99,5,6,0,99".split(",").map(String::toLong)

        fun test() {
            Day2().run {
                // val input = File("/Users/dkhawk/Downloads/2019/input-2.txt").readText().trim()
                part1(sample4)
            }
        }
    }

    private fun part1(input: List<Long>) {
        val computer = Computer(input, ArrayDeque<Long>(), ArrayDeque<Long>())
        computer.execute()
        computer.dumpMemory(-1)

//        repeat(1000000) {
//            data.add(99)
//        }
//        println(data.joinToString())
        // replace position 1 with the value 12 and replace position 2

//        val expected = 19690720
//        var dd = 0
//
//        while (true) {
//            val data = ArrayList<Int>(input.split(",").map { it.toInt() })
//            data[1] = dd % 100
//            data[2] = dd / 100
//
//            data.windowed(4, 4).forEach { op ->
//                val p1 = op[1]
//                val p2 = op[2]
//                val p3 = op[3]
//                when (op[0]) {
//                    1 -> data[p3] = data[p1] + data[p2]
//                    2 -> data[p3] = data[p1] * data[p2]
//                    99 -> return@forEach
//                }
//            }
//
//            if (data[0] == expected)
//                break
//
//            dd++
//            println(dd)
//        }
//
//        println(dd % 100)
//        println(dd / 100)
//
////        println(data.joinToString())
//
//        // opcode 1 = add pos1 pos2 pos3
//        // opcode 2 = mult pos1 pos2 pos3
//        // opcode 99 = halt
//        // 1,10,20,30
//        // pos[30] = pos[10] + pos[20]
    }
}
*/

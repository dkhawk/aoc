package com.sphericalchickens.aoc2019.day05

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.util.*

class Day5 {
    companion object {
        val simpleAdd = "11102,9,10,0,99"

        val sampleInput = """
            1,9,10,3,2,3,11,0,99,30,40,50
        """.trimIndent()

        val sample2 = """
            1,1,1,4,99,5,6,0,99
        """.trimIndent()

        val sample3 = """
            2,3,0,3,99
        """.trimIndent()

        val sample4 = "3,0,4,0,99"

        val sample5 = "1002,4,3,4,33"

        val fullInput = """
            3,225,1,225,6,6,1100,1,238,225,104,0,1002,114,46,224,1001,224,-736,224,4,224,1002,223,8,223,1001,224,3,224,1,223,224,223,1,166,195,224,1001,224,-137,224,4,224,102,8,223,223,101,5,224,224,1,223,224,223,1001,169,83,224,1001,224,-90,224,4,224,102,8,223,223,1001,224,2,224,1,224,223,223,101,44,117,224,101,-131,224,224,4,224,1002,223,8,223,101,5,224,224,1,224,223,223,1101,80,17,225,1101,56,51,225,1101,78,89,225,1102,48,16,225,1101,87,78,225,1102,34,33,224,101,-1122,224,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1101,66,53,224,101,-119,224,224,4,224,102,8,223,223,1001,224,5,224,1,223,224,223,1102,51,49,225,1101,7,15,225,2,110,106,224,1001,224,-4539,224,4,224,102,8,223,223,101,3,224,224,1,223,224,223,1102,88,78,225,102,78,101,224,101,-6240,224,224,4,224,1002,223,8,223,101,5,224,224,1,224,223,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,1107,226,677,224,102,2,223,223,1006,224,329,101,1,223,223,1108,226,677,224,1002,223,2,223,1005,224,344,101,1,223,223,8,226,677,224,102,2,223,223,1006,224,359,1001,223,1,223,1007,226,677,224,1002,223,2,223,1005,224,374,101,1,223,223,1008,677,677,224,1002,223,2,223,1005,224,389,1001,223,1,223,1108,677,226,224,1002,223,2,223,1006,224,404,1001,223,1,223,1007,226,226,224,1002,223,2,223,1005,224,419,1001,223,1,223,1107,677,226,224,1002,223,2,223,1006,224,434,101,1,223,223,108,677,677,224,1002,223,2,223,1005,224,449,1001,223,1,223,1107,677,677,224,102,2,223,223,1005,224,464,1001,223,1,223,108,226,226,224,1002,223,2,223,1006,224,479,1001,223,1,223,1008,226,226,224,102,2,223,223,1005,224,494,101,1,223,223,108,677,226,224,102,2,223,223,1005,224,509,1001,223,1,223,8,677,226,224,1002,223,2,223,1006,224,524,101,1,223,223,7,226,677,224,1002,223,2,223,1006,224,539,101,1,223,223,7,677,226,224,102,2,223,223,1006,224,554,1001,223,1,223,7,226,226,224,1002,223,2,223,1006,224,569,101,1,223,223,107,677,677,224,102,2,223,223,1006,224,584,101,1,223,223,1108,677,677,224,102,2,223,223,1006,224,599,1001,223,1,223,1008,677,226,224,1002,223,2,223,1005,224,614,1001,223,1,223,8,677,677,224,1002,223,2,223,1006,224,629,1001,223,1,223,107,226,677,224,1002,223,2,223,1006,224,644,101,1,223,223,1007,677,677,224,102,2,223,223,1006,224,659,101,1,223,223,107,226,226,224,1002,223,2,223,1006,224,674,1001,223,1,223,4,223,99,226
        """.trimIndent()

        val p2Input1 = "3,9,8,9,10,9,4,9,99,-1,8".split(",").map(String::toLong)
        val p2Input2 = "3,9,7,9,10,9,4,9,99,-1,8".split(",").map(String::toLong)

        val p2Input3 = "3,3,1108,-1,8,3,4,3,99".split(",").map(String::toLong)

        val p2Input4 =  "3,3,1107,-1,8,3,4,3,99".split(",").map(String::toLong)

        val p2Input5 = "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9".split(",").map(String::toLong)

        val p2Input6 = "3,3,1105,-1,9,1101,0,0,12,4,12,99,1".split(",").map(String::toLong)

        val p2InputBig = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99".split(",").map(String::toLong)

        fun test() {
            val input = p2InputBig
            listOf(0,1,8,9).forEach {
                print("$it: -> ")
                Day5().part1(input, it.toLong())
            }
//            Day5().run {
//                val input = File("/Users/dkhawk/Downloads/2019/input-5.txt").readText().trim()
//                part1(fullInput, "5")
//            }
        }
    }
/*
    data class Command(val numParams: Int, val cmd: (computer: Computer, values: IntArray, parameters: IntArray) -> Unit) {
        companion object {
            fun getValues(computer: Computer, numParams: Int, parameterMode: Int): List<IntArray> {
                val pModes = IntArray(numParams)
                var divisor = 1
                val values = IntArray(pModes.size)
                val parameters = IntArray(pModes.size)

                for (i in 0 until numParams) {
                    val mode = (parameterMode / divisor) % 10
                    divisor *= 10
                    parameters[i] = computer.getParameter(i + 1)
                    values[i] = computer.getValue(parameters[i], mode)
                }

                return listOf(parameters, values)
            }
        }

        fun execute(computer: Computer, parameterMode: Int) {
            val valParam = getValues(computer, numParams, parameterMode)
            cmd(computer, valParam[1], valParam[0])
        }
    }

    private val commandMap = HashMap<Int, Command>()

    init {
        // add
        var opcode = 1
        commandMap[opcode++] = Command(3) { computer, values, parameters ->
            computer.storeValue(parameters[2], values[0] + values[1])
            computer.pc += parameters.size + 1
        }
        // mult (2)
        commandMap[opcode++] = Command(3) { computer, values, parameters ->
            computer.storeValue(parameters[2], values[0] * values[1])
            computer.pc += parameters.size + 1
        }
        // store input (3)
        commandMap[opcode++] = Command(1) { computer, values, parameters ->
            computer.storeValue(parameters[0], computer.readInput())
            computer.pc += parameters.size + 1
        }
        // write output (4)
        commandMap[opcode++] = Command(1) { computer, values, parameters ->
            println(values[0])
            computer.pc += parameters.size + 1
        }
        // jump-if-true (5)
        commandMap[opcode++] = Command(2) { computer, values, parameters ->
            if (values[0] != 0) {
                computer.pc = values[1]
            } else {
                computer.pc += parameters.size + 1
            }
        }
        // jump-if-false (6)
        commandMap[opcode++] = Command(2) { computer, values, parameters ->
            if (values[0] != 0) {
                computer.pc += parameters.size + 1
            } else {
                computer.pc = values[1]
            }
        }
        // less than (7)
        commandMap[opcode++] = Command(3) { computer, values, parameters ->
            computer.storeValue(parameters[2], if (values[0] < values[1]) 1 else 0)
            computer.pc += parameters.size + 1
        }
        // equals (8)
        commandMap[opcode++] = Command(3) { computer, values, parameters ->
            computer.storeValue(parameters[2], if (values[0] == values[1]) 1 else 0)
            computer.pc += parameters.size + 1
        }
    }

    class Computer(initialState: List<Int>, private val input: List<Int>, private val size: Int = 1000) {
        val data = IntArray(size)
        var pc = 0
        var inputCounter = 0

        init {
            data.fill(99, 0, data.lastIndex)
            initialState.forEachIndexed { index, value ->
                data[index] = value
            }
        }

        fun getValue(parameter: Int, mode: Int) : Int {
            return if (mode == 1) parameter else data[parameter]
        }

        fun storeValue(address: Int, value: Int) {
            data[address] = value
        }

        fun getParameter(offset: Int): Int {
            return data[pc + offset]
        }

        fun dumpMemory(limit: Int = -1) {
            if (limit < 0) {
                println(data.joinToString())
            } else {
                println(data.slice(0 until limit).joinToString())
            }
        }

        fun readInput(): Int {
            return input[inputCounter++]
        }
    }
*/
    private fun part1(program: List<Long>, input: Long) {
        val inputQueue = ArrayDeque<Long>()
        val outputQueue = ArrayDeque<Long>()

        inputQueue.offer(input)
        val computer = Computer(program, inputQueue, outputQueue)
        computer.execute()
        println(outputQueue.joinToString())
//    computer.dumpMemory(-1)

//        computer.dumpMemory()
    }
}

package com.sphericalchickens.aoc2019.day07

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Day7 {
    companion object {
        const val DEBUG = 3
        const val INFO = 2
        const val VERBOSE = 1
        const val debugLevel = VERBOSE

        val inputProgram =
            File("/Users/dkhawk/Downloads/2019/input-7.txt").readText().trim().split(",").map(String::toLong)

        val sampleProgram = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0".split(",").map(String::toInt)
        val sampleInputs = listOf(4, 3, 2, 1, 0)

        val sampleProgram2 =
            "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0".split(",").map(String::toInt)
        val sampleInputs2 = listOf(0, 1, 2, 3, 4)

        val sampleProgram3 =
            "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0".split(
                ","
            ).map(String::toInt)
        val sampleInputs3 = listOf(1, 0, 4, 3, 2)

        val program = sampleProgram3
        //            val inputs = sampleInputs3
        val inputs = listOf(0, 0, 0, 0, 5)

//        val output = File("/Users/dkhawk/Downloads/2019/input-7-out.txt").bufferedWriter()

        val testInput = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5".split(",").map(String::toLong)

        val testInputPart2B = "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10".split(",").map(String::toLong)

        fun test() {
//            Day7().part1execute(inputProgram, inputs)
//            val out = Day7().executeProgram(program, listOf(5, 2380))
//            println("out: $out")

//            val out = Day7().part1execute(sampleProgram3, sampleInputs3)
//            println("out: $out")

//            val out = Day7().part1execute(sampleProgram2, sampleInputs2)
//            println("out: $out")

//            val out = Day7().part1execute(sampleProgram, sampleInputs)
//            println("out: $out")

//            val out = Day7().part1execute(inputProgram, listOf(2, 4, 4, 4, 4))
//            println("out: $out")

//            Day7().part1(inputProgram)
//            Day7().part1(sampleProgram3)

//            Day7().testChannel()

//            Day7().part1(testInput)
            val output = Day7().part1execute(testInput, listOf(9,8,7,6,5))
            println("output $output")

//            val output = Day7().part1execute(testInputPart2B, listOf(9,7,8,5,6))
//            println("output $output")
//            Day7().part1(testInput)

//            Day7().part1(inputProgram)
        }
    }

    private fun part1(program: List<Long>) {
        var maxOutput = Int.MIN_VALUE
        var maxOutputVector = listOf(0, 0, 0, 0, 0)

        val phaseVector = IntArray(5)
        phaseVector.fill(-1)

//        val phaseRange = 0..4
        val phaseRange = 5..9

        for (a in phaseRange) {
            phaseVector[0] = a
            for (b in phaseRange) {
                if (phaseVector.contains(b)) continue
                phaseVector[1] = b
                for (c in phaseRange) {
                    if (phaseVector.contains(c)) continue
                    phaseVector[2] = c
                    for (d in phaseRange) {
                        if (phaseVector.contains(d)) continue
                        phaseVector[3] = d
                        for (e in phaseRange) {
                            if (phaseVector.contains(e)) continue
                            phaseVector[4] = e
                            val inputs = listOf(a, b, c, d, e)
                            if (debugLevel >= INFO) {
                                println("trying (${inputs.joinToString()})")
                            }

                            try {
                                val output = part1execute(program, inputs)
                                if (output > maxOutput) {
                                    if (debugLevel >= VERBOSE) {
                                        println("Max so far $output, (${inputs.joinToString()})")
                                    }
                                    maxOutput = output
                                    maxOutputVector = inputs
                                }
                            } catch (e: RuntimeException) {
                                if (debugLevel >= INFO) {
                                    println("Input (${inputs.joinToString()}) failed")
                                }
                            }

                            phaseVector[4] = -1
                        }
                        phaseVector[3] = -1
                    }
                    phaseVector[2] = -1
                }
                phaseVector[1] = -1
            }
            phaseVector[0] = -1
        }

        if (debugLevel >= DEBUG) {
            println("max: $maxOutput")
            println("best vector: ${maxOutputVector.joinToString()}")
        }
    }

    private fun part1execute(program: List<Long>, inputs: List<Int>): Int {
        var lastOutput = 0

        val queues = (0..4).map { ArrayDeque<Long>(100) }

        inputs.forEachIndexed { index, phase ->
            queues[index].offer(phase.toLong())
        }

        val computers = ArrayList<Computer>(5)
        repeat(5) { computerId ->
            computers.add(Computer(program, queues[computerId], queues[(computerId + 1) % 5 ]))
        }

        // Seed the first computer.  The rest should already be hooked to the others output
        queues[0].offer(0)

//        dumpQueues(queues)

        while (true) {
            computers.forEachIndexed {computerId, computer ->
//                println("computerId: $computerId")
//                dumpQueues(queues, computer)
                computer.execute()
//                dumpQueues(queues, computer)
                if (debugLevel >= DEBUG) {
                    println("${COLORS.GREEN}${computerId} done: $lastOutput ${NO_COLOR}")
                    println("-----------------------------------")
                }
            }

//            dumpQueues(queues)

//            computers.forEach { println(it.state) }

            val finished = computers.count { it.state == Computer.DONE } >= (computers.size - 1)
            if (finished) break
        }

        return queues.first().remove().toInt()
    }

    private fun dumpQueues(queues: List<ArrayDeque<Long>>, computer: Computer? = null) {
        queues.forEachIndexed { index, queue ->
            var color = NO_COLOR
            computer?.let { computer ->
                if (computer.inputQueue == queue) {
                    color = COLORS.GREEN.toString()
                }
                if (computer.outputQueue == queue) {
                    color = COLORS.RED.toString()
                }
            }
            print("$color(${queue.joinToString()}), ${NO_COLOR}")
        }
        println()
    }
}

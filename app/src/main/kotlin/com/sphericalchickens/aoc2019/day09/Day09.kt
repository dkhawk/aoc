package com.sphericalchickens.aoc2019.day09

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.io.File
import java.util.*

class Day9 {
    companion object {
        val testInput = "109,2000,109,19,204,-2018,99".split(",").map(String::toLong)
        val testInput2 = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99".split(",").map(String::toLong)
        val testInput3 = "1102,34915192,34915192,7,4,7,99,0".split(",").map(String::toLong)
        val testInput4 = "104,1125899906842624,99".split(",").map(String::toLong)

        val inputProgram = File("/Users/dkhawk/Downloads/2019/input-9.txt").readText().trim().split(",").map(String::toLong)

        fun test() {
            Day9().part1(inputProgram)
        }
    }

    private fun part1(input: List<Long>) {
        val bufferIn = ArrayDeque<Long>()
        val bufferOut = ArrayDeque<Long>()
        bufferIn.offer(2)
        val computer = Computer(input, bufferIn, bufferOut)
        computer.execute()
        println(bufferOut.joinToString())
    }
}
package com.sphericalchickens.aoc2019.day16

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import kotlinx.coroutines.GlobalScope
import java.io.File
import kotlin.math.abs
import kotlinx.coroutines.*

class Day16 {
    companion object {
        val part1Input = File("/Users/dkhawk/Downloads/2019/input-16.txt").readText().trim()

        fun test() {
//            Day16().part1("12345678")
//            Day16().part1("80871224585914546619083218645595")
//            Day16().part1("19617804207202209144916044189917")
//            Day16().part1(part1Input)

            Day16().part2("03036732577212944063491565474664")
        }
    }

    private fun part1(input: String) {
        val basePattern = listOf(0, 1, 0, -1)
        var number = input.toList().map{ it.toInt() - 48 }
        println(number.joinToString(""))

        repeat(100) {
            number = calculatePhase(number, basePattern)
//            println(number.joinToString(""))
        }
        println(number.subList(0, 8).joinToString(""))
    }

    private fun part2(input: String) {
        val basePattern = listOf(0, 1, 0, -1)
        var temp = input.toList().map{ it.toInt() - 48 }.toIntArray()
        var temp2 = IntArray(temp.size * 10_000)
        repeat(10_000) {
            temp.copyInto(temp2, it * temp.size)
        }
        var number = temp2.toList()
//        println(number.slice(0..(temp.size * 3 - 1)).joinToString(""))

        val startTime = System.currentTimeMillis()
        repeat(100) {
            println(it)
            println("start: ${(System.currentTimeMillis() - startTime) / 1000.0}")
            number = calculatePhase(number, basePattern)
//            println(number.joinToString(""))
            println("end: ${(System.currentTimeMillis() - startTime) / 1000.0}")
        }
        println(number.subList(0, 8).joinToString(""))
    }

    private fun calculatePhase(
        input: List<Int>,
        basePattern: List<Int>
    ) = runBlocking<List<Int>> {
        val output = IntArray(input.size)
        val jobs = ArrayList<Job>(input.size)
        for (position in 1..input.size) {
//            println("$position (${position.toDouble()/input.size})")
            val job = launch {
                val sum = computeRow(input, position, basePattern)
                output[position - 1] = abs(sum) % 10
            }
            jobs.add(job)
//            val sum = computeRow(input, position, basePattern)
        }
        jobs.forEach { it.join() }
        output.toList()
    }

    private suspend fun computeRow(
        input: List<Int>,
        position: Int,
        basePattern: List<Int>
    ): Int {
//        val startTime = System.currentTimeMillis()
        var sum = 0
        for (i in 1..input.size) {
            val baseIndex = i / position
            val m = basePattern[baseIndex % basePattern.size]
            val factor = m * input[i - 1]
            sum += factor
        }
//        println("end: ${(System.currentTimeMillis() - startTime) / 1000.0}")
        return sum
    }
}

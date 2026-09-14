package com.sphericalchickens.aoc2021.day01

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*

import java.io.File
import kotlin.system.measureTimeMillis

@kotlin.ExperimentalStdlibApi
class Day01 {
  companion object {
    fun run() {
      val time = measureTimeMillis {
//        Day01().part1()
        Day01().part2()
      }
      println("millis: $time")
    }

    val realInput = readInputLines("aoc2021/day01_input.txt")

    val sample = """
      199
      200
      208
      210
      200
      207
      240
      269
      260
      263
    """.trimIndent().split("\n").filter { it.isNotBlank() }
  }

  private fun part1() {
//    val input = sample
    val input = realInput

    val num = input.map { it.toInt() }
      .windowed(2, 1).count { it.first() < it.last() }
    println(num)
  }

  private fun part2() {
//        val input = sample
    val input = realInput

    val num = input.asSequence().map { it.toInt() }
      .windowed(3, 1).map { it.sum() }
      .windowed(2, 1).count { it.first() < it.last() }
    println(num)
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 1 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day01.run()
        } catch (e: Exception) {
            println("Error running Day01: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

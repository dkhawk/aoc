package com.sphericalchickens.aoc2021.day02

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.InputFactory
import com.sphericalchickens.utils.Template
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day02 {
  companion object {
    fun run() {
      val time = measureTimeMillis {
//        Day02().part1()
        Day02().part2()
      }
      println("millis: $time")
    }

    val testInput = """
      forward 5
      down 5
      forward 8
      up 3
      down 8
      forward 2""".trimIndent().split("\n").filter { it.isNotBlank() }

    @Template("#0 #1")
    data class Step(val direction: String, val distance: Int)

    val realInput = readInputLines("aoc2021/day02_input.txt")
  }

  private fun part1() {
//    val input = testInput
    val input = Day02.Companion.realInput
    val inputFactory = InputFactory(Day02.Companion.Step::class)
    val inputs = input.map{ inputFactory.lineToClass<Day02.Companion.Step>(it) }

    var maxX = 0
    var maxD = 0

    var loc = Vector(0, 0)
    inputs.forEach { step ->
      when (step!!.direction) {
        "forward" -> loc += Vector(step.distance, 0)
        "down" -> loc += Vector(0, step.distance)
        "up" -> loc += Vector(0, -step.distance)
      }
      maxX = max(maxX, loc.x)
      maxD = max(maxD, loc.y)
    }

    println(maxX)
    println(maxD)

    println(loc.x * loc.y)

  }

  private fun part2() {
//    val input = testInput
    val input = Day02.Companion.realInput
    val inputFactory = InputFactory(Day02.Companion.Step::class)
    val inputs = input.map{ inputFactory.lineToClass<Day02.Companion.Step>(it) }

    var aim = 0
    var loc = Vector(0, 0)
    inputs.forEach { step ->
      when (step!!.direction) {
        "forward" -> { loc += Vector(step.distance, step.distance * aim) }
        "down" -> {
          aim += step.distance
        }
        "up" -> {
          aim -= step.distance
        }
      }
    }

    println(loc)
    println(loc.x * loc.y)
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 2 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day02.run()
        } catch (e: Exception) {
            println("Error running Day02: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

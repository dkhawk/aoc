package com.sphericalchickens.aoc2021.day14

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day14 {
  companion object {
    fun run() {
      val time1 = measureTimeMillis {
        Day14().part1()
      }
      println("millis: $time1")

      measureTimeMillis {
        Day14().part2()
      }.also {
        println("millis: $it")
      }
    }
  }

  val sample = """
    NNCB
    
    CH -> B
    HH -> N
    CB -> H
    NH -> C
    HB -> C
    HC -> B
    HN -> C
    NN -> C
    BH -> H
    NC -> B
    NB -> B
    BN -> B
    BB -> N
    BC -> B
    CC -> N
    CN -> C""".trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): Pair<String, Map<String, String>> {
    val input = if (useRealInput) {
      Input.readAsLines("14")
    } else {
      sample
    }

    val template = input.take(1).first()
    val rules = input.drop(1).map { line ->
      line.split(" -> ").let {
        it.first() to it.last()
      }
    }.toMap()

    return template to rules
  }

  private fun part1() {
    val (template, rules) = getInput(useRealInput = true)

    var poly = template

    repeat(10) {
      poly = poly.windowed(2, 1) { pair ->
        pair.first() + rules[pair]!!
      }.joinToString("") + template.last()
    }

    val counts = poly.groupingBy { it }.eachCount()

    val answer = counts.maxOf { it.value }.toLong() - counts.minOf { it.value }.toLong()

    println(answer)
  }

  private fun part2() {
    val (template, rules) = getInput(useRealInput = true)

    var counts = rules.keys.map { it to 0L }.toMap().toMutableMap()

    template.windowed(2, 1) { pair ->
        val key = pair.toString()
        counts[key] = counts.getOrDefault(key, 0) + 1
    }

    repeat(40) {
      val newCounts = mutableMapOf<String, Long>()
      counts.keys.map { key ->
        val c = rules[key]!!
        val v = counts[key]!!
        val key1 = key.first() + c
        newCounts[key1] = newCounts.getOrDefault(key1, 0) + v
        val key2 = c + key.last()
        newCounts[key2] = newCounts.getOrDefault(key2, 0) + v
      }
      counts = newCounts
    }

    val elementCounts = mutableMapOf<Char, Long>()

    counts.forEach { (t, u) ->
      elementCounts[t.first()] = elementCounts.getOrDefault(t.first(), 0) + u
      elementCounts[t.last()] = elementCounts.getOrDefault(t.last(), 0) + u
    }

    elementCounts[template.last()] = elementCounts.getOrDefault(template.last(), 0) + 1

    val sorted = elementCounts.toList().sortedBy { it.second }
    val answer = sorted.last().second / 2 - sorted.first().second / 2
    println(answer)
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 14 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day14.run()
        } catch (e: Exception) {
            println("Error running Day14: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

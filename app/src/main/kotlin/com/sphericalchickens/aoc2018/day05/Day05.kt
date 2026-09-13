package com.sphericalchickens.aoc2018.day05

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    dabAcCaCBAcCcaDA
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }

    // inputElves = input.mapIndexed { index, snacks -> toElf(index, snacks) }
  }

  fun part1() {
    val polymer = input.first().toPolymer()
    println(polymer)

    polymer.react()

    println(polymer)

    println(polymer.toString().length)
  }

  private fun react(polymer: StringBuilder): Boolean {
    val toRemove = polymer.withIndex().windowed(2, 1).firstOrNull { (a, b) ->
      a.value.lowercaseChar() == b.value.lowercaseChar() && a.value.isUpperCase() != b.value.isUpperCase()
    }

    return toRemove?.first()?.index?.let { index ->
      polymer.delete(index, index + 2)
      true
    } ?: false
  }

  fun part2() {
    val polymer = input.first().toPolymer()

    val all = polymer.toString().lowercase().toSet()

    var min = Int.MAX_VALUE

    all.forEach { bad ->
      val p = input.first().filterNot { it.lowercaseChar() == bad }.toPolymer()
      p.react()
      val l = p.toString().length
      if (l < min) {
        min = l
      }
    }

    println(min)
  }

  fun execute() {
    job?.cancel()
    job = scope.launch {
      running = true
      running = false
    }
  }

  fun step() {
  }

  fun stop() {
    job?.cancel()
    running = false
  }

  fun reset() {
    stop()
  }

  fun updateDataSource(useRealData: Boolean) {
    this.useRealData = useRealData
    initialize()
    reset()
  }
}

private fun String.toPolymer(): Polymer {
  return Polymer(this)
}

class Node(val value: Char, var next: Node?)

class Polymer(ps: String) {
  private var head: Node = Node(0.toChar(), null)

  init {
    var tail = Node(0.toChar(), null)
    ps.reversed().forEach {
      val n = Node(it, tail)
      tail = n
    }
    head = tail
  }

  override fun toString(): String {
    var n = head
    val sb = StringBuilder()
    while (n.next != null) {
      sb.append(n.value)
      n = n.next!!
    }
    return sb.toString()
  }

  fun react() {
    var changed = true
    while (changed) {
      changed = false
      var current = head
      var prev: Node? = null
      while (current.next != null) {
        val next = current.next!!
        if (current.value.lowercaseChar() == next.value.lowercaseChar() && current.value.isUpperCase() != next.value.isUpperCase()) {
          changed = true
          if (prev != null) {
            prev.next = next.next
          } else {
            head = next.next!!
          }
        }
        prev = current
        current = next
      }
    }
  }
}

fun main() {
    println("--- Advent of Code 2018, Day 5 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}

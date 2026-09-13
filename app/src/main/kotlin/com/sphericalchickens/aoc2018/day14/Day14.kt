package com.sphericalchickens.aoc2018.day14

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
    2018
  """.trimIndent().split("\n")

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  data class Node(val id: Int, val value: Int, var next: Node? = null, var prev: Node? = null)

  fun part1() {
    val goal = input.first().toInt()
    var nextNodeId = 0
    val root = Node(nextNodeId++, 3)
    var tail = Node(nextNodeId++,7, prev = root)
    root.next = tail

    val elves = mutableListOf(root, tail)

    val lastTen = mutableListOf<Int>()

    while(nextNodeId < (goal + 10)) {
      val sum = elves.sumOf { it.value }
      if (sum >= 10) {
        val newNode = Node(nextNodeId++, 1, prev = tail)
        tail.next = newNode
        tail = newNode
        if (tail.id >= goal) {
          lastTen.add(tail.value)
        }
      }

      val newNode = Node(nextNodeId++, sum % 10, prev = tail)
      tail.next = newNode
      tail = newNode
      if (tail.id >= goal) {
        lastTen.add(tail.value)
      }

      repeat(2) { elfId ->
        repeat(elves[elfId].value + 1) {
          elves[elfId] = elves[elfId].next ?: root
        }
      }

      // println(recipesToString(root, elves))
    }

    println(lastTen.take(10).joinToString(""))
  }

  private fun recipesToString(root: Node, elves: List<Node>): String {
    val sb = StringBuilder()

    var current: Node? = root

    while (current != null) {
      val s = if (current.id == elves[0].id) {
        "(${current.value})"
      } else if (current.id == elves[1].id) {
        "[${current.value}]"
      } else {
        " ${current.value} "
      }
      sb.append(s)
      current = current.next
    }

    return sb.toString()
  }

  fun part2() {
    /*
    51589 first appears after 9 recipes.
    01245 first appears after 5 recipes.
    92510 first appears after 18 recipes.
    59414 first appears after 2018 recipes.
     */
    val goalString = input.first()  // 59414

    var nextNodeId = 0
    val root = Node(nextNodeId++, 3)
    var tail = Node(nextNodeId++,7, prev = root)
    root.next = tail

    val elves = mutableListOf(root, tail)

    val lastTen = ArrayDeque<Int>(10)

    while(true) {
      val sum = elves.sumOf { it.value }
      if (sum >= 10) {
        val newNode = Node(nextNodeId++, 1, prev = tail)
        tail.next = newNode
        tail = newNode
        lastTen.add(tail.value)
        while (lastTen.size > goalString.length) {
          lastTen.removeFirst()
        }
        if (lastTen.joinToString("") == goalString) {
          break
        }
      }

      val newNode = Node(nextNodeId++, sum % 10, prev = tail)
      tail.next = newNode
      tail = newNode
      lastTen.add(tail.value)
      while (lastTen.size > goalString.length) {
        lastTen.removeFirst()
      }
      if (lastTen.joinToString("") == goalString) {
        break
      }

      repeat(2) { elfId ->
        repeat(elves[elfId].value + 1) {
          elves[elfId] = elves[elfId].next ?: root
        }
      }

      // println(recipesToString(root, elves))
    }

    println((nextNodeId - goalString.length))
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


fun main() {
    println("--- Advent of Code 2018, Day 14 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}

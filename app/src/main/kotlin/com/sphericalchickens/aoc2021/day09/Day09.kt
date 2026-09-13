package com.sphericalchickens.aoc2021.day09

import com.sphericalchickens.utils.*


import java.util.LinkedList
import java.util.Queue
import com.sphericalchickens.utils.CharGrid
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day09 {
  companion object {
    fun run() {
//      Day09().part1()
      Day09().part2()
    }
  }

  val sample = """
      2199943210
      3987894921
      9856789892
      8767896789
      9899965678
      """.trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): CharGrid {
    val input = if (useRealInput) {
      Input.readAsLines("09")  // <====== TODO Set the day number!!!!
    } else {
      sample
    }

    return CharGrid(input)
  }

  private fun part1() {
    val grid = getInput(useRealInput = true)
//    println(grid)

    val s = grid.width * grid.height
    val lows = mutableListOf<Int>()
    (0 until s).forEach { it ->
      val t = grid.getIndex(it)
      val m = grid.getNeighbors(it).minOf { it }
      if (t < m) {
//        println("$t is a min")
//        val v = grid.indexToVector(it)
        lows.add((t - '0') + 1)
      }
    }

    println(lows.sum())
  }

  private fun part2() {
    val grid = getInput(useRealInput = true)

    val s = grid.width * grid.height
    val lows = mutableListOf<Vector>()
    (0 until s).forEach { it ->
      val t = grid.getIndex(it)
      val m = grid.getNeighbors(it).minOf { it }
      if (t < m) {
        val v = grid.indexToVector(it)
        lows.add(v)
      }
    }

    val bs = lows.map { low ->
      findBasin(low, grid)
    }

    val a = bs.map { it.size.toLong() }.sortedDescending().take(3)
    val m = a.reduce { acc, it -> acc * it }
    println(m)
  }

  private fun findBasin(low: Vector, grid: CharGrid): HashSet<Vector> {
    val queue: Queue<Vector> = LinkedList()
    queue.add(low)

    val seen = HashSet<Vector>()
    while (queue.isNotEmpty()) {
      val v = queue.remove()
      seen.add(v)
      queue.addAll(
        grid.getNeighborsIf(v) { c, vect ->
          c < '9' && !seen.contains(vect) && !queue.contains(vect)
        })
    }

    return seen
  }
}

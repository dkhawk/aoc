package com.sphericalchickens.aoc2021.day11

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import java.util.LinkedList
import java.util.Queue
import com.sphericalchickens.utils.CharGrid
import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day11 {
  companion object {
    fun run() {
      Day11().part1()
      Day11().part2()
    }
  }

  val sample = """
      5483143223
      2745854711
      5264556173
      6141336146
      6357385478
      4167524645
      2176841721
      6882881134
      4846848554
      5283751526
      """.trimIndent().split("\n").filter { it.isNotBlank() }

  val sample2 = """
    11111
    19991
    19191
    19991
    11111
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): CharGrid {
    val input = if (useRealInput) {
      Input.readAsLines("11")  // <====== TODO Set the day number!!!!
    } else {
      sample
    }

    return CharGrid(input)
  }

  private fun part1() {
    var grid = getInput(useRealInput = true)

    var flashCount = 0

    repeat(100) {
      // Increase energy level by one
      val nextGrid = grid.advance { _, _, c -> c + 1 }

      // Find initial flashes
      val flashes = nextGrid.findAll { _, value ->
        value > '9'
      }.map { it.first }

      val flashed = mutableSetOf<Int>()
      val toFlash : Queue<Int> = LinkedList()
      toFlash.addAll(flashes)

      while (toFlash.isNotEmpty()) {
        val flasher = toFlash.remove()
        flashed.add(flasher)

        nextGrid.getNeighbor8sWithLocation(flasher).forEach { p ->
          val v = p.first
          val c = nextGrid.get(v)
          if (c <= '9') {
            val newValue = c + 1
            nextGrid.setCell(v, newValue)
            if (newValue > '9') {
              val index = nextGrid.vectorToIndex(v)
              if (index !in flashed && index !in toFlash) {
                toFlash.add(index)
              }
            }
          }
        }
      }

      flashCount += flashed.size

      flashed.forEach { index ->
        nextGrid.setIndex(index, '0')
      }

      grid = nextGrid
//      println(grid.toStringWithHighlights { c, _ -> c == '0' })
    }
    println("flashCount: $flashCount")
  }

  private fun part2() {
    var grid = getInput(useRealInput = true)

    var flashCount = 0

    repeat(1_000) { cycle ->
      // Increase energy level by one
      val nextGrid = grid.advance { _, _, c -> c + 1 }

      // Find initial flashes
      val flashes = nextGrid.findAll { _, value ->
        value > '9'
      }.map { it.first }

      val flashed = mutableSetOf<Int>()
      val toFlash : Queue<Int> = LinkedList()
      toFlash.addAll(flashes)

      while (toFlash.isNotEmpty()) {
        val flasher = toFlash.remove()
        flashed.add(flasher)

        nextGrid.getNeighbor8sWithLocation(flasher).forEach { p ->
          val v = p.first
          val c = nextGrid.get(v)
          if (c <= '9') {
            val newValue = c + 1
            nextGrid.setCell(v, newValue)
            if (newValue > '9') {
              val index = nextGrid.vectorToIndex(v)
              if (index !in flashed && index !in toFlash) {
                toFlash.add(index)
              }
            }
          }
        }
      }

      flashCount += flashed.size

      if (flashed.size == nextGrid.grid.size) {
        println("In sync at ${cycle + 1}")
        return
      }

      flashed.forEach { index ->
        nextGrid.setIndex(index, '0')
      }

      grid = nextGrid
//      println(grid.toStringWithHighlights { c, _ -> c == '0' })
    }
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 11 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day11.run()
        } catch (e: Exception) {
            println("Error running Day11: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

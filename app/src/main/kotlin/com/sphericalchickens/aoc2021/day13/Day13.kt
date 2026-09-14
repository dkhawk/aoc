package com.sphericalchickens.aoc2021.day13

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.CharGrid
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day13 {
  companion object {
    fun run() {
//      Day13().part1()
      Day13().part1a()
//      Day13().part2()
      Day13().part2a()
    }

//    val off = '.'
//    val on = '#'
    val off = ' '
    val on = '█' // is more visible
  }

  val sample = """
      6,10
      0,14
      9,10
      0,3
      10,4
      4,11
      6,0
      6,12
      4,1
      0,13
      10,12
      3,4
      3,0
      8,4
      1,10
      2,14
      8,10
      9,0
      
      fold along y=7
      fold along x=5
      """.trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): Pair<List<List<Int>>, List<Pair<String, Int>>> {
    val input = if (useRealInput) {
      Input.readAsLines("13")
    } else {
      sample
    }

    val parts = input.partition { it.startsWith("fold") }
    val coords = parts.second.map { it.split(",").map { it.toInt() } }

    val folds = parts.first.map { it.split("=").let { it.first().takeLast(1) to it.last().toInt() } }

    return Pair(coords, folds)
  }

  private fun part1() {
    val inputs = getInput(useRealInput = true)
    val folds = inputs.second

    var grid = createGrid(inputs.first)

    folds.take(1).forEach { fold ->
      if (fold.first == "y") {
        grid = grid.foldY(fold.second)
      } else {
        grid = grid.foldX(fold.second)
      }
    }

    println(grid.grid.count { it == on })
  }

  private fun part1a() {
    val inputs = getInput(useRealInput = true)
    val folds = inputs.second

    var grid = createGrid(inputs.first)

    folds.take(1).forEach { fold ->
      grid = if (fold.first == "y") {
        foldVerticalByTaking(grid, fold.second)
      } else {
        foldHorizontalByTaking(grid, fold.second)
      }
    }

    println(grid.grid.count { it == on })
  }

  private fun part2() {
    val inputs = getInput(useRealInput = true)
    val folds = inputs.second

    var grid = createGrid(inputs.first)

    folds.forEachIndexed { index, fold ->
      grid = if (fold.first == "y") {
        grid.foldY(fold.second)
      } else {
        grid.foldX(fold.second)
      }
    }

    println(grid)
  }

  private fun part2a() {
    val inputs = getInput(useRealInput = true)
    val folds = inputs.second

    var grid = createGrid(inputs.first)

    folds.forEachIndexed { index, fold ->
      grid = if (fold.first == "y") {
        foldVerticalByTaking(grid, fold.second)
      } else {
        foldHorizontalByTaking(grid, fold.second)
      }
    }

    println(grid)
  }

  private fun foldHorizontalByTaking(grid: CharGrid, foldLine: Int): CharGrid {
    return foldVerticalByTaking(grid.rotateClockwise(), foldLine).rotateCounterClockwise()
  }

  private fun foldVerticalByTaking(
    grid: CharGrid,
    foldLine: Int,
  ): CharGrid {
    val gridLines = grid.grid.toList().windowed(grid.width, grid.width)

    val list1 = gridLines.take(foldLine).reversed()
    val list2 = gridLines.drop(foldLine + 1)

    // Deal with a non-centered fold
    val lines = maxOf(list1.size, list2.size)

    val padLine = off.toString().repeat(grid.width).toList()
    val thing = (0 until lines).map { index ->
      val a = list1.getOrNull(index) ?: padLine
      val b = list2.getOrNull(index) ?: padLine
      a to b
    }.map {
      val first = it.first
      val second = it.second

      first.toList().zip(second.toList()).map { (a, b) ->
        if (a == off && b == off) {
          off
        } else {
          on
        }
      }.joinToString("")
    }.reversed()

    return CharGrid(thing)
  }

  private fun createGrid(coords: List<List<Int>>): CharGrid {
    val maxX = coords.maxOf { it.first() } + 1
    val maxY = coords.maxOf { it.last() } + 1

    val grid = CharGrid(maxX, maxY, off)

    coords.map { loc ->
      Vector(loc.first(), loc.last())
    }.forEach {
      grid.setCell(it, on)
    }

    return grid
  }
}

private fun CharGrid.foldY(foldLine: Int): CharGrid {
  val out = CharGrid(this.width, this.height / 2 + 1, Day13.off)

  (0 until height).map { row ->
    (0 until width).map { col ->
      if (row < foldLine) {
        out.setCell_xy(col, row, getCell_xy(col, row))
      } else if (row > foldLine) {
        if (getCell_xy(col, row) == Day13.on) {
          val r = foldLine - (row - foldLine)
          out.setCell_xy(col, r, Day13.on)
        }
      }
    }
  }

  return out
}

private fun CharGrid.foldX(foldLine: Int): CharGrid {
  val out = CharGrid(this.width / 2, this.height, Day13.off)

  (0 until height).map { row ->
    (0 until width).map { col ->
      if (col < foldLine) {
        out.setCell_xy(col, row, getCell_xy(col, row))
      } else if (col > foldLine) {
        if (getCell_xy(col, row) == Day13.on) {
          val r = foldLine - (col - foldLine)
          out.setCell_xy(r, row, Day13.on)
        }
      }
    }
  }

  return out
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 13 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day13.run()
        } catch (e: Exception) {
            println("Error running Day13: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

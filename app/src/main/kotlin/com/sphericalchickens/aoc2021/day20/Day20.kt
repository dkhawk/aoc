package com.sphericalchickens.aoc2021.day20

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.Heading8
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day20 {
  companion object {
    fun run() {
      Day20().part1()
      Day20().part2()
    }
  }

  val sample = """
      ..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##
      #..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###
      .######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#.
      .#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#.....
      .#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#..
      ...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.....
      ..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#
      
      #..#.
      #....
      ##..#
      ..#..
      ..###
      """.trimIndent()

  private fun getInput(useRealInput: Boolean): Pair<String, String> {
    val input = if (useRealInput) {
      Input.readAsString("20")
    } else {
      sample
    }

    val (enhancerLines, image) = input.split("\n\n")

    val enhancer = enhancerLines.filter { it in listOf('.', '#') }

    return enhancer to image
  }

  private fun part1() {
    val realInput = true
    val (enhancer, image) = getInput(useRealInput = realInput)

    println(enhancer)

    val grid = InfiniteGrid(image)

    repeat(2) { cycle ->
      if (realInput) {
        grid.defaultChar = if (cycle % 2 == 0) '.' else '#'
      }
      enhance(grid, enhancer)
      if (cycle % 2 == 1) {
        crop(grid)
      }
    }

    println(grid.toImage(2))

    println(grid.characterMap.count { it.value == '#' })
  }

  private fun part2() {
    val realInput = true
    val (enhancer, image) = getInput(useRealInput = realInput)

    println(enhancer)

    val grid = InfiniteGrid(image)

    repeat(50) { cycle ->
      if (realInput) {
        grid.defaultChar = if (cycle % 2 == 0) '.' else '#'
      }
      enhance(grid, enhancer)
      if (cycle % 2 == 1) {
        crop(grid)
      }
    }

    println(grid.toImage(2))

    println(grid.characterMap.count { it.value == '#' })
  }

  private fun crop(grid: InfiniteGrid) {
    val filtered = grid.characterMap.filter { (vector, c) ->
      c == '#'
    }
    grid.characterMap.clear()
    grid.characterMap.putAll(filtered)
  }

  private fun enhance(grid: InfiniteGrid, enhancer: String) {
    val yRange = grid.yRange(4)
    val xRange = grid.xRange(4)
    yRange.flatMap { row ->
      xRange.map { col ->
        val vector = Vector(col, row)
        val index = grid.getNeighbors9(vector)
          .map { if (it.second == '#') 1 else 0 }
          .reduce { acc, i ->
            acc.shl(1).or(i)
          }
        vector to enhancer[index]
      }
    }.forEach { (vector, value) ->
      grid[vector] = value
    }
  }
}

class InfiniteGrid(inputString: String, var defaultChar: Char = '.') {
  val characterMap: MutableMap<Vector, Char>

  init {
    characterMap = inputString.split("\n").flatMapIndexed { row, line ->
      line.mapIndexedNotNull { col, c ->
        Vector(col, row) to c
      }
    }.toMap().toMutableMap()
  }

  fun toImage(padding: Int = 0): String {
    return yRange(padding).joinToString("\n") { row ->
      xRange(padding).map { col ->
        characterMap.getOrDefault(Vector(col, row), defaultChar)
      }.joinToString("")
    }
  }

  fun yRange(padding: Int = 0): IntRange = characterMap.keys.map { it.y }.toRange(padding)
  fun xRange(padding: Int = 0): IntRange = characterMap.keys.map { it.x }.toRange(padding)

  fun getNeighbors9(vector: Vector): List<Pair<Vector, Char>> {
    return listOf(
      Heading8.NORTHWEST.vector,
      Heading8.NORTH.vector,
      Heading8.NORTHEAST.vector,
      Heading8.WEST.vector,
      Vector(),
      Heading8.EAST.vector,
      Heading8.SOUTHWEST.vector,
      Heading8.SOUTH.vector,
      Heading8.SOUTHEAST.vector,
    ).map { heading ->
      (vector + heading).let { v ->
        v to getCell(v)
      }
    }
  }

  private fun getCell(location: Vector): Char = characterMap.getOrDefault(location, defaultChar)

  operator fun set(vector: Vector, value: Char) {
    characterMap[vector] = value
  }
}

private fun List<Int>.toRange(padding: Int = 0): IntRange =
  (this.minOf { it } - padding)..(this.maxOf { it } + padding)


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 20 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day20.run()
        } catch (e: Exception) {
            println("Error running Day20: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

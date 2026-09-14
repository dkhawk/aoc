package com.sphericalchickens.aoc2018.day06

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


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

  var maxSum = -1

  val sampleInput = """
    1, 1
    1, 6
    8, 3
    3, 4
    5, 5
    8, 9
  """.trimIndent().split("\n")

  fun initialize() {
    maxSum = if (useRealData) 10_000 else 32

    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val coords = input.map { it.split(", ").map { it.toInt() }.let {Vector(it[0], it[1])} }

    val (xs, ys) = coords.unzip()

    val (xmin, xmax) = xs.range()
    val (ymin, ymax) = ys.range()

    val min0 = Vector(xmin, ymin)
    val max0 = Vector(xmax, ymax)

    val min = Vector(xmin - 1, ymin - 1)
    val max = Vector(xmax + 1, ymax + 1)

    val regions = mutableMapOf<Vector, Long>()

    val infiniteRegions = mutableSetOf<Vector>()

    (min.y .. max.y).forEach { yy ->
      (min.x .. max.x).forEach { xx ->
        val location = Vector(xx, yy)
        val distances = coords.map { center ->
          center to center.cityDistanceTo(location)
        }.sortedBy { it.second }
        if (distances[0].second != distances[1].second) {
          // is unique
          regions[distances[0].first] = regions.getOrDefault(distances[0].first, 0) + 1
          if (!location.inBounds(min0, max0)) {
            infiniteRegions.add(distances.first().first)
          }
        }
      }
    }

    val finiteRegions = regions.filterNot { it.key in infiniteRegions }
    println(finiteRegions.maxByOrNull { it.value })
  }

  fun part2() {
    val coords = input.map { it.split(", ").map { it.toInt() }.let {Vector(it[0], it[1])} }

    val (xs, ys) = coords.unzip()

    val (xmin, xmax) = xs.range()
    val (ymin, ymax) = ys.range()

    val min = Vector(xmin - 1, ymin - 1)
    val max = Vector(xmax + 1, ymax + 1)

    val total = (min.y .. max.y).sumOf { yy ->
      (min.x .. max.x).count { xx ->
        val location = Vector(xx, yy)
        coords.sumOf { center -> center.cityDistanceTo(location) } < maxSum
      }
    }

    println(total)
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
    println("--- Advent of Code 2018, Day 6 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    val (_, p1Duration) = measureTimedValue {
        try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Solving Part 2:")
    val (_, p2Duration) = measureTimedValue {
        try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
    }
    println("Part 2 runtime: ${formatDuration(p2Duration)}")
}

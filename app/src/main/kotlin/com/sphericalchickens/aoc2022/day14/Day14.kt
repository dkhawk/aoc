package com.sphericalchickens.aoc2022.day14

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue


import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 14
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    498,4 -> 498,6 -> 496,6
    503,4 -> 502,4 -> 502,9 -> 494,9
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val map = createMap(input)
    val sandMap = startSand(map)
    println(sandMap.size)
  }

  fun part2() {
    val map = createMap(input)
    val sandMap = startSand2(map)
    println(sandMap.size)
  }

  private val directions = listOf(
    Vector(0, 1),
    Vector(-1, 1),
    Vector(1, 1)
  )

  private fun startSand2(map: Set<Vector>): MutableSet<Vector> {
    val floor = map.maxByOrNull { it.y }!!.y + 2
    val sandMap = mutableSetOf<Vector>()

    var finished = false

    while(!finished) {
      var sand = Vector(500, 0)
      var blocked = false

      while (!(blocked || finished)) {
        val newLocation = directions.firstOrNull { heading ->
          val new = sand + heading
          val isBlocked = map.contains(new) || sandMap.contains(new) || new.y == floor
          !isBlocked
        }?.plus(sand)

        if (newLocation == null) {
          blocked = true
        } else {
          sand = newLocation
        }

        if (sand.y == 0) {
          sandMap.add(sand)
          finished = true
        }
      }

      if (!finished) {
        sandMap.add(sand)
      }
    }
    return sandMap
  }

  private fun startSand(map: Set<Vector>): MutableSet<Vector> {
    val yMax = map.maxByOrNull { it.y }!!.y
    val sandMap = mutableSetOf<Vector>()

    var finished = false

    while(!finished) {
      var sand = Vector(500, 0)
      var blocked = false

      while (!(blocked || finished)) {
        val newLocation = directions.firstOrNull { heading ->
          val new = sand + heading
          val isBlocked = map.contains(new) || sandMap.contains(new)
          !isBlocked
        }?.plus(sand)

        if (newLocation == null) {
          blocked = true
        } else {
          sand = newLocation
        }

        if (sand.y >= yMax) {
          finished = true
        }
      }

      if (!finished) {
        sandMap.add(sand)
      }
    }
    return sandMap
  }

  private fun drawMap(map: Set<Vector>, sandMap: Set<Vector> = emptySet()) {
    val xMax = max(map.maxByOrNull { it.x }!!.x, sandMap.maxByOrNull { it.x }!!.x)
    val xMin = min(map.minByOrNull { it.x }!!.x, sandMap.minByOrNull { it.x }!!.x)
    val yMax = max(map.maxByOrNull { it.y }!!.y, sandMap.maxByOrNull { it.y }!!.y)
    val yMin = min(map.minByOrNull { it.y }!!.y, sandMap.minByOrNull { it.y }!!.y)

    val d = (yMin..yMax).joinToString("\n") { row ->
      (xMin..xMax).joinToString("") { col ->
        when {
          map.contains(Vector(col, row)) -> "#"
          sandMap.contains(Vector(col, row)) -> "o"
          else -> "."
        }
      }
    }
    println(d)
  }

  private fun createMap(input: List<String>): Set<Vector> {
    return input.flatMap { str ->
      str.split(" -> ")
        .map { parsePoint(it) }
        .windowed(2, 1)
        .flatMap { (start, finish) ->
          drawLine(start, finish)
      }
    }.toSet()
  }

  private fun drawLine(start: Vector, finish: Vector): List<Vector> {
    val heading = start.directionTo(finish).sign
    val coords = generateSequence(start) { loc ->
      if (loc == finish) {
        null
      } else {
        loc + heading
      }
    }
    return coords.toList()
  }

  fun parsePoint(str: String) : Vector =
    str.split(",").map { it.toInt() }
      .windowed(2, 2)
      .map { (x, y) -> Vector(x, y) }.first()

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
    println("--- Advent of Code 2022, Day 14 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}

package com.sphericalchickens.aoc2022.day18

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue


import java.util.PriorityQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 18
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<List<Int>>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    2,2,2
    1,2,2
    3,2,2
    2,1,2
    2,3,2
    2,2,1
    2,2,3
    2,2,4
    2,2,6
    1,2,5
    3,2,5
    2,1,5
    2,3,5
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  val sample2 = """
    1,1,1
    2,1,1
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  init {
  }

  fun initialize() {
    val lines = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines().filter { it.isNotBlank() }
      realInput
    } else {
      sampleInput
      // sample2
    }

    input = lines.map { it.split(",").map { it.toInt() } }
  }

  fun part1() {
    val cubes = input.map { Vector3d(it) }
    val occupied = cubes.toSet()

    val s = cubes.sumOf { cube ->
      cube.getNeighbors().count { it !in occupied }
    }

    println(s)
  }

  fun part2() {
    val cubes = input.map { Vector3d(it) }
    val occupied = cubes.toSet()

    val (minX, maxX) = cubes.minMaxOf { it.x }
    val (minY, maxY) = cubes.minMaxOf { it.y }
    val (minZ, maxZ) = cubes.minMaxOf { it.z }

    val min = Vector3d(minX - 1, minY - 1, minZ - 1)
    val max = Vector3d(maxX + 1, maxY + 1, maxZ + 1)

    val cube = Cube(min, max)
    println(cube.area)

    val container = mutableSetOf<Vector3d>()
    container.add(min)

    val queue = ArrayDeque<Vector3d>()
    queue.addLast(min)

    while (queue.isNotEmpty()) {
      val next = queue.removeFirst()
      next.getNeighbors()
        .filter { it in cube }
        .filter { it !in occupied }
        .filter { it !in container }
        .forEach {
          container.add(it)
          queue.addLast(it)
        }
    }

    val s = cubes.sumOf { pixel ->
      pixel.getNeighbors().count { it in container }
    }

    println(s)
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

private fun List<Vector3d>.minMaxOf(selector: (Vector3d) -> Int): Pair<Int, Int> {
  val iterator = iterator()
  if (!iterator.hasNext()) throw NoSuchElementException()
  var minValue = selector(iterator.next())
  var maxValue = minValue
  while (iterator.hasNext()) {
    val v = selector(iterator.next())
    if (minValue > v) {
      minValue = v
    }
    if (maxValue < v) {
      maxValue = v
    }
  }
  return minValue to maxValue
}

private fun List<Int>.minMax() = minOf { it } to maxOf { it }


class Cube(val minX: Int, val minY: Int, val minZ: Int, val maxX: Int, val maxY: Int, val maxZ: Int) {
  constructor(min: Vector3d, max: Vector3d): this(
    min.x, min.y, min.z, max.x, max.y, max.z
  )

  val xRange = minX..maxX
  val yRange = minY..maxY
  val zRange = minZ..maxZ

  val width = xRange.last - xRange.first
  val depth = yRange.last - yRange.first
  val height = zRange.last - zRange.first

  val area: Long
    get() = width.toLong() * height * depth

  operator fun contains(min: Vector3d) = min.x in xRange && min.y in yRange && min.z in zRange
}


fun main() {
    println("--- Advent of Code 2022, Day 18 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    println("Solving Part 2:")
    try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
}

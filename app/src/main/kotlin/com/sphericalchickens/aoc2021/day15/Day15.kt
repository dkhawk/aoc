package com.sphericalchickens.aoc2021.day15

import com.sphericalchickens.utils.*


import java.util.PriorityQueue
import kotlin.math.min
import com.sphericalchickens.utils.CharGrid
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day15 {
  companion object {
    fun run() {
      Day15().part1()
      Day15().part2()
      Day15().part1a()
      Day15().part2a()
    }
  }

  val sample = """
      1163751742
      1381373672
      2136511328
      3694931569
      7463417111
      1319128137
      1359912421
      3125421639
      1293138521
      2311944581
      """.trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): CharGrid {
    val input = if (useRealInput) {
      Input.readAsLines("15")
    } else {
      sample
    }

    return CharGrid(input)
  }

  private fun part1() {
    val grid = getInput(useRealInput = true)
    val cost = dijkstra(grid)
    println(cost)
  }

  private fun dijkstra(grid: CharGrid): Int? {
    val sptSet = mutableSetOf<Vector>()
    val costToNode = HashMap<Vector, Int>()

    val finish = Vector(grid.width - 1, grid.height - 1)

    costToNode[Vector(0, 0)] = 0

    val path = mutableListOf<Vector>()
    path.add(Vector(0, 0))

    val queue = PriorityQueue<WeightedVector>()
    queue.add(WeightedVector(0, 0, 0.0))

    while (queue.isNotEmpty()) {
      val weightedNext = queue.remove()
      val next = weightedNext.toVector()
      sptSet.add(next)

      if (next == finish) {
        println(costToNode.size)
        println(costToNode[next])
        return costToNode[next]
      }

      val currentCost = costToNode[next]!!

      grid.getNeighborsWithLocation(next).forEach { neighbor ->
        val v = neighbor.first
        if (v !in sptSet) {
          queue.removeIf { it.x == v.x && it.y == v.y }
          val c = min(neighbor.second.digitToInt() + currentCost, costToNode.getOrDefault(v, Int.MAX_VALUE))
          costToNode[v] = c
          queue.add(WeightedVector(v.x, v.y, c.toDouble()))
        }
      }
    }

    throw Exception("No path found")
  }

  private fun part1a() {
    val grid = getInput(useRealInput = true)

    println(aStar(grid))
  }

  private fun part2a() {
    val grid0 = getInput(useRealInput = true)
    val grid = generateGrid(grid0)

    println(aStar(grid))
  }

  private fun aStar(grid: CharGrid): Int {
    val start = Vector(0, 0)
    val finish = Vector(grid.width - 1, grid.height - 1)

    val estimatedCost = start.distance(finish)

    val openSet = PriorityQueue<WeightedVector>()
    openSet.add(WeightedVector(0, 0, estimatedCost))

    val gscore = mutableMapOf(
      start to 0
    )

    val fscore = mutableMapOf(
      start to estimatedCost
    )

    while (openSet.isNotEmpty()) {
      val weightedNext = openSet.remove()
      val next = weightedNext.toVector()
      if (next == finish) {
        println("score sizes ${gscore.size} ${fscore.size}")
        return gscore[weightedNext.toVector()]!!
      }

      grid.getNeighborsWithLocation(next).forEach { neighbor ->
        val neighborVector = neighbor.first
        val cost = neighbor.second.digitToInt()
        val tentativeScore = gscore.getOrDefault(next, Int.MAX_VALUE) + cost
        if (tentativeScore < gscore.getOrDefault(neighborVector, Int.MAX_VALUE)) {
          gscore[neighborVector] = tentativeScore
          fscore[neighborVector] = tentativeScore + neighborVector.distance(finish)

          val weighted =
            WeightedVector(neighborVector.x, neighborVector.y, fscore[neighborVector]!!)

          openSet.removeIf {
            it.x == neighborVector.x && it.y == neighborVector.y
          }
          openSet.add(weighted)
        }
      }
    }

    throw Exception("No solution")
  }

  private fun part2() {
    val grid0 = getInput(useRealInput = true)
    val grid = generateGrid(grid0)

    println(dijkstra(grid))
  }

  private fun generateGrid(grid: CharGrid): CharGrid {
    val grids = mutableMapOf(
      Vector(0, 0) to grid
    )

    (0 until 5).forEach { row ->
      (0 until 5).forEach { col ->
        val loc = Vector(col, row)
        if (!grids.contains(loc)) {
          // get the grid to the left
          val src = grids[Vector(col - 1, row)]!!
          val next = src.grid.map {
            var n = it + 1
            if (n > '9') {
              n = '1'
            }
            n
          }.toCharArray()
          val nextGrid = CharGrid(next, src.width)
          grids[loc] = nextGrid
          if (row < 4) {
            val l2 = Vector(col - 1, row + 1)
            grids[l2] = nextGrid
          }
        } else {
          if (row < 4) {
            val l2 = Vector(col - 1, row + 1)
            grids[l2] = grids[loc]!!
          }
        }
      }
    }

    val ca = (0 until 5 * grid.height).flatMap { row ->
      (0 until 5).flatMap { col ->
        val loc = Vector(col, row / grid.height)
        grids[loc]!!.getRow(row % grid.height).toList()
      }
    }.toCharArray()

    return CharGrid(ca,grid.width * 5, grid.height * 5)
  }
}

data class WeightedVector(val x: Int, val y: Int, val cost: Double) : Comparable<WeightedVector> {
  override fun compareTo(other: WeightedVector): Int {
    return cost.compareTo(other.cost)
  }

  fun toVector(): Vector {
    return Vector(x, y)
  }
}

package com.sphericalchickens.aoc2021.day17

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day17 {
  companion object {
    fun run() {
      Day17().part1()
      Day17().part2()
    }
  }

  private fun part2() {
//    val targetX = 20..30
//    val targetY = -10..-5

    val targetX = 150..171
    val targetY = -129..-70

    val successful = mutableSetOf<Vector>()

    (-200..5000).forEach { y0 ->
      (0..200).forEach { x0 ->
        val initial = Vector(x0,y0)

        var position = Vector(0, 0)
        var velocity = initial

        var count = 0

        while (true) {
          if (position.x in targetX && position.y in targetY) {
            successful.add(initial)
            break
          }

          if (position.x > targetX.last || position.y < targetY.first) {
            break
          }

          position += velocity
          velocity = Vector((velocity.x - 1).coerceAtLeast(0), velocity.y - 1)
          count += 1
        }
      }
    }

    println(successful.size)

    println(successful.maxOf { it.x })
    println(successful.minOf { it.x })
    println(successful.maxOf { it.y })
    println(successful.minOf { it.y })
  }

  private fun part1() {
//    val targetX = 20..30
//    val targetY = -10..-5

    val targetX = 150..171
    val targetY = -129..-70

    var globalMax = 0
    var best = Vector()

    (0..1000).forEach { y0 ->
      (0..100).forEach { x0 ->
        val initial = Vector(x0,y0)

        var position = Vector(0, 0)
        var velocity = initial

        var count = 0

        var maxY = 0

        var success = false

        while (true) {
          if (position.x in targetX && position.y in targetY) {
            success = true
            break
          }

          if (position.x > targetX.last || position.y < targetY.first) {
            success = false
            break
          }

          position += velocity
          velocity = Vector((velocity.x - 1).coerceAtLeast(0), velocity.y - 1)
          count += 1

          maxY = maxOf(maxY, position.y)
        }

        if (success && (maxY > globalMax)) {
          globalMax = maxY
          best = initial
        }
      }
    }

    println(globalMax)
    println(best)
  }
}

package com.sphericalchickens.aoc2016.day13

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 13 ---")

    val input = readInputLines("aoc2016/day13_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input.first().toInt(), Vector(31, 39))
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input.first().toInt())
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private var magicNumber = 0

private fun isWall(x: Int, y: Int): Boolean {
    val v = (x.toLong()*x + 3*x + 2*x*y + y + y*y) + magicNumber
    return v.toString(2).count { it == '1' }.and(1) == 1
}

private data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
    fun isWall() = isWall(this.x, this.y)
}

private fun runPart1Tests() {
    magicNumber = 10

    check("6,2 is open", false, Vector(2, 6).isWall())
    check("2,1 is wall", true, Vector(2, 1).isWall())
    check("Part 1 Test Case 1", 11, part1(10, Vector(7, 4)))
}

private fun part1(n: Int, goal: Vector): Int {
    val map = mutableMapOf<Vector, Boolean>()

    magicNumber = n

    // DFS
    val queue = PriorityQueue<Pair<Vector, List<Vector>>>(compareBy { it.second.size })
    queue.offer(Pair(Vector(1, 1), listOf(Vector(1, 1))))

    val visited = mutableSetOf(queue.peek().first)

    while (queue.isNotEmpty()) {
        val (location, path) = queue.poll()

        listOf(
            Vector(-1, 0),
            Vector(0, -1),
            Vector(1, 0),
            Vector(0, 1),
        ).map { location + it }
            .filter { it.x >= 0 && it.y >= 0 }
            .map { candidate ->
                candidate to map.getOrPut(candidate) {
                    candidate.isWall()
                }
            }
            .forEach { (loc, isWall) ->
                if (loc == goal) {
                    return path.size
                }

                if (!visited.contains(loc) && !isWall) {
                    visited.add(loc)
                    queue.offer(loc to (path + loc))
                }
            }
    }

    return -1
}

private fun showMap(map: MutableMap<Vector, Boolean>, path: List<Vector>) {
    val maxX = map.keys.maxOfOrNull { it.x }!!
    val maxY = map.keys.maxOfOrNull { it.y }!!

    buildString {
        for (y in 0..maxY) {
            append(y.toString().padStart(2, '0'))
            append(' ')
            for (x in 0..maxX) {
                val vector = Vector(x, y)

                if (path.contains(vector)) {
                    append('x')
                } else {
                    append(
                        when(map[vector]) {
                            true -> '#'
                            false -> '.'
                            else -> '?'
                        }
                    )
                }
            }
            append('\n')
        }
    }.println()
}

private fun part2(n: Int): Int {
    val map = mutableMapOf<Vector, Boolean>()

    magicNumber = n

    // DFS
    val queue = PriorityQueue<Pair<Vector, List<Vector>>>(compareBy { it.second.size })
    queue.offer(Pair(Vector(1, 1), listOf(Vector(1, 1))))

    val visited = mutableSetOf(queue.peek().first)

    while (queue.isNotEmpty()) {
        val (location, path) = queue.poll()

        if (path.size > 51) {
            return visited.size
        }

        visited.add(location)

        listOf(
            Vector(-1, 0),
            Vector(0, -1),
            Vector(1, 0),
            Vector(0, 1),
        ).map { location + it }
            .filter { it.x >= 0 && it.y >= 0 }
            .map { candidate ->
                candidate to map.getOrPut(candidate) {
                    candidate.isWall()
                }
            }
            .forEach { (loc, isWall) ->
                if (!visited.contains(loc) && !isWall) {
                    queue.offer(loc to (path + loc))
                }
            }
    }

    return -1
}

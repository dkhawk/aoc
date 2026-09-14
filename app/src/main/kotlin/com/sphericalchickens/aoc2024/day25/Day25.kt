package com.sphericalchickens.aoc2024.day25

import com.sphericalchickens.utils.*




val testInput = """
    #####
    .####
    .####
    .####
    .#.#.
    .#...
    .....

    #####
    ##.##
    .#.##
    ...##
    ...#.
    ...#.
    .....

    .....
    #....
    #....
    #...#
    #.#.#
    #.###
    #####

    .....
    .....
    #.#..
    ###..
    ###.#
    ###.#
    #####

    .....
    .....
    .....
    #....
    #.#..
    #.#.#
    #####
""".trimIndent()

fun main() {
    check(part1(testInput) == 3)
//    check(part2(testInput) == 0)

    val input = readText("inputs/25")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
}

private fun part1(input: String): Int {
    // locks have the top row full and the bottom row empty
    // keys have the top row empty and the bottom row full

    val (locks, keys) = input.split("\n\n").partition { it.first() == '#' }

    val lockHeights = locks.map { it.toHeights() }
    val keyHeights = keys.map { it.toLows() }

    var count = 0

    lockHeights.forEach { lh ->
        keyHeights.forEach { kh ->
            if (kh.fits(lh)) {
                count += 1
            }
        }
    }

    count.println()

    return count
}

const val height = 6

private fun List<Int>.fits(lh: List<Int>): Boolean {
    return this.zip(lh).all { (k, l) ->
        k + l < height
    }
}

fun String.toHeights(): List<Int> {
    val grid = buildGrid(this.lines().filter { it.isNotBlank() }.also {
        it.size.println()
        require(it.size == 7) { "Expected height of 7.  $it***" }
    })

    val y = grid.keys.groupBy { it.x }.entries.map { (column, items) -> items.maxBy { it.y } }.map { it.y }
    return  y
}

fun String.toLows(): List<Int> {
    val grid = buildGridNoFilter(this.lines().filter { it.isNotBlank() }.also { require(it.size == 7) })

    val bounds = grid.getBounds()

    val heights = mutableMapOf<Int, Int>()

    grid.entries.forEach { (location, value) ->
        if (value == '#') {
            heights[location.x] = kotlin.math.min(location.y, heights.getOrDefault(location.x, Int.MAX_VALUE))
        }
    }

    return heights.toSortedMap().values.toList().map { bounds.max.y - it }
}

private fun part2(input: List<String>): Int {
    return -1
}
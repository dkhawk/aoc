package com.sphericalchickens.aoc2024.day04.b

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.Vector
import com.sphericalchickens.utils.allHeadings8
import com.sphericalchickens.utils.mapToLocations
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readText
import kotlin.math.sign
import kotlin.time.measureTime

val input = """
    MMMSXXMASM
    MSAMXMSMSA
    AMXSXMAAMM
    MSAMASMSMX
    XMASAMXAMM
    XXAMMXXAMA
    SMSMSASXSS
    SAXAMASAAA
    MAMMMXMMMM
    MXMXAXMASX
""".trimIndent().lines()

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(input) == 18)
    check(part2(input) == 9)

    // Read the input from the `src/Day01.txt` file.
    val input = readText("inputs/04").lines()

    measureTime {
        part1(input).println()
        part2(input).println()
    }.println()
}


fun createKernels(): List<List<Pair<Vector, Char>>> {
    val word = "XMAS"

    val kernels = allHeadings8().map { heading ->
        val s = generateSequence(Vector()) { it + heading }.iterator()
        word.map { s.next() to it }
    }

//    kernels.forEach {
//        it.toMap().printGrid()
//        println()
//    }

    return kernels
}

val part1Kernels = createKernels()
fun part1(input: List<String>): Int {

    return mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'X' }.sumOf { (location, _) ->
            part1Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}


fun createKernels2(): List<List<Pair<Vector, Char>>> {
    val k = (-1..1).map{ Vector(it, it) }.zip("MAS".toList()).let {
        it + it.mirrorHorizontally()
    }

    val kernels = generateSequence(k) {
        it.rotate()
    }.take(4).toList()

//    kernels.forEach {
//        it.toMap().printGrid()
//        println()
//    }

    return kernels
}

private fun <T> List<Pair<Vector, T>>.pivot(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        Vector(location.y, location.x) to t
    }
}

private fun <T> List<Pair<Vector, T>>.mirrorHorizontally(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        Vector(-location.x, location.y) to t
    }
}

private fun <T> List<Pair<Vector, T>>.rotate(): List<Pair<Vector, T>> {
    return this.map { (location, t) ->
        val (x, y) = location
        val v = when {
            x.sign == -1 && y.sign == -1 -> Vector(-x, y)
            x.sign == 1 && y.sign == -1 -> Vector(x, -y)
            x.sign == 1 && y.sign == 1 -> Vector(-x, y)
            x.sign == -1 && y.sign == 1 -> Vector(x, -y)
            else -> location
        }

        v to t
    }
}

val part2Kernels = createKernels2()

fun part2(input: List<String>): Int {
    return mapToLocations(input).toMap().let { grid ->
        grid.entries.filter { it.value == 'A' }.sumOf { (location, _) ->
            part2Kernels.count { kernel ->
                checkKernelAtLocation(grid, location, kernel)
            }
        }
    }
}

fun checkKernelAtLocation(grid: Map<Vector, Char>, location: Vector, kernel: List<Pair<Vector, Char>>): Boolean {
    return kernel.all { (kernelLocation, expected) ->
        grid[kernelLocation + location] == expected
    }
}

package com.sphericalchickens.aoc2025.day12

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputText
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = false
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 12 ---")

    val input = readInputText("aoc2025/day12_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
            part1(input)
        }
        println("   Part 1: $part1Result")
        println("Part 1 runtime: ${formatDuration(part1Duration)}")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            part2(input)
        }
        println("   Part 2: $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}")
    }
}

private fun runPart1Tests() {
    val testInput = """
        0:
        ###
        ##.
        ##.

        1:
        ###
        ##.
        .##

        2:
        .##
        ###
        ##.

        3:
        ##.
        ###
        ##.

        4:
        ###
        #..
        ###

        5:
        ###
        .#.
        ###

        4x4:  0 0 0 0 2 0
        12x5: 1 0 1 0 2 2
        12x5: 1 0 1 0 3 2
    """.trimIndent()

    val t1 = """
        123
        456
        789
    """.trimIndent().toArea()

    val r1 = """
        741
        852
        963
    """.trimIndent().toArea()

    val f1 = """
        321
        654
        987
    """.trimIndent().toArea()

    val f2 = """
        789
        456
        123
    """.trimIndent().toArea()

    check("rotation", r1.toString(), t1.rotate().toString())

    check("flipped", f1.toString(), t1.flipAlongY().toString())

    check("flipped", f2.toString(), t1.flipAlongX().toString())

    val input = readInputText("aoc2025/day12_input.txt")

    val parts = input.split("\n\n")
    val presents = parts.dropLast(1).map { it.toPresent() }
    presents.sumOf { it.orientations.size }.println()

//    presents.first().orientations.joinToString("\n\n").println()
    presents[4].orientations.joinToString("\n\n").println()

    val regions = parts.last().lines().filter{ it.isNotBlank() }.map { it.toRegion() }
    regions.first().println()
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private fun part1(input: String): Int {
    val parts = input.split("\n\n")

    val presents = parts.dropLast(1).map { it.toPresent() }
    val regions = parts.last().lines().filter{ it.isNotBlank() }.map { it.toRegion() }

    presents.first().orientations

    regions.take(1).count { region -> hasSolution(region, presents) }

    return -1
}

private class Area(val width: Int, val height: Int) {
    val grid: CharArray = CharArray(height * width) { ' ' }

    operator fun get(x: Int, y: Int) = grid[y * width + x]
    operator fun set(x: Int, y: Int, value: Char) { grid[y * width + x] = value }

    override fun toString(): String {
        return buildString {
            (0 until height).forEach { y ->
                grid.slice((y * width) ..< ((y + 1) * width)).forEach { append(it) }
                append('\n')
            }
        }.dropLast(1)
    }

    fun setRange(start: Int, line: String) {
        line.toCharArray().copyInto(grid, start)
    }

    // I'm going to programmer hell for this...

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Area) return false

        if (width != other.width) return false
        if (height != other.height) return false
        // specific check for array content
        if (!grid.contentEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        // specific hash for array content
        result = 31 * result + grid.contentHashCode()
        return result
    }
}

private fun String.toArea() : Area {
    val lines = lines()
    val h = lines.size
    val w = lines[0].length

    return Area(width = h, height = w).apply {
        lines.forEachIndexed { y, line ->
            setRange(y * w, line)
        }
    }
}

private fun hasSolution(
    region: Region,
    shapes: List<Present>
): Boolean {
    val presents = buildList {
        region.shapes.mapIndexed { idx, count ->
            repeat(count) { add(shapes[idx]) }
        }
    }

    val regionArea = Area(
        width = region.height, height = region.width
    )

    regionArea.println()

    return false
}

private data class Present(
    val index: Int,
    val shape: String
) {
    val orientations = orientations()

    private fun orientations(): Set<Area> {
        val a = shape.toArea()

        return buildSet {
            add(a)
            a.rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
            a.flipAlongY().rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
            a.flipAlongX().rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
        }
    }
}

private fun Area.rotate(): Area {
    val h = this.width
    val result = Area(this.height, this.width)

    for (y in 0 until height) {
        for (x in 0 until width) {
            result[((h - 1) - y), x] = this[x, y]
        }
    }

    return result
}

private fun Area.flipAlongY(): Area {
    val result = Area(this.height, this.width)

    for (y in 0 until height) {
        for (x in 0 until width) {
            result[(this.width - 1) - x, y] = this[x, y]
        }
    }

    return result
}

private fun Area.flipAlongX(): Area {
    val result = Area(this.height, this.width)

    for (y in 0 until height) {
        for (x in 0 until width) {
            result[x, (this.height - 1) - y] = this[x, y]
        }
    }

    return result
}

private fun String.toPresent(): Present {
    val lines = lines()
    val index = lines.first().dropLast(1).trim().toInt()
    val shape = this.substringAfter("\n")

    return Present(
        index, shape
    )
}

private fun String.toRegion(): Region {
    val (sizeString, shapesString) = split(":")

    val (w, l) = sizeString.split("x").map { it.toInt() }
    val shapes = shapesString.trim().split(" ").map { it.toInt() }

    return Region(w, l, shapes)
}

private data class Region(val width: Int, val height: Int, val shapes: List<Int>)

private fun part2(input: String): Int {
    return -1
}


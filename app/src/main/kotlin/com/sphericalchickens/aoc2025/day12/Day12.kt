package com.sphericalchickens.aoc2025.day12

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputText
import kotlin.collections.count
import kotlin.collections.forEach
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
    """.trimIndent().toRectangle()

    val r1 = """
        741
        852
        963
    """.trimIndent().toRectangle()

    val f1 = """
        321
        654
        987
    """.trimIndent().toRectangle()

    val f2 = """
        789
        456
        123
    """.trimIndent().toRectangle()

    check("rotation", r1, t1.rotate())
    check("flipped", f1, t1.flipAlongY())
    check("flipped", f2, t1.flipAlongX())

//    val input = readInputText("aoc2025/day12_input.txt")
//
    val (presents, regions) = parseInput(testInput)

    check("orientations", 28, presents.sumOf { it.orientations.size })
    check("has solution #1", true, hasSolution(regions[0], presents))
//    check("Part 1 Test Case 1", 2, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent()
    check("Part 2 Test Case 1", -1, part2(testInput))
}

private fun part1(input: String): Int {
    val (presents, regions) = parseInput(input)

    return regions.count { region -> hasSolution(region, presents) }
}

private fun parseInput(input: String): Pair<List<Present>, List<Region>> {
    val parts = input.split("\n\n")
    val presents = parts.dropLast(1).map { it.toPresent() }
    val regions = parts.last().lines().filter { it.isNotBlank() }.map { it.toRegion() }
    return Pair(presents, regions)
}

private data class Rectangle(val width: Int, val height: Int, val grid: String) {
    constructor(width: Int, height: Int) : this(width, height, ' '.toString().repeat(width * height))

    val area = grid.length

    val holeCount by lazy { grid.count { it != '#' } }
    val blockCount by lazy { area - holeCount }

    operator fun get(x: Int, y: Int) = grid[y * width + x]

    fun description() = "width = $width, height = $height, area = ${width * height}"

    override fun toString(): String {
        return buildString {
            append(description())
            append('\n')
            (0 until height).forEach { y ->
                grid.slice((y * width) ..< ((y + 1) * width)).forEach { append(it) }
                append('\n')
            }
        }.dropLast(1)
    }
}

private fun String.toRectangle() : Rectangle {
    val lines = lines()
    val h = lines.size
    val w = lines[0].length

    return Rectangle(
        width = w,
        height = h,
        grid = lines.joinToString(separator = "")
    )
}

private class Hopper(
    var count: Int,
    val shape: List<Present>
)

private fun hasSolution(
    region: Region,
    presents: List<Present>,
    progress: CharArray? = null,
): Boolean {
    val area = progress ?: CharArray(region.width * region.height) { '.' }
    val hoppers = region.shapeCounts.toIntArray()

    val presentX = 3
    val presentY = 3

    fun get(x: Int, y: Int) = area[y * region.width + x]
    fun set(x: Int, y: Int, ch: Char) {
        area[y * region.width + x] = ch
    }

    // Filled space => '#'
    // Usable space => '.'
    // Unusable space => 'x'

    // simple rejection
    val needed = presents.mapIndexed { index, present -> present.shape.blockCount * region.shapeCounts[index] }.sum()
    val holes = area.count { it == '.' }

    if (needed >= holes) {
        // One always hope....
        "Trivial rejection of $region".println()
        return false
    }

    // Let's try tiling...
    // Find the next open spot

    while (true) {
        if (hoppers.none { it > 0 }) {
            return true
        }

        val nextFreeSpace = area.indexOf('.')

        if (nextFreeSpace == -1) {
            return false
        }

        val y = nextFreeSpace / region.width
        val x = nextFreeSpace % region.width

        // See if there is even the possibility of placing a block there
        if ((region.height - y < 3) || (region.width - x < 3)) {
            set(x, y, 'x')
            continue
        }

        // Generate a sequence of possible next blocks
        val nextBlockIndexAndOrientationSequence = sequence {
            hoppers.forEachIndexed { index, count ->
                if (count > 0) {
                    val orientations = presents[index].orientations
                    orientations.forEach { orientation ->
                        yield(index to orientation)
                    }
                }
            }
        }

        // Do any fit?
        val candidates = nextBlockIndexAndOrientationSequence.filter { (index, block) ->
            var fits = true
            val yIter = (0 until presentY).iterator()

            while (fits && yIter.hasNext()) {
                val y0 = yIter.next()
                val xIter = (0 until presentX).iterator()
                while (fits && xIter.hasNext()) {
                    val x0 = xIter.next()
                    if (block[x0, y0] == '#' && get(x0 + x, y0 + y) == '#') {
                        fits = false
                    }
                }
            }

            fits
        }.toList()

        if (candidates.isEmpty()) {
            set(x, y, 'x')
            continue
        }

        // Try each candidate in succession
        candidates.firstOrNull { candidate ->
            val progress = CharArray(area.size)
            area.copyInto(progress)

            fun set(x: Int, y: Int, ch: Char) {
                progress[y * region.width + x] = ch
            }

            val yIter = (0 until presentY).iterator()

            while (yIter.hasNext()) {
                val y0 = yIter.next()
                val xIter = (0 until presentX).iterator()
                while (xIter.hasNext()) {
                    val x0 = xIter.next()
                    if (candidate.second[x0, y0] == '#') set(x0 + x, y0 + y, '#')
                }
            }

            val newShapeCounts = region.shapeCounts.toIntArray()
            newShapeCounts[candidate.first] -= 1

            val newRegion = region.copy(
               shapeCounts = newShapeCounts.toList()
            )

            hasSolution(
                region = newRegion,
                presents = presents,
                progress = progress,
            )
        }

        return false
    }
}

private data class Present(
    val index: Int,
    val shape: Rectangle
) {
    val orientations = orientations()

    private fun orientations(): Set<Rectangle> {
        val rectangle = shape

        return buildSet {
            add(rectangle)
            rectangle.rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
            rectangle.flipAlongY().rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
            rectangle.flipAlongX().rotate().also { add(it) }.rotate().also { add(it) }.rotate().also { add(it) }
        }
    }
}

private fun Rectangle.rotate(): Rectangle {
    val h = this.width
    val ca = CharArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val xx = ((h - 1) - y)
            val yy = x
            val index = yy * this.height + xx
            ca[index] = this[x, y]
        }
    }

    return Rectangle(width = this.height, height = this.width, grid = ca.concatToString())
}

private fun Rectangle.flipAlongY(): Rectangle {
    val ca = CharArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            ca[y * this.width + ((height - 1) - x)] = this[x, y]
        }
    }

    return Rectangle(width = this.width, height = this.height, grid = ca.concatToString())
}

private fun Rectangle.flipAlongX(): Rectangle {
    val ca = CharArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            ca[((this.height - 1) - y) * this.width + x] = this[x, y]
        }
    }

    return Rectangle(width = this.width, height = this.height, grid = ca.concatToString())
}

private fun String.toPresent(): Present {
    val lines = lines()
    val index = lines.first().dropLast(1).trim().toInt()
    val shape = this.substringAfter("\n")

    return Present(
        index, shape.toRectangle()
    )
}

private fun String.toRegion(): Region {
    val (sizeString, shapesString) = split(":")

    val (w, l) = sizeString.split("x").map { it.toInt() }
    val shapes = shapesString.trim().split(" ").map { it.toInt() }

    return Region(w, l, shapes)
}

private data class Region(val width: Int, val height: Int, val shapeCounts: List<Int>) {
}

private fun part2(input: String): Int {
    return -1
}


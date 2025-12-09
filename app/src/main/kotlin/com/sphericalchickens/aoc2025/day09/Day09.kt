package com.sphericalchickens.aoc2025.day09

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 9 ---")

    val input = readInputLines("aoc2025/day09_input.txt")

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

val testInput = """
    7,1
    11,1
    11,7
    9,7
    9,5
    2,5
    2,3
    7,3
""".trimIndent().lines()

private fun runPart1Tests() {
    check("Part 1 Test Case 1", 50, part1(testInput))
}

private fun runPart2Tests() {
    check("Part 2 Test Case 1", 24, part2(testInput))
}

private fun part1(input: List<String>): Long {
    val corners = parseCorners(input)

    return findMaxOf(corners)
}

private fun parseCorners(input: List<String>): List<Vector> =
    input.map { it.split(",").map { it.trim().toInt() } }.map { (a, b) -> Vector(a, b) }

private fun findMaxOf(corners: List<Vector>): Long {
    if (corners.size < 2) return 0L

    val first = corners.first()
    val rest = corners.drop(1)

    val myMax = rest.maxOfOrNull { area(first, it) } ?: 0L

    val othersMax = findMaxOf(rest)

    return max(myMax, othersMax)
}

private fun area(a: Vector, b: Vector): Long {
    return a.delta(b).let { (dx, dy) ->
        dx.toLong() * dy.toLong()
    }
}

private fun Vector.delta(b: Vector): Pair<Int, Int> {
    return abs(x - b.x) + 1 to abs(y - b.y) + 1
}

private data class Vector(val x: Int, val y: Int)

private fun part2(input: List<String>): Long {
    val corners = parseCorners(input)

    val (compressedMap, decoderMap) = createCompressedMap(corners)

    val invalidTiles = findInvalidTiles(compressedMap)

    val rectangles = createRectangles(corners).sortedByDescending { it.area }

    val largestValidRectangle = rectangles.first { rectangle ->
        val c1 = decoderMap.getValue(rectangle.c1)
        val c2 = decoderMap.getValue(rectangle.c2)

        Rectangle(c1, c2).tiles().none { v ->
            v in invalidTiles
        }
    }

    return largestValidRectangle.area
}

private fun createCompressedMap(
    corners: List<Vector>,
): Pair<MutableSet<Vector>, MutableMap<Vector, Vector>> {
    // Compress the map to make it possible to use flood fill
    // We create two maps:
    //  * the compressed map has all redundant rows and columns removed
    //  * the decoder map maps a full coordinate to its compressed equivalent

    // Start by rendering all edge tiles
    val edges = drawEdges(corners)

    val minX = corners.minBy { it.x }.x
    val maxX = corners.maxBy { it.x }.x

    val minY = corners.minBy { it.y }.y
    val maxY = corners.maxBy { it.y }.y

    // The x and y coordinates of all the corners
    val xCorners = corners.map { it.x }.toSet()
    val yCorners = corners.map { it.y }.toSet()

    val compressedMap = mutableSetOf<Vector>()
    val decoderMap = mutableMapOf<Vector, Vector>()

    var rowNumber = -1
    (minY..maxY).forEach { y ->
        if (y in yCorners) {
            // This is the rowNumber of the compressed row
            rowNumber += 1
            var colNumber = -1
            (minX..maxX).forEach { x ->
                if (x in xCorners) {
                    // This is the colNumber of the compressed col
                    colNumber += 1
                    val full = Vector(x, y)
                    if (full in edges) {
                        val compressed = Vector(colNumber, rowNumber)
                        compressedMap.add(compressed)
                        decoderMap[full] = compressed
                    }
                }
            }
        }
    }
    return Pair(compressedMap, decoderMap)
}

private fun drawEdges(corners: List<Vector>): Set<Vector> {
    return buildSet {
        corners.zipWithNextAndWrap().forEach { (a, b) ->
            if (a.x != b.x) {
                check(a.y == b.y)
                val x0 = min(a.x, b.x)
                val x1 = max(a.x, b.x)
                val y = a.y
                ((x0)..(x1)).forEach { add(Vector(it, y)) }
            } else {
                check(a.x == b.x)
                val y0 = min(a.y, b.y)
                val y1 = max(a.y, b.y)
                val x = a.x
                ((y0)..(y1)).forEach { add(Vector(x, it)) }
            }
        }
    }
}

private fun findInvalidTiles(compressedMap: MutableSet<Vector>): MutableSet<Vector> {
    // Flood fill -- we know that the minimum coordinate is outside the shape (since we subtracted one from the mins)
    // We also know that we can completely surround the shape since we added one to the maxes

    val x0 = compressedMap.minBy { it.x }.x - 1
    val x1 = compressedMap.maxBy { it.x }.x + 1

    val y0 = compressedMap.minBy { it.y }.y - 1
    val y1 = compressedMap.maxBy { it.y }.y + 1

    val start = Vector(x0, y0)
    val queue = ArrayDeque<Vector>()
    queue.add(start)

    val validX = x0..x1
    val validY = y0..y1

    // After the flood fill runs, visited will be all tiles outside the shape
    val visited = mutableSetOf(start)

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()

        ((next.y - 1)..(next.y + 1)).forEach { y ->
            ((next.x - 1)..(next.x + 1)).forEach { x ->
                val v = Vector(x, y)
                if (v != next) {
                    if (v !in compressedMap && v !in visited && v.x in validX && v.y in validY) {
                        visited.add(v)
                        queue.addLast(v)
                    }
                }
            }
        }
    }
    return visited
}

private data class Rectangle(val c1: Vector, val c2: Vector) {
    val area = area(c1, c2)

    val minX = min(c1.x, c2.x)
    val maxX = max(c1.x, c2.x)

    val minY = min(c1.y, c2.y)
    val maxY = max(c1.y, c2.y)

    val internalXRange = (minX + 1)..(maxX - 1)
    val internalYRange = (minY + 1)..(maxY - 1)

    fun contains(corner: Vector): Boolean {
        return corner.x in internalXRange && corner.y in internalYRange
    }

    fun tiles() = sequence {
        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                yield(Vector(x, y))
            }
        }
    }
}

private fun createRectangles(corners: List<Vector>) : List<Rectangle> {
    if (corners.size < 2) return emptyList()

    val first = corners.first()

    val rest = corners.drop(1)

    return rest.map { Rectangle(first, it) } + createRectangles(rest)
}

fun <T> List<T>.zipWithNextAndWrap(): Sequence<Pair<T, T>> = sequence {
    if (isEmpty()) return@sequence // Handle empty list case

    // Yield all adjacent pairs using zipWithNext
    this@zipWithNextAndWrap.zipWithNext().forEach { pair ->
        yield(pair)
    }

    // Add the wrap-around pair (last with first)
    if (size > 1) { // Only add if there's more than one element
        yield(Pair(last(), first()))
    }
}
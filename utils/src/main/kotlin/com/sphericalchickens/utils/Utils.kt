package com.sphericalchickens.utils


import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

fun readText(name: String): String {
    val localFile = java.io.File("src/$name.txt")
    if (localFile.exists()) return localFile.readText()
    if (name.startsWith("inputs/")) {
        val num = name.removePrefix("inputs/").padStart(2, '0')
        return readInputText("aoc2024/day${num}_input.txt")
    }
    return Path("src/$name.txt").readText()
}

/**
 * Reads lines from the given input txt file.
 */
fun readLines(name: String) = readText(name).trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> Iterable<T>.printAsLines() = this.joinToString("\n").println()

fun <K, V> Map<K, V>.printAsLines() {
    this.entries.printAsLines()
}





fun allHeadings8(): List<Vector> {
    return Heading8.entries.map { heading -> heading.vector }
}

fun mapToLocations(input: List<String>): List<Pair<Vector, Char>> {
    return input.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Vector(x, y) to c
        }
    }.flatten()
}

data class Bounds(
    val min: Vector,
    val max: Vector,
) {
    fun contains(vector: Vector): Boolean {
        return vector.x in min.x..(max.x) && vector.y in min.y..(max.y)
    }

    /*
        bounds.println()
    bounds.contains(bounds.min).println()
    bounds.contains(bounds.max).println()
    bounds.contains(bounds.min + Heading.NORTH).println()
    bounds.contains(bounds.min + Heading.WEST).println()
    bounds.contains(bounds.max + Heading.EAST).println()
    bounds.contains(bounds.max + Heading.SOUTH).println()
     */
}

fun Collection<Vector>.minMax(): Bounds {
    val xMinMax = this.map { it.x }.sorted().let { l -> l.first() to l.last() }
    val yMinMax = this.map { it.y }.sorted().let { l -> l.first() to l.last() }

    return Bounds(
        Vector(xMinMax.first, yMinMax.first),
        Vector(xMinMax.second, yMinMax.second)
    )
}

fun Map<Vector, Char>.getBounds(): Bounds {
    return keys.minMax()
}

fun Map<Vector, Char>.printGrid(
    bounds: Bounds? = null,
    default: Char = '.',
    formatter: (Vector, Char) -> String = { _, c -> "$c" }
) {
    val (minLocation, maxLocation) = bounds ?: keys.minMax()

    val gw = this.withDefault { default }

    (minLocation..maxLocation).mapIndexed { index, row ->
        (String.format("%4d ", index) + row.joinToString("") {
            formatter(it, gw.getValue(it))
        }).println()
    }
}

fun Heading.toSymbol(): Char {
    return when (this) {
        Heading.NORTH -> '^'
        Heading.EAST -> '>'
        Heading.SOUTH -> 'v'
        Heading.WEST -> '<'
    }
}

fun buildGrid(input: List<String>): Map<Vector, Char> {
    return buildMap {
        input.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c != '.') put(Vector(x, y), c)
            }
        }.flatten()
    }
}

fun List<String>.toGrid() = buildGrid(this)

fun buildGridNoFilter(input: List<String>): Map<Vector, Char> {
    return buildMap {
        input.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                put(Vector(x, y), c)
            }
        }.flatten()
    }
}
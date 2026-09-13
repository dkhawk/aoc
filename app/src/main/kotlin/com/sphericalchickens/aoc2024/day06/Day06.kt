package com.sphericalchickens.aoc2024.day06

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*


val testInput = """
    ....#.....
    .........#
    ..........
    ..#.......
    .......#..
    ..........
    .#..^.....
    ........#.
    #.........
    ......#...""".trimIndent()

fun main() = runBlocking {
    val input = readLines("inputs/06")

    check(part1(testInput.lines()) == 41)
    part1(input).println()

    launch(Dispatchers.Default) {
        check(part2(testInput.lines(), this) == 6)
        part2(input, this).println()
    }

    "${COLORS.RED}Reminder: 785 is too low!${COLORS.NONE}".println()
    "${COLORS.RED}Reminder: 1502 is too high!${COLORS.NONE}".println()
}

fun part1(input: List<String>): Int {
    val grid = buildGrid(input).apply {
        // printGrid()
    }.withDefault { '.' }

    val bounds = grid.keys.minMax()

    var guard = grid.entries.first { (_, v) -> v == '^' }.key
    var heading = Heading.NORTH

    val visited = mutableSetOf<Vector>()

    while (bounds.contains(guard)) {
        visited.add(guard)
        val next = guard.advance(heading)
        if (!bounds.contains(next)) {
            break
        }
        if (grid.getValue(next) == '#') {
            heading = heading.turnRight()
        } else {
            guard = next
        }
    }

    return visited.size
}

data class CandidateObject(
    val location: Vector,
    val guard: Vector,
    val heading: Heading,
)

suspend fun part2(input: List<String>, coroutineScope: CoroutineScope): Int {
    val grid = buildGrid(input).apply {
        // printGrid()
    }.withDefault { '.' }

    val bounds = grid.keys.minMax()

    var guard = grid.entries.first { (_, v) -> v == '^' }.key
    val start = guard
    var heading = Heading.NORTH

    val visited = mutableSetOf<Vector>()

//    val expectedObstacles = getExpectedObstacles(testData)
//    expectedObstacles.println()

//    expectedObstacles.forEach { expected ->
//        Heading.entries.forEach { heading ->
//
//            val guard = expected.advance(heading.turnLeft())
//
//            checkCandidate(
//                grid,
//                bounds,
//                CandidateObject(
//                    guard = guard,
//                    heading = Heading.NORTH,
//                    location = expected
//                )
//            ).println()
//        }
//    }

    // These are all candidate locations where we _could_ put an obstacle
    val candidates = mutableSetOf<CandidateObject>()

    while (bounds.contains(guard)) {
        visited.add(guard)
        val next = guard.advance(heading)
        if (!bounds.contains(next)) {
            break
        }
        if (grid.getValue(next) == '#') {
            heading = heading.turnRight()
        } else {
            candidates.add(
                CandidateObject(
                    location = next,
                    guard = start,
                    heading = Heading.NORTH,
                )
            )
            guard = next
        }
    }

    val deferreds = candidates.map {
        coroutineScope.async {
            checkCandidate(grid, bounds, it)
        }
    }

    val actual = deferreds.awaitAll().filterNotNull().toSet()

//    "Actual: $actual".println()
//    "Missing: ${expectedObstacles - actual}".println()
//    "Extra: ${actual - expectedObstacles}".println()

    val result = grid.toMutableMap()
    visited.forEach {
        result[it] = 'X'
    }

//    result.printGrid()
//
//    grid.printGrid { location, c ->
//        if (location in actual) {
//            "${COLORS.GREEN}O${COLORS.NONE}"
//        } else {
//            "$c"
//        }
//    }

    return actual.size
}

fun checkCandidate(grid: Map<Vector, Char>, bounds: Bounds, candidate: CandidateObject) : Vector? {
    var heading = candidate.heading
    var guard = candidate.guard

    val visited = mutableSetOf<Pair<Vector, Heading>>()
    visited.add(guard to heading)

    var result: Vector?

    while (true) {
        val next = guard.advance(heading)

        if (!bounds.contains(next)) {
            result = null
            break
        }

        if (grid.getValue(next) == '#' || next == candidate.location) {
            heading = heading.turnRight()
        } else {
            guard = next
            if (visited.contains(guard to heading)) {
                return candidate.location
            }
            visited.add(guard to heading)
        }
    }

//    grid.printGrid { vector, c ->
//        if (candidate.location == vector) {
//            "${COLORS.GREEN}O${COLORS.NONE}"
//        } else if (visited.contains(vector)) {
//            "${COLORS.RED}+${COLORS.NONE}"
//        } else {
//            c.toString()
//        }
//    }

    return result
}

fun getExpectedObstacles(testData: String): Set<Vector> {
    return testData.split("\n\n").map { testGrid ->
        buildGrid(testGrid.lines()).entries.first { (_, v) -> v == 'O' }.key
    }.also { it.println() }.toSet()
}

val testData = """
    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ....|..#|.
    ....|...|.
    .#.O^---+.
    ........#.
    #.........
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ......O.#.
    #.........
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    .+----+O#.
    #+----+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ..|...|.#.
    #O+---+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    ....|.|.#.
    #..O+-+...
    ......#...

    ....#.....
    ....+---+#
    ....|...|.
    ..#.|...|.
    ..+-+-+#|.
    ..|.|.|.|.
    .#+-^-+-+.
    .+----++#.
    #+----++..
    ......#O..
""".trimIndent()
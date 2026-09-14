package com.sphericalchickens.aoc2024.day15

import com.sphericalchickens.utils.*



import kotlin.math.abs

val testInput1 = """
    ########
    #..O.O.#
    ##@.O..#
    #...O..#
    #.#.O..#
    #...O..#
    #......#
    ########

    <^^>>>vv<v>>v<<
""".trimIndent().lines()

val testInput2 = """
    ##########
    #..O..O.O#
    #......O.#
    #.OO..O.O#
    #..O@..O.#
    #O#..O...#
    #O..O..O.#
    #.OO.O.OO#
    #....O...#
    ##########

    <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
    vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
    ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
    <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
    ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
    ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
    >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
    <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
    ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
    v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
""".trimIndent().lines()

val testInput3 = """
    #######
    #...#.#
    #.....#
    #..OO@#
    #..O..#
    #.....#
    #######

    <vv<<^^<<^^
""".trimIndent().lines()

fun main() {
    check(part1(testInput1) == 2028)
    check(part1(testInput2) == 10092)

    val input = readLines("inputs/15")
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")

    check(part2(testInput3) == listOf(105, (7 + 100 * 2), (6 + 100 * 3)).sum())
    check(part2(testInput2) == 9021)

    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

private fun part1(input: List<String>): Int {
    val grid = createGrid(input)
    val moves = input.toMoves()
    var currentLocation = grid.entries.first { it.value == '@' }.key

    moves.forEach { heading ->
        currentLocation = moveRobot(grid, currentLocation, heading)
    }
    return grid.score()
}

private fun List<String>.toMoves() = getMoves(this)

private fun getMoves(input: List<String>) =
    input.dropWhile { it.isNotBlank() }.flatMap { line ->
        line.trim().mapNotNull {
            when (it) {
                '^' -> Heading.NORTH
                'v' -> Heading.SOUTH
                '<' -> Heading.WEST
                '>' -> Heading.EAST
                else -> null
            }
        }
    }.asSequence()

private fun createGrid(input: List<String>) =
    input.takeWhile { it.isNotBlank() }.toGrid().toMutableMap()

private fun Map<Vector, Char>.score(): Int {
    return entries.sumOf { (location, char) ->
        if (char == 'O' || char == '[') {
            location.y * 100 + location.x
        } else {
            0
        }
    }
}

private fun moveRobot(grid: MutableMap<Vector, Char>, location: Vector, heading: Heading): Vector {
    val goal = location.advance(heading)

    if (grid[goal] == '#') {
        // Move failed!!
        return location
    }

    // Boxes in the way.  Move the boxes (boxes are fungible so just create a box in the first free space found)
    var nextFreeSpace = goal
    if (grid[nextFreeSpace] == 'O') {
        while (grid[nextFreeSpace] == 'O') {
            nextFreeSpace = nextFreeSpace.advance(heading)
        }

        if (grid[nextFreeSpace] == '#') {
            // Move failed!!
            return location
        }

        grid[nextFreeSpace] = 'O'
    }

    grid[goal] = '@'
    grid[location] = '.'
    return goal
}

private fun part2(input: List<String>): Int {
    val grid = createGrid(input).transformForPart2().toMutableMap()
    val moves = input.toMoves()
    var currentLocation = grid.entries.first { it.value == '@' }.key

    moves.forEach { heading ->
        currentLocation = moveRobot2(grid, currentLocation, heading)
    }

    // Calculate score
    return grid.score()
}

fun moveRobot2(grid: MutableMap<Vector, Char>, location: Vector, heading: Heading): Vector {
    val goal = location.advance(heading)

    if (grid[goal] == '#') {
        // Move failed!!
        return location
    }

    if (grid[goal] == null) {
        // Move succeeded!!
        grid[goal] = '@'
        grid.remove(location)
        return goal
    }

    // Easier to treat east/west separately -- boxes are only two spaces wide in the east/west direction
    if (heading == Heading.EAST || heading == Heading.WEST) {
        return moveBoxEastWest(grid, location, heading)
    }

    return moveBoxNorthSouth(grid, location, heading)
}

fun moveBoxNorthSouth(grid: MutableMap<Vector, Char>, location: Vector, heading: Heading): Vector {
    // The list of objects to move at the end of it all
    val locationsToMove = mutableMapOf(location to '@')

    // All items in the foremost locations that need to be checked (initially just the robot)
    var frontLine = listOf(location)

    while (true) {
        // For each location in the frontLine, check if it can advance

        // If there is a wall for any of the frontLine next locations, then the move fails
        if (frontLine.any { grid[it.advance(heading)] == '#' }) {
            // Move failed
            return location
        }

        // No more boxes (or walls) in the way.  Proceed with the move.
        if (frontLine.all { grid[it.advance(heading)] == null }) {
            // Move succeeded!

            // Remove all old locations
            grid.removeAll(locationsToMove.keys)

            // Add back the boxes at the new locations
            grid.putAll(locationsToMove.map {(k,v) -> k.advance(heading) to v})

            return location.advance(heading)
        }

        // Build a new frontline from the boxes
        frontLine = buildList {
            // For each box encountered, replace the current block in the frontline with both sides of the next block
            frontLine.forEach { loc ->
                val next = loc.advance(heading)
                val c = grid[next]

                // We should have already eliminated this possibility...
                check(c != '#')

                when (c) {
                    '[' -> {
                        add(next)
                        add(next.advance(Heading.EAST))
                    }
                    ']' -> {
                        add(next)
                        add(next.advance(Heading.WEST))
                    }
                }
            }
        }

        locationsToMove.putAll(frontLine.map { it to grid.getValue(it) })
    }
}

fun moveBoxEastWest(grid: MutableMap<Vector, Char>, location: Vector, heading: Heading): Vector {
//    "Moving East/West".println()
    check(heading == Heading.EAST || heading == Heading.WEST)

    var freeSpace = location.advance(heading)
    while (grid[freeSpace] == '[' || grid[freeSpace] == ']') {
        freeSpace = freeSpace.advance(heading)
    }

    if (grid[freeSpace] == '#') {
        return location
    }

    check((freeSpace.x - location.x).isOdd())

    // Now shift everything.
    while (freeSpace != location) {
        grid[freeSpace] = grid[freeSpace.advance(heading.opposite())]!!
        freeSpace = freeSpace.advance(heading.opposite())
    }

    grid.remove(location)

    return location.advance(heading)
}

private fun Map<Vector, Char>.transformForPart2(): Map<Vector, Char> {
    val orgEntries = this.entries
    return buildMap {
        put(Vector(0,0), '!')

        orgEntries.forEach { (location, char) ->
            val l1 = Vector(location.x * 2, location.y)
            val l2 = l1.advance(Heading.EAST)
            when (char) {
                '#', '.' -> {
                    this[l1] = char
                    this[l2] = char
                }
                '@' -> {
                    this[l1] = '@'
                    this[l2] = '.'
                }
                'O' -> {
                    this[l1] = '['
                    this[l2] = ']'
                }
            }
        }
    }
}

private fun Int.isOdd(): Boolean = abs(this).rem(2) != 0

private fun <K, V> MutableMap<K, V>.removeAll(items: Collection<K>) {
    items.forEach {
        remove(it)
    }
}

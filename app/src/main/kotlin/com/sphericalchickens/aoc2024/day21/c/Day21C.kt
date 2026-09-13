package com.sphericalchickens.aoc2024.day21.c

import com.sphericalchickens.utils.*



import kotlin.time.measureTime

val testInput = """
    029A
    980A
    179A
    456A
    379A
""".trimIndent().lines()


fun main() {
    check(part1(testInput) == 126384L)
    val input = readLines("inputs/21")
    part1(input)
//    part2(input)

    measureTime {
        part2(input)
    }.println()
}

val dpadGrid = buildGrid(
    """
    .^A
    <v>
""".trimIndent().lines()
)

val dpad = dpadGrid.entries.associate { it.value to it.key }


val keypadGrid = buildGrid(
    """
    789
    456
    123
    .0A
""".trimIndent().lines()
)

val keypad = keypadGrid.entries.associate { it.value to it.key }

sealed interface Robot {
    val location: Vector
    val grid: Map<Vector, Char>
    val pad: Map<Char, Vector>

    fun toLocation(goal: Char) = pad[goal]!!
    fun isLegalLocation(location: Vector): Boolean = grid.contains(location)
    fun toSymbolForHeading(heading: Heading): Char = grid[location.advance(heading)]!!
    fun isLegalMove(heading: Heading): Boolean = isLegalLocation(location.advance(heading))
    fun pathToGoalString(path: List<Vector>) = buildString {
        var l = location
        path.forEach {
            val heading = (it - l).toHeading()
            append(heading.toSymbol())
            l = l.advance(heading)
        }
    }

    fun pathToLocations(goalPath: String): List<Vector> {
        return goalPath.map { symbol -> pad[symbol]!! }
    }
}

data class DpadRobot(override val location: Vector = dpad['A']!!) : Robot {
    override val grid: Map<Vector, Char>
        get() = dpadGrid

    override val pad: Map<Char, Vector>
        get() = dpad
}

data class KeypadRobot(override val location: Vector = keypad['A']!!) : Robot {
    override val grid: Map<Vector, Char>
        get() = keypadGrid

    override val pad: Map<Char, Vector>
        get() = keypad
}

private fun part1(input: List<String>): Long {
    return input.map { code ->
        code to getShortestPathSize(
            robots = listOf(
                KeypadRobot(),
                DpadRobot(),
                DpadRobot()
            ),
            outputToProduce = code
        )
    }.sumOf { (code, path) ->
        code.filter { it.isDigit() }.toInt() * path
    }.also {
        it.println()
    }
}

private fun part1a(input: List<String>): Long {
    return input.map { code ->
        code to getShortestPath(
            robots = listOf(
                KeypadRobot(),
                DpadRobot(),
                DpadRobot()
            ),
            outputToProduce = code
        )
    }.sumOf { (code, path) ->
        code.filter { it.isDigit() }.toInt() * path.length.toLong()
    }.also {
        it.println()
    }
}

//private fun part2a(input: List<String>): Long {
//    val numBots = 25
//    measureTime {
//    val robots = buildList {
//        add(KeypadRobot())
//            repeat(numBots) {
//                add(DpadRobot())
//            }
//        }
//
//        val pathLength = getShortestPathSize(
//            robots = robots,
//            outputToProduce = input.first()
//        )
//
//        "$numBots: $pathLength".println()
//    }.println()
//
//    return -1
//}

private fun part2(input: List<String>): Long {
    return input.map { code ->
        val robots = buildList {
            add(KeypadRobot())
            repeat(25) {
                add(DpadRobot())
            }
        }

        code to getShortestPathSize(
            robots = robots,
            outputToProduce = code
        )
    }.sumOf { (code, path) ->
        code.filter { it.isDigit() }.toInt() * path
    }.also {
        it.println()
    }
}

val memory = mutableMapOf<Pair<List<Robot>, String>, String>()
val memory2 = mutableMapOf<Pair<List<Robot>, String>, Long>()

var hits = 0

private fun getShortestPath(robots: List<Robot>, outputToProduce: String): String {
//    "====================================".println()
//    "(robots: $robots, outputToProduce: $outputToProduce)".println()

    if (robots.isEmpty()) {
        // For example, if I were able ot stand directly at the keypad, this would be the solution
//        println("Buttons for me to push: $outputToProduce")
//        println(" ")
        return outputToProduce
    }

    memory[robots to outputToProduce]?.let {
        hits += 1
        return it
    }

    val bot = robots.first()

    // Turn the goalPath into a series of locations
    val locationsToVisit = ArrayDeque(bot.pathToLocations(outputToProduce))

    locationsToVisit.addFirst(bot.location)

    val subGoals = locationsToVisit.windowed(2, 1) {(src, dst) ->
        src to dst
    }

    val problemsToSolve = subGoals.map { subGoal ->
        val src = subGoal.first
        val dst = subGoal.second

        if (src == dst) {
            emptyList()
        } else {
//        println("Go from $src to $dst (${bot.grid[src]} to ${bot.grid[dst]})")
            val possiblePaths = getAllPaths(bot, src, dst)
//        possiblePaths.printAsLines()
//        println("")
            possiblePaths
        }
    }

//    "problems to solve".println()
//    problemsToSolve.printAsLines()
//    "".println()

    val full = problemsToSolve.map { subPathOptions ->
//        "subPathOptions: $subPathOptions".println()

        // We have a list of all paths to consider between some src and dst
        val movementOptions = if (subPathOptions.isEmpty()) { listOf("A") } else subPathOptions.map { pathAsLocations ->
            // Translate this to a dpad movement path
            // Don't forget to push the button
            pathAsLocations.toDpadSequence() + 'A'
        }

//        "movement options: ${movementOptions}".println()

        // Now, which of these is going to give us the shortest result?
        val possiblePaths = movementOptions.map { path ->
//            "remaining robots ${robots.drop(1)}: $path ".println() //  (robots: $robots, outputToProduce: $outputToProduce)
            getShortestPath(robots.drop(1), path)
        }

//        "possiblePaths: $possiblePaths".println()

        if (possiblePaths.isEmpty()) {
            ""
        } else {
            possiblePaths.minBy {
                it.length
            }
        }
    }

    return full.joinToString("").also {
        memory[robots to outputToProduce] = it
//        "$bot + $outputToProduce = $it".println()
    }
}


private fun getShortestPathSize(robots: List<Robot>, outputToProduce: String): Long {
//    "====================================".println()
//    "(robots: $robots, outputToProduce: $outputToProduce)".println()

    if (robots.isEmpty()) {
        // For example, if I were able ot stand directly at the keypad, this would be the solution
//        println("Buttons for me to push: $outputToProduce")
//        println(" ")
        return outputToProduce.length.toLong()
    }

    memory2[robots to outputToProduce]?.let {
        hits += 1
        return it
    }

    val bot = robots.first()

    // Turn the goalPath into a series of locations
    val locationsToVisit = ArrayDeque(bot.pathToLocations(outputToProduce))

    locationsToVisit.addFirst(bot.location)

    val subGoals = locationsToVisit.windowed(2, 1) {(src, dst) ->
        src to dst
    }

    val problemsToSolve = subGoals.map { subGoal ->
        val src = subGoal.first
        val dst = subGoal.second

        if (src == dst) {
            emptyList()
        } else {
//        println("Go from $src to $dst (${bot.grid[src]} to ${bot.grid[dst]})")
            val possiblePaths = getAllPaths(bot, src, dst)
//        possiblePaths.printAsLines()
//        println("")
            possiblePaths
        }
    }

//    "problems to solve".println()
//    problemsToSolve.printAsLines()
//    "".println()

    val full = problemsToSolve.map { subPathOptions ->
//        "subPathOptions: $subPathOptions".println()

        // We have a list of all paths to consider between some src and dst
        val movementOptions = if (subPathOptions.isEmpty()) { listOf("A") } else subPathOptions.map { pathAsLocations ->
            // Translate this to a dpad movement path
            // Don't forget to push the button
            pathAsLocations.toDpadSequence() + 'A'
        }

//        "movement options: ${movementOptions}".println()

        // Now, which of these is going to give us the shortest result?
        val possiblePaths = movementOptions.map { path ->
//            "remaining robots ${robots.drop(1)}: $path ".println() //  (robots: $robots, outputToProduce: $outputToProduce)
            getShortestPathSize(robots.drop(1), path)
        }

//        "possiblePaths: $possiblePaths".println()

        if (possiblePaths.isEmpty()) {
            0
        } else {
            possiblePaths.minBy {
                it
            }
        }
    }

    return full.sum().also {
        memory2[robots to outputToProduce] = it
    }
}


private fun List<Vector>.toDpadSequence(): String {
    return windowed(2, 1).map { (a, b) ->
        b - a
    }.map { it.toHeading().toSymbol() }.joinToString("")
}

// Include the source and the destination!
private fun getAllPaths(
    bot: Robot,
    src: Vector,
    dst: Vector
): List<List<Vector>> {
//    "###################".println()
//    "getAllPaths: $bot $src $dst".println()

    val allHeadingSequences = getHeadingSequences(src, dst).toList() //.also { .printAsLines() }

    val permutations = allHeadingSequences.permutations().toList()

//    "permutations: $permutations".println()

    val movementSequences = permutations.map { headings ->
        var location = src

        buildList {
            add(src)
            headings.forEach { heading ->
                location.advance(heading).also {
                    location = it
                    add(it)
                }
            }
        }
    }

//    "movement sequences: $movementSequences".println()

    val legalPaths = movementSequences.filter { locations ->
        locations.all {
            val isLegal = bot.isLegalLocation(it)
//            if (!isLegal) {
//                "Illegal path: $locations".println()
//            }
            isLegal
        }
    }

//    "Legal paths: $legalPaths".println()

    return legalPaths
}


private fun getHeadingSequences(src: Vector, dst: Vector) = sequence {
    val delta = dst - src

    if (delta.x > 0) {
        repeat(delta.x) {
            yield(Heading.EAST)
        }
    }

    if (delta.x < 0) {
        repeat(-delta.x) {
            yield(Heading.WEST)
        }
    }

    if (delta.y > 0) {
        repeat(delta.y) {
            yield(Heading.SOUTH)
        }
    }

    if (delta.y < 0) {
        repeat(-delta.y) {
            yield(Heading.NORTH)
        }
    }
}















private fun getAllPaths(
    bot: Robot,
    path: String
): List<Sequence<List<Vector>>> {
    return path.map { goal ->
        getAllPaths(bot, goal)
    }
}

private fun getAllPaths(
    bot: Robot,
    goal: Char
): Sequence<List<Vector>> {
    val goalLocation = bot.toLocation(goal)

    val allHeadingSequences = getHeadingSequences(bot, goalLocation).toList() //.also { .printAsLines() }
//    allHeadingSequences.permutations().toList().printAsLines()

    return allHeadingSequences.permutations().map { headings ->
        var location = bot.location
        headings.map { heading ->
            location.advance(heading).also { location = it }
        }
    }.filter { locations ->
        locations.all {
            val isLegal = bot.isLegalLocation(it)
            isLegal
        }
    }
}

private fun <E> List<E>.permutations(): Sequence<List<E>> = sequence {
    if (size == 1) {
        yield(listOf(first()))
    }

    val indices = this@permutations.indices

    val emitted = mutableSetOf<List<E>>()

    for (index in indices) {
        val rest = subList(0, index) + subList(index + 1, size)

        rest.permutations().forEach {
            val next = buildList {
                add(this@permutations.get(index))
                addAll(it)
            }

            if (!emitted.contains(next)) {
                emitted.add(next)
                yield(next)
            }
        }
    }
}

private fun getHeadingSequences(robot: Robot, goal: Vector) = sequence {
    val delta = goal - robot.location

    if (delta.x > 0) {
        repeat(delta.x) {
            yield(Heading.EAST)
        }
    }

    if (delta.x < 0) {
        repeat(-delta.x) {
            yield(Heading.WEST)
        }
    }

    if (delta.y > 0) {
        repeat(delta.y) {
            yield(Heading.SOUTH)
        }
    }

    if (delta.y < 0) {
        repeat(-delta.y) {
            yield(Heading.NORTH)
        }
    }
}

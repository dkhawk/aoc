package com.sphericalchickens.aoc2024.day21.b

import com.sphericalchickens.utils.*




//val testInput = """
//    029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
//    980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
//    179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
//    456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
//    379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
//""".trimIndent().lines()
private val testInput = """
    029A
    980A
    179A
    456A
    379A
""".trimIndent().lines()

fun main() {
    check(part1(testInput) == 126384)
}

// ========================================

private val dpadGrid = buildGrid(
    """
    .^A
    <v>
""".trimIndent().lines()
)

private val dpad = dpadGrid.entries.associate { it.value to it.key }

private val keypadGrid = buildGrid(
    """
    789
    456
    123
    .0A
""".trimIndent().lines()
)

private val keypad = keypadGrid.entries.associate { it.value to it.key }

// ========================================

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

data class State(
    val robots: List<Robot>,
)

data class StateWithGoal(
    val robots: List<Robot>,
    val goal: Vector,
)

private fun part1(input: List<String>): Int {

//    getAllPaths(DpadRobot(dpad['A']!!), '<').toList().printAsLines()
//    getAllPaths(KeypadRobot(keypad['A']!!), '7').toList().printAsLines()
//
//    println()

//    val b = getSetOfHeadings(DpadRobot(dpad['<']!!), 'A').also { it.printAsLines() }

//    TODO()

    // Terminal case!!
//    findMinimumSequence(
//        StateWithGoal(
//            robots = emptyList(),
//            goal = '^'
//        )
//    ).println()

//    // Get the first robot to move to '^'
//    // There should be exactly one answer!
//    findMinimumSequence(
//        StateWithGoal(
//            listOf(DpadRobot()), '^'
//        )
//    ).println()
//
//    // Get the first robot to move to '>'
//    // There should be exactly one answer!
//    findMinimumSequence(
//        StateWithGoal(
//            listOf(DpadRobot()), '>'
//        )
//    ).println()

    findMinimumSequence(
        robots = listOf(DpadRobot()),
        goal = "v"
    ).println()

//    // Move the first robot by directly using the dpad!
//    DpadRobot().apply {
//        move(dpad['^']!!).println()
//    }
//
//    KeypadRobot().move(keypad['0']!!).println()

    TODO()
}

private fun getAllPaths(
    bot: Robot,
    goal: Vector
): Sequence<List<Vector>> {
    val allHeadingSequences = getHeadingSequences(bot, goal).toList() //.also { .printAsLines() }
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

private val statesWithMinPath = mutableMapOf<Pair<List<Robot>, String>, List<String>>()

private fun findMinimumSequence(robots: List<Robot>, goal: String): List<String> {
    // If we already know the solution, return it!
    statesWithMinPath[robots to goal]?.let { return it }

    if (robots.isEmpty()) {
        // No robots to move, just push the f'ing button!
        return listOf(goal)
    }

    val bot = robots.first()

    goal.map { next ->
        val nextGoal = bot.toLocation(next)
        val paths = getAllPaths(bot, nextGoal).toList()
        paths.printAsLines()
        paths.map {
            bot.pathToGoalString(it)
        }.printAsLines()
    }

    TODO()


//    // Get the goal location for this particular robot!
//    val goalLocation = bot.toLocation(goal)
//    val delta = goalLocation - bot.location
//    val magnitude = delta.magnitude()
//
//
//    // Trivial case:
//    if (magnitude == 1) {
//        val newGoal = bot.toSymbolForHeading(delta.toHeading())
//        val newStateWithGoal = StateWithGoal(robots.drop(1), newGoal)
//        val result = findMinimumSequence(newStateWithGoal)
//
//        return updateMinPath(stateWithGoal, result)
//    }
//
//    // Now we have to try all the possible paths
//    // I am guessing that we will eventually need to return all possible shortest paths!
//
//    // Move the bot one step closer to the goal
//    // There should be at most two options
//    val candidateMoves = Heading.entries.filter { heading ->
//        // Is this a legal move that gets us closer to the goal?
//        bot.isLegalMove(heading) && ((goalLocation - bot.location.advance(heading)).magnitude() < magnitude)
//    }.also { it.println() }
//
//    candidateMoves.map { heading ->
//        val newBot = bot.move(heading)
//        val newStateWithGoal = StateWithGoal(
//            buildList {
//                add(newBot)
//                addAll(robots.drop(1))
//            },
//            goal
//        )
//        buildString {
//            append(bot.toSymbolForHeading(heading))
//            append(findMinimumSequence(newStateWithGoal))
//        }
//    }.println()
//
//    println()
//    println()

    TODO()
}

//private fun updateMinPath(stateWithGoal: StateWithGoal, result: List<String>): List<String> {
//    val oldPaths = statesWithMinPath[stateWithGoal]
//
//    if (oldPaths == null) {
//        statesWithMinPath[stateWithGoal] = result
//        return result
//    }
//
//    // We are only trying to minimize the final element of the list
//    val oldCost = oldPaths.last().length
//    val newCost = result.last().length
//
//    if (newCost < oldCost) {
//        statesWithMinPath[stateWithGoal] = result
//        return result
//    }
//
//    return oldPaths
//}

private fun Vector.toDpadSymbol() = Heading.entries.first { it.vector == this@toDpadSymbol }.toSymbol()

inline fun <reified T : Robot> T.move(goal: Vector): Robot {
    require((goal - this.location).magnitude() == 1) { "Magnitude must be 1" }

    return when (this) {
        is DpadRobot -> DpadRobot(goal)
        is KeypadRobot -> KeypadRobot(goal)
        else -> error("Should not happen")
    }
}

inline fun <reified T : Robot> T.move(heading: Heading): Robot {
    val newLocation = location.advance(heading)

    return when (this) {
        is DpadRobot -> DpadRobot(newLocation)
        is KeypadRobot -> KeypadRobot(newLocation)
        else -> error("Should not happen")
    }
}

package com.sphericalchickens.aoc2024.day21

import com.sphericalchickens.utils.*




//val testInput = """
//    029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
//    980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
//    179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
//    456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
//    379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
//""".trimIndent().lines()
val testInput = """
    029A
    980A
    179A
    456A
    379A
""".trimIndent().lines()

fun main() {
//    reverseCheck("v<<A^>>AvA^Av<<A^>>AAv<A<A^>>AA<Av>AA^Av<A^>AA<A>Av<A<A^>>AAA<Av>A^A")
//    check(reverseCheck("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A") == "029A")
//    check(reverseCheck("<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A") == "980A")
//    check(reverseCheck("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A") == "179A")
//    check(reverseCheck("<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A") == "456A")

//    check(reverseCheck("<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A") == "379A")
//
//    println()
//    println()
//
//
//    check(reverseCheck("v<<A^>>AvA^Av<<A^>>AAv<A<A^>>AA<Av>AA^Av<A^>AA<A>Av<A<A^>>AAA<Av>A^A") == "379A")

    check(part1(testInput) == 126384)
    return

    check(part2(testInput) == 0)

    val input = readLines("inputs/0")
}

sealed interface DpadCommand

data class Move(val heading: Heading) : DpadCommand
data object PushIt : DpadCommand

fun reverseCheck(mySequence: String): String {
    val dpadCommand = mapOf(
        '^' to Move(Heading.NORTH),
        'v' to Move(Heading.SOUTH),
        '<' to Move(Heading.WEST),
        '>' to Move(Heading.EAST),
        'A' to PushIt
    )

    val coldRobotCommands = mySequence.map { dpadChar ->
        dpadCommand.getValue(dpadChar)
    }


//    "cold: $coldRobotCommands".println()

    val coldRobotOutput = buildList {
        coldRobotCommands.fold(Vector(2, 0)) { location, command ->
            when (command) {
                is Move -> location.advance(command.heading)
                PushIt -> {
                    add(dpadGrid.getValue(location))
                    location
                }
            }
        }
    }

    mySequence.split("A").map { it + 'A' }.zip(coldRobotOutput).println()

    val radiationRobotCommands = coldRobotOutput.map { dpadChar ->
        dpadCommand.getValue(dpadChar)
    }

//    "radiation: $radiationRobotCommands".println()

    val radiationRobotOutput = buildList {
        radiationRobotCommands.fold(Vector(2, 0)) { location, command ->
            when (command) {
                is Move -> location.advance(command.heading)
                PushIt -> {
                    add(dpadGrid.getValue(location))
                    location
                }
            }
        }
    }

    coldRobotOutput.joinToString("").split("A").map { it + 'A' }.zip(radiationRobotOutput).println()

    val keypadRobotCommands = radiationRobotOutput.map { dpadChar ->
        dpadCommand.getValue(dpadChar)
    }

//    "keypad: $keypadRobotCommands".println()

    val code = buildString {
        keypadRobotCommands.fold(Vector(2, 3)) { location, command ->
            when (command) {
                is Move -> location.advance(command.heading)
                PushIt -> {
                    append(keypadGrid.getValue(location))
                    location
                }
            }
        }
    }

    radiationRobotOutput.joinToString("").split("A").map { it + 'A' }.zip(code.toList()).println()

    return code.also { it.println() }
}

private fun part1(input: List<String>): Int {

//    moveRobot(DpadRobot(),)

    val robotState = RobotStates(
        listOf(
            KeypadRobot(), // KBOT
            DpadRobot(), // RBOT
            DpadRobot(), // CBOT
        )
    )

    val stateWithGoal = StateWithGoal(robotState, keypad.getValue('0'))
//    findShortestSequence(stateWithGoal)

    TODO()
//    return input.sumOf { code ->
//        val myDpadSequence = findCommands(code)
//
//        "$code: $myDpadSequence".println()
//
//        val complexity = myDpadSequence.length * code.filter { it.isDigit() }.toInt(10)
//
//        complexity.println()
//        complexity
//    }
}

fun moveRobot(dpadRobot: DpadRobot, c: Char) {
    TODO("Not yet implemented")
}

val statesMap: MutableMap<StateWithGoal, MoveSequence> = mutableMapOf()

const val CBOT = 2
const val RBOT = 1
const val KBOT = 0

//fun findShortestSequence(stateWithGoal: StateWithGoal): MoveSequence {
//    // We already know how to solve this!
//    statesMap[stateWithGoal]?.let { moveSequence ->
//        return moveSequence
//    }
//
//    if (stateWithGoal.robotState.robots.size == 1) {
//        // I can just push the dpad buttons to move the robot!
//
//        // If there is only
//    }
//
//    val goal = stateWithGoal.goal
//    val bot = stateWithGoal.robotState.robots.first()
//
//    // starting with the keypad robot find the deltas to the goal.
//    // calculate the deltas for the keypad robot
//    val delta = goal - bot.location
//
//    // Now, determine the cost to get to the next step towards the goal.  There will only be two!
//    // (Could it ever make sense for there to more than two?)
//
//    // We want the cost to each of the possible next squares
//
//    val distanceToGoal = delta.magnitude()
//
//    val candidates = Heading.entries.filter { heading ->
//        val next = bot.location.advance(heading)
//        bot.isLegal(next) && (goal - next).magnitude() < distanceToGoal
//    }
//
//    candidates.println()
//
//    val newRobotState = RobotStates(stateWithGoal.robotState.robots.drop(1))
//
//    candidates.map { heading ->
//        // What movement is going to get the robot closer to the goal?
//        val moveSequence = MoveSequence(listOf(heading))
//        findShortestSequence(StateWithGoal(newRobotState, ))
//    }
//
//    TODO()
//}

data class RobotStates(
    val robots: List<Robot>,
) {
    override fun toString(): String {
        return robots.joinToString(", ") { "${it.location}" }
    }
}

@JvmInline
value class MoveSequence(val movements: List<Heading>)

data class StateWithGoal(
    val robotState: RobotStates,
    val goal: Vector,
)

sealed interface Robot {
    val location: Vector

    fun isLegal(next: Vector): Boolean
}

data class KeypadRobot(
    override val location: Vector = keypad['A']!!,
) : Robot {
    override fun isLegal(next: Vector) = keypadGrid.containsKey(next)
}

data class DpadRobot(
    override val location: Vector = dpad['A']!!,
) : Robot {
    override fun isLegal(next: Vector) = dpadGrid.containsKey(next)
}

data class WorldState(
    val keyPadRobot: KeypadRobot = KeypadRobot(),
    val radiationBot: DpadRobot = DpadRobot(),
    val coldBot: DpadRobot = DpadRobot(),
) {
    override fun toString(): String {
        return "keypadBot: $keyPadRobot\nradiationBot: $radiationBot\ncoldBot: $coldBot"
    }
}

//private fun findCommands(code: String): String {
//    val keyPadRobotDeltas = code.toDeltas().toArrayDeque()
//
//    val visited = mutableMapOf<WorldState, Int>()
//    visited[WorldState(code = "")] = 0
//
//    while (keyPadRobotDeltas.isNotEmpty()) {
//        val nextDelta = keyPadRobotDeltas.removeAt(keyPadRobotDeltas.lastIndex)
//
//
//    }
//
//    // TODO: if this is too slow, add caching here!!
//
//    keyPadRobotDeltas.forEach { delta ->
//
//    }
//
//    val radiationRobotDeltas =
//        keyPadRobotDeltas.joinToString("") { delta ->
//            delta.toDpadSequence()
//        }.dpadToDeltas('A')
//
//    TODO()
//
//
//    val coldRobotDeltas =
//        radiationRobotDeltas.joinToString("") { delta -> delta.toDpadSequence() }.dpadToDeltas('A')
//    val myDpadSequence = coldRobotDeltas.joinToString("") { delta -> delta.toDpadSequence() }
//    return myDpadSequence
//}

private fun <E> List<E>.toArrayDeque(): ArrayDeque<E> {
    return ArrayDeque<E>().apply { addAll(this@toArrayDeque) }
}


//"<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"
//"v<<A^>>AvA^Av<<A^>>AAv<A<A^>>AA<Av>AA^Av<A^>AA<A>Av<A<A^>>AAA<Av>A^A"

private fun String.dpadToDeltas(initialPosition: Char): List<Vector> {
    val s = this

    return buildList {
        add(dpad.getValue(s.first()) - dpad.getValue(initialPosition))

        s.toList().windowed(2, 1).forEach { (prev, next) ->
            add(dpad.getValue(next) - dpad.getValue(prev))
        }
    }
}

private fun Vector.toDpadSequence(): String {
    var goal = Vector(-this.x, -this.y)

    return buildString {
        // Go South first to avoid hovering over the empty space!
        while (goal.y < 0) {
            goal = goal.advance(Heading.SOUTH)
            append('v')
        }
        while (goal.y > 0) {
            goal = goal.advance(Heading.NORTH)
            append('^')
        }
        while (goal.x < 0) {
            goal = goal.advance(Heading.EAST)
            append('>')
        }
        while (goal.x > 0) {
            goal = goal.advance(Heading.WEST)
            append('<')
        }

        // Execute the operation
        append('A')
    }
}

val keypadGrid = buildGrid(
    """
    789
    456
    123
     0A
""".trimIndent().lines()
)

val keypad = keypadGrid.entries.associate { it.value to it.key }

val dpadGrid = buildGrid(
    """
     ^A
    <v>
""".trimIndent().lines()
)

val dpad = dpadGrid.entries.associate { it.value to it.key }

private fun String.toDeltas(): List<Vector> {
    val s = this

    return buildList {
        // Always start at 'A'
        add(keypad.getValue(s.first()) - keypad.getValue('A'))

        s.toList().windowed(2, 1).forEach { (prev, next) ->
            add(keypad.getValue(next) - keypad.getValue(prev))
        }
    }
}

private fun part2(input: List<String>): Int {
    return -1
}
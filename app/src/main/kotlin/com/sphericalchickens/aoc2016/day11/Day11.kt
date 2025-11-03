package com.sphericalchickens.aoc2016.day11

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import java.util.PriorityQueue
import java.util.SortedSet
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 11 ---")

    val input = readInputLines("aoc2016/day11_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val timeInMillis = measureTimeMillis {
            val part1Result = part1(input)
            println("   Part 1: $part1Result")
        }
        println("Part 1 runtime: $timeInMillis ms.")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val timeInMillis = measureTimeMillis {
            val part2Result = part2(input)
            println("   Part 2: $part2Result")
        }
        println("Part 2 runtime: $timeInMillis ms.")
    }
}

private fun runPart1Tests() {
    val testInput = """
        The first floor contains a hydrogen-compatible microchip and a lithium-compatible microchip.
        The second floor contains a hydrogen generator.
        The third floor contains a lithium generator.
        The fourth floor contains nothing relevant.
    """.trimIndent().lines()
//    check("Part 1 Test Case 1", "expected", part1(testInput))
    for (line in testInput) {
        val floor = parseLine(line)
        println("$line : ${floor.chips}, ${floor.generators}")
    }

    check("Part 1 Test Case 1", 11, part1(testInput))
}

private fun runPart2Tests() {
    val testInput = """
        
    """.trimIndent().lines()
    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private data class Floor(val objects: SortedSet<String>) {
    val generators = objects.filter { it.last() == 'G' }
    val chips = objects.filter { it.last() == 'M' }
}

private data class State(val elevatorFloor: Int, val floors: List<Floor>) {
    val score = floors.withIndex().sumOf {(index, floor) ->
        val s = floor.objects.size * index
        s * s
    }
}

private fun State.isValid(): Boolean {
    return floors.all { it.isValid() }
}

private fun Floor.isValid(): Boolean {
    if (generators.isEmpty() || chips.isEmpty()) {
        return true
    }

    return chips.all { generators.contains(it.toGenerator()) }
}

private fun String.toGenerator(): String {
    return first() + "G"
}

private fun part1(input: List<String>): Int {
    /*
    Max elevator capacity = 2 items
    Min elevator load = 1 item
    Elevator stops at every floor
     */

    val elevator = 0
    val floors = input.map { parseLine(it) }
    val state = State(elevator, floors)

    return solve(state)
}

private fun solve(state: State): Int {
    //  No loops!
    val visited = mutableSetOf(state)

    val work = PriorityQueue<Pair<State, Int>>(compareBy { it.second })

    work.addAll(state.nextMoves(visited, work).map { it to 1 })

    while (work.isNotEmpty()) {
        val nextState = work.poll()

        if (nextState.first.isSuccess()) {
            render(nextState.first).println()
            return nextState.second
        }

        val nextMoves = nextState.first.nextMoves(visited, work).map { it to nextState.second + 1 }
        val s = nextMoves.firstOrNull { it.first.isSuccess() }

        if (s != null) {
            return s.second
        }

        work.addAll(nextMoves)
//        "There are ${work.size} items in the queue".println()
    }

    return -1
}

private fun State.isSuccess(): Boolean {
    return (
            elevatorFloor == 3 &&
            floors[0].objects.isEmpty() &&
            floors[1].objects.isEmpty() &&
            floors[2].objects.isEmpty() &&
            floors[3].objects.isNotEmpty()
        )
}

private fun Floor.code(): String {
    return buildString {
        val sets = objects.groupBy { it.first() }
        val (groupsWithPairs, groupsWithSingle) = sets.values.partition { it.size >= 2 }
        append(groupsWithPairs.size)
        append("+")
        append(groupsWithSingle.joinToString(","))
    }
}

private fun State.code(): String {
    return floors.mapIndexed { index, floor ->
        (if (index == elevatorFloor) "E" else ".") + floor.code()
    }.joinToString(";")
}

private fun State.nextMoves(visited: MutableSet<State>, work: PriorityQueue<Pair<State, Int>>): List<State> {
    val floor = floors[elevatorFloor]

    val cargoOptions = buildList {
        for (firstItem in floor.objects) {
            add(setOf(firstItem))
            for (secondItem in (floor.objects - firstItem)) {
                add(setOf(firstItem, secondItem))
            }
        }
    }.map { it.sorted() }.toSet()

    val nextElevatorOptions = when (elevatorFloor) {
        0 -> listOf(1)
        1 -> listOf(0, 2)
        2 -> listOf(1, 3)
        3 -> listOf(2)
        else -> error("WTF: elevator floor: $elevatorFloor")
    }

    return buildList {
        nextElevatorOptions.forEach { nextFloor ->
            cargoOptions.forEach { cargo ->

                val newFloorObjects = floors[nextFloor].objects + cargo
                val oldFloorObjects = floors[elevatorFloor].objects - cargo

                val newFloors = buildList {
                    for (idx in floors.indices) {
                        when (idx) {
                            nextFloor -> add(Floor(newFloorObjects.toSortedSet()))
                            elevatorFloor -> add(Floor(oldFloorObjects.toSortedSet()))
                            else -> add(floors[idx])
                        }
                    }
                }
                val proposedState = State(nextFloor, newFloors)

                if (!visited.contains(proposedState)) {
                    visited.add(proposedState)
                    if (proposedState.isValid()) {
                        add(proposedState)
                    }
                }
            }
        }
    }
}

private fun part2(input: List<String>): Int {
    /*
    Max elevator capacity = 2 items
    Min elevator load = 1 item
    Elevator stops at every floor
     */

    /*
    An elerium generator.
An elerium-compatible microchip.
A dilithium generator.
A dilithium-compatible microchip.
     */

    val elevator = 0
    val floors = input.map { parseLine(it) }

    floors[0].objects.addAll(listOf("EG", "EM", "DG", "DM"))

    val state = State(elevator, floors)

    render(state).println()

    // The important realization here is that it doesn't matter which of the pairs are moved first -- the answer is the same.

    //  No loops!
    val visited = mutableSetOf(state)

    val visitedByCode = mutableSetOf<String>()

    visitedByCode.add(state.code())

    val work = PriorityQueue<Pair<State, Int>>(compareBy { it.second })

    work.addAll(state.nextMoves(visited, work).map { it to 1 })

    while (work.isNotEmpty()) {
        val nextState = work.poll()

        if (nextState.first.isSuccess()) {
            render(nextState.first).println()
            return nextState.second
        }

        val nextMoves = nextState.first.nextMoves(visited, work).map { it to nextState.second + 1 }

        val s = nextMoves.firstOrNull { it.first.isSuccess() }
        if (s != null) {
            return s.second
        }

        val n = nextMoves.filterNot { visitedByCode.contains(it.first.code()) }

        visitedByCode.addAll(n.map { it.first.code() })

        // Prune all redundant moves
//        val m = nextMoves.associateBy { it.first.code() }
        work.addAll(n)

//        "There are ${work.size} items in the queue".println()
    }

    return -1
}

private val MicroChipRegex = Regex("""(\w+)-compatible microchip""")
private val GeneratorRegex = Regex("""(\w+) generator""")

private fun parseLine(line: String): Floor {
    val microChips = MicroChipRegex.findAll(line).flatMap { it.groupValues.drop(1) }.toList().map { it.first().uppercase() + "M" }
    val generators = GeneratorRegex.findAll(line).flatMap { it.groupValues.drop(1) }.toList().map { it.first().uppercase() + "G" }

    return Floor((microChips + generators).toSortedSet())
}

private fun render(state: State): String {
    val metals = state.floors.flatMap { it.objects }.toSortedSet()

    return buildString {
        for (floorIndex in (0..3).reversed()) {
            val floor = state.floors[floorIndex]
            val floorObjects = floor.objects
            val l = ("F${floorIndex + 1} ") + (if (state.elevatorFloor == floorIndex) "E  " else ".  ") +
                    metals.joinToString(" ") {
                        if (floorObjects.contains(it)) it else ". "
                    }

            append(l)
            append("\n")
        }
    }
}
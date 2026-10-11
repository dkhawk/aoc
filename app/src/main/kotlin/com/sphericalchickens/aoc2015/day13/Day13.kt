package com.sphericalchickens.aoc2015.day13

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code 2015, Day 13: Knights of the Dinner Table
 *
 * Placeholder template for Day 13.
 */
fun main() {
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc2015/day13_input.txt")
    println("\n--- Advent of Code 2015, Day 13: Knights of the Dinner Table ---")

    // --- Part 1 ---
    val (part1Result, part1Duration) = measureTimedValue {
        part1(puzzleInput)
    }
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${formatDuration(part1Duration)}")

    // --- Part 2 ---
    val (part2Result, part2Duration) = measureTimedValue {
        part2(puzzleInput)
    }
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${formatDuration(part2Duration)}")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

data class NodeCost(
    val person: String,
    val neighbor: String,
    val cost: Int,
)

fun String.toNodeCost() : NodeCost {
    val parts = this.trimEnd('.').split(" ")
    val sign = when(parts[2]) {
        "gain" -> 1
        "lose" -> -1
        else -> throw Exception("Invalid cost $parts[2] in $parts")
    }

    return NodeCost(
        person = parts[0],
        neighbor = parts.last(),
        cost = parts[3].toInt() * sign,
    )
}

fun part1(input: List<String>): Int {
    val nodes = input.map { node -> node.toNodeCost() }

    // build for each pairing since each pairing has two costs
    val pmap = nodes.groupBy { it.person }.mapValues { b -> b.value.associate { it.neighbor to it.cost } }

    // Now, start with an arbitrary person and do the search
    val leftToSeat = pmap.keys.toList()

    return seatNextPerson(pmap, leftToSeat.take(1), leftToSeat.drop(1))
}

fun seatNextPerson(pmap: Map<String, Map<String, Int>>, seated: List<String>, leftToSeat: List<String>): Int {
    if (leftToSeat.size == 1) {
        // Last person!
        val target = leftToSeat.first()
        val n1 = seated.last()
        val n2 = seated.first()
        return score(pmap, target, n1) + score(pmap,target, n2)
    }

    // Last person seated
    val lastPersonSeated = seated.last()
    val candidates = pmap[lastPersonSeated]!!.keys - seated

    return candidates.maxOfOrNull { candidate ->
        score(pmap, candidate, lastPersonSeated) + seatNextPerson(pmap, seated + candidate, leftToSeat - candidate)
    }!!
}

fun score(pmap: Map<String, Map<String, Int>>, target: String, n1: String) : Int {
    return pmap[target]!![n1]!! + pmap[n1]!![target]!!
}

fun part2(input: List<String>): Int {
    val nodes = input.map { node -> node.toNodeCost() }

    val originalPmap = nodes.groupBy { it.person }.mapValues { b -> b.value.associate { it.neighbor to it.cost } }
    val people = originalPmap.keys

    val pmap = originalPmap.mapValues { (_, neighbors) ->
        neighbors + ("Me" to 0)
    } + ("Me" to people.associateWith { 0 })

    val leftToSeat = pmap.keys.toList()

    return seatNextPerson(pmap, leftToSeat.take(1), leftToSeat.drop(1))
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    val testInput = """
Alice would gain 54 happiness units by sitting next to Bob.
Alice would lose 79 happiness units by sitting next to Carol.
Alice would lose 2 happiness units by sitting next to David.
Bob would gain 83 happiness units by sitting next to Alice.
Bob would lose 7 happiness units by sitting next to Carol.
Bob would lose 63 happiness units by sitting next to David.
Carol would lose 62 happiness units by sitting next to Alice.
Carol would gain 60 happiness units by sitting next to Bob.
Carol would gain 55 happiness units by sitting next to David.
David would gain 46 happiness units by sitting next to Alice.
David would lose 7 happiness units by sitting next to Bob.
David would gain 41 happiness units by sitting next to Carol.
    """.trimIndent().lines().filter(String::isNotBlank)

    check(testInput[0],testInput[0].toNodeCost(), NodeCost("Alice", "Bob", 54))
    check(testInput[1],testInput[1].toNodeCost(), NodeCost("Alice", "Carol", -79))

    // TODO: Add real test expectations when implementing
    check("Part 1 stub check", 330, part1(testInput))
}

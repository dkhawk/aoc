package com.sphericalchickens.aoc2024.day05

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readText
import kotlin.time.measureTime

val input = """
    47|53
    97|13
    97|61
    97|47
    75|29
    61|13
    75|53
    29|13
    97|29
    53|29
    61|53
    97|53
    61|29
    47|13
    75|47
    97|75
    47|61
    75|61
    47|29
    75|13
    53|13
    
    75,47,61,53,29
    97,61,53,29,13
    75,29,13
    75,97,47,61,53
    61,13,29
    97,13,75,29,47
""".trimIndent().lines()

typealias RuleSet = List<Pair<Int, Int>>

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(input) == 143)
    check(part2(input) == 123)

    // Read the input from the `src/Day01.txt` file.
    val input = readText("inputs/05").lines()

    val (p1, d1) = kotlin.time.measureTimedValue { part1(input) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

fun part1(input: List<String>): Int {
    val rules = parseRules(input)
    val updates = parseUpdates(input)

    return updates.sumOf { update -> validateUpdate(update, rules) }
}

fun part2(input: List<String>): Int {
    val rules = parseRules(input)
    val updates = parseUpdates(input)

    return updates.filter { update -> validateUpdate(update, rules) == 0 }
        .sumOf { update -> fixUpdate(update, rules) }
}

fun validateUpdate(update: List<Int>, rules: RuleSet): Int {
    val printed = mutableListOf<Int>()

    val (blockedBy, iAmBlocking) = createBlockingMaps(rules, update)

    val iterator = update.iterator()
    while (iterator.hasNext()) {
        val page = iterator.next()
        if (blockedBy.containsKey(page)) {
            // The update is invalid
            return 0
        }
        printed.add(page)
        updateBlockingMaps(iAmBlocking, page, blockedBy)
    }

    return printed.middle()
}

fun fixUpdate(update: List<Int>, rules: RuleSet): Int {
    val printed = mutableListOf<Int>()

    val (blockedBy, iAmBlocking) = createBlockingMaps(rules, update)

    val unprintedSet = update.toMutableSet()

    while (unprintedSet.isNotEmpty()) {
        val unblockedPages = unprintedSet - blockedBy.keys

        // Enforce the assumption that there is only one valid option
        require(unblockedPages.size == 1)

        val page = unblockedPages.first()
        printed.add(page)
        unprintedSet.remove(page)
        updateBlockingMaps(iAmBlocking, page, blockedBy)
    }

    return printed.middle()
}

private fun updateBlockingMaps(
    iAmBlocking: Map<Int, Set<Int>>,
    page: Int,
    blockedBy: MutableMap<Int, MutableSet<Int>>
) {
    iAmBlocking[page]?.let { pagesIamBlocking ->
        pagesIamBlocking.forEach { blockedPage ->
            blockedBy[blockedPage]?.let { pagesIamBlockedBy ->
                // Current page is no longer blocked by "page"
                pagesIamBlockedBy.remove(page)
                if (pagesIamBlockedBy.isEmpty()) {
                    // Current page is no longer blocked at all
                    blockedBy.remove(blockedPage)
                }
            }
        }
    }
}

private fun createBlockingMaps(
    rules: RuleSet,
    update: List<Int>
): Pair<MutableMap<Int, MutableSet<Int>>, Map<Int, Set<Int>>> {
    val blockedBy = mutableMapOf<Int, MutableSet<Int>>()
    val iAmBlocking = mutableMapOf<Int, MutableSet<Int>>()
    rules.forEach { (first, second) ->
        if (update.contains(first)) {
            blockedBy.getOrPut(second) { mutableSetOf() }.add(first)
        }
        iAmBlocking.getOrPut(first) { mutableSetOf() }.add(second)
    }
    return Pair(blockedBy, iAmBlocking)
}

fun <E> List<E>.middle(): E {
    return this[this.size / 2]
}

fun parseRules(input: List<String>) = input.takeWhile { it.isNotBlank() }.map { it.split("|").map(String::toInt).toPair() }
fun parseUpdates(input: List<String>)= input.dropWhile { it.isNotBlank() }.drop(1).filter { it.isNotBlank() }.map { it.split(",").map(String::toInt) }

private fun <E> List<E>.toPair(): Pair<E, E> {
    return this[0] to this[1]
}

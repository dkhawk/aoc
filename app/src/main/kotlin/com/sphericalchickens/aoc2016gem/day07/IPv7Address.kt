package com.sphericalchickens.aoc2016gem.day07

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

/**
 * Represents a parsed IPv7 address, separated into supernet (outside brackets)
 * and hypernet (inside brackets) sequences.
 *
 * Using a data class provides a clear, immutable structure for our parsed data,
 * which makes the subsequent logic easier to read and reason about.
 */
private data class IPv7Address(val supernets: List<String>, val hypernets: List<String>)

/**
 * Parses a raw string address into an IPv7Address data class.
 *
 * We leverage a regular expression to find all hypernet sequences `[... ]`.
 * The `Regex.findAll` function returns a sequence of match results, from which
 * we extract the content within the brackets.
 *
 * To get the supernet sequences, we simply split the original string by the
 * same hypernet pattern. This is a much more declarative and less error-prone
 * approach than manual iteration.
 */
private fun parseAddress(address: String): IPv7Address {
    val hypernetRegex = "\\[(\\w+)]".toRegex()
    val hypernets = hypernetRegex.findAll(address).map { it.groupValues[1] }.toList()
    val supernets = address.split(hypernetRegex)
    return IPv7Address(supernets, hypernets)
}

/**
 * --- Part 1 Logic: Transport-Layer Snooping (TLS) ---
 *
 * An address supports TLS if a supernet sequence contains an "ABBA" pattern,
 * and no hypernet sequence contains an "ABBA" pattern.
 */
private fun IPv7Address.supportsTls(): Boolean {
    // The logic becomes a clear and concise expression of the rules:
    // - Is there `any` supernet that contains an ABBA?
    // - And are there `none` in the hypernets?
    return supernets.any { it.containsAbba() } && hypernets.none { it.containsAbba() }
}

/**
 * Helper function to detect an "ABBA" pattern (e.g., "xyyx" but not "aaaa")
 * in any CharSequence.
 *
 * This logic is extracted into a helper to avoid duplication and improve clarity.
 * Using `windowed` is an idiomatic Kotlin way to inspect sliding sections of a sequence.
 */
private fun CharSequence.containsAbba(): Boolean {
    return windowed(4).any {
        it[0] == it[3] && it[1] == it[2] && it[0] != it[1]
    }
}

/**
 * --- Part 2 Logic: Super-Secret Listening (SSL) ---
 *
 * An address supports SSL if there is an "ABA" pattern in a supernet sequence,
 * and a corresponding "BAB" pattern in a hypernet sequence.
 */
private fun IPv7Address.supportsSsl(): Boolean {
    // First, we find all "ABA" patterns in the supernets and collect them into a Set for
    // efficient lookup. For example, "zaz" and "zbz" from "zazbz".
    val abas = supernets.flatMap { it.findAbas() }.toSet()

    // If no ABAs are found, it can't possibly support SSL.
    if (abas.isEmpty()) return false

    // Next, we find all "ABA" patterns within the hypernet sequences. These are our potential "BABs".
    val babs = hypernets.flatMap { it.findAbas() }.toSet()

    // Finally, we check if any of the `abas` we found have a corresponding `bab`.
    // We do this by transforming each `aba` (e.g., "zaz") into its "bab" form ("aza")
    // and checking for its existence in the `babs` set.
    return abas.any { aba ->
        val bab = "${aba[1]}${aba[0]}${aba[1]}"
        bab in babs
    }
}

/**
 * Helper that returns a sequence of all "ABA" patterns (e.g., "xyx" but not "xxx")
 * found in a CharSequence.
 *
 * Returning a `Sequence` is a good practice for intermediate operations, as it
 * processes the data lazily, potentially improving performance on large inputs by
 * avoiding the creation of intermediate lists.
 *
 * Note: Gemini got this wrong.  Windowed does not return a sequence.
 */
private fun CharSequence.findAbas(): List<String> {
    return windowed(3).filter {
        it[0] == it[2] && it[0] != it[1]
    }
}

private fun part1(input: List<String>): Int {
    return input.count { parseAddress(it).supportsTls() }
}

private fun part2(input: List<String>): Int {
    return input.count { parseAddress(it).supportsSsl() }
}

// --- Main function and tests ---
// The main execution block and tests remain largely the same, but are updated
// to call the new, refactored functions.

fun main() {
    println("--- Advent of Code 2016, Day 07 (Refactored) ---")

    val input = readInputLines("aoc2016/day07_input.txt")

    // --- Part 1 ---
    println("🧪 Running Part 1 tests...")
    runPart1Tests()
    println("✅ Part 1 tests passed!")

    println("🎁 Solving Part 1...")
    val part1Time = measureTimeMillis {
        val result = part1(input)
        println("   Part 1: $result")
        check("Part 1 Final Answer", 105, result)
    }
    println("   Part 1 runtime: $part1Time ms.")

    // --- Part 2 ---
    println("🧪 Running Part 2 tests...")
    runPart2Tests()
    println("✅ Part 2 tests passed!")

    println("🎀 Solving Part 2...")
    val part2Time = measureTimeMillis {
        val result = part2(input)
        println("   Part 2: $result")
        check("Part 2 Final Answer", 258, result)
    }
    println("   Part 2 runtime: $part2Time ms.")
}

private fun runPart1Tests() {
    check("abba[mnop]qrst", true, parseAddress("abba[mnop]qrst").supportsTls())
    check("abcd[bddb]xyyx", false, parseAddress("abcd[bddb]xyyx").supportsTls())
    check("aaaa[qwer]tyui", false, parseAddress("aaaa[qwer]tyui").supportsTls())
    check("ioxxoj[asdfgh]zxcvbn", true, parseAddress("ioxxoj[asdfgh]zxcvbn").supportsTls())
}

private fun runPart2Tests() {
    check("aba[bab]xyz", true, parseAddress("aba[bab]xyz").supportsSsl())
    check("xyx[xyx]xyx", false, parseAddress("xyx[xyx]xyx").supportsSsl())
    check("aaa[kek]eke", true, parseAddress("aaa[kek]eke").supportsSsl())
    check("zazbz[bzb]cdb", true, parseAddress("zazbz[bzb]cdb").supportsSsl())
}

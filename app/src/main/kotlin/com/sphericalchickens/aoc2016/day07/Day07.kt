package com.sphericalchickens.aoc2016.day07

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines
import kotlin.system.measureTimeMillis

fun main() {
    // --- Development Workflow Control Panel ---
    // Set these flags to control which parts of the solution to run.
    val runPart1Tests = true
    val runPart1Solution = true
    val runPart2Tests = true
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2016, Day 07 ---")

    val input = readInputLines("aoc2016/day07_input.txt")

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
        abba[mnop]qrst yes
        abcd[bddb]xyyx no
        aaaa[qwer]tyui no
        ioxxoj[asdfgh]zxcvbn yes
    """.trimIndent().lines()
        .map { line ->
            val (address,isABBA) = line.split(" ")
            address to (isABBA == "yes")
        }

    testInput.forEach { pair ->
        check("Part 1 Test Case 1: $pair", pair.second, pair.first.isABBA())
    }

    check("Part 1 Test Case 1", testInput.count { it.second }, part1(testInput.map { it.first }))
}

private fun String.isABBA(): Boolean {
    val (abbaGood, abbaBad) = getStringBlocks()

    val good = abbaGood.any { stringBuilder ->
        stringBuilder.toString().windowed(4).any {
            it[0] == it[3] && it[1] == it[2] && it[0] != it[1]
        }
    }
    val bad = abbaBad.none { stringBuilder ->
        stringBuilder.toString().windowed(4).any {
            it[0] == it[3] && it[1] == it[2] && it[0] != it[1]
        }
    }

    return good && bad
}

private fun runPart2Tests() {
    val testInput = """
        aba[bab]xyz yes # supports SSL (aba outside square brackets with corresponding bab within square brackets).
        xyx[xyx]xyx no # does not support SSL (xyx, but no corresponding yxy).
        aaa[kek]eke yes # supports SSL (eke in supernet with corresponding kek in hypernet; the aaa sequence is not related, because the interior character must be different).
        zazbz[bzb]cdb yes # supports SSL (zaz has no corresponding aza, but zbz has a corresponding bzb, even though zaz and zbz overlap).
    """.trimIndent().lines().map { it.substringBefore('#') }
        .map { line ->
            val (address,isSSL) = line.split(" ")
            address to (isSSL == "yes")
        }

    testInput.forEach { pair ->
        check("Part 2 Test Case 2: $pair", pair.second, pair.first.isSSL())
    }

//    check("Part 2 Test Case 1", "expected", part2(testInput))
}

private fun String.isSSL(): Boolean {
    val (aba, bab) = getStringBlocks()

    val abaSet = aba.flatMap { line->
        line.windowed(3).mapNotNull { block ->
            if (block[0] == block[2] && block[1] != block[0]) {
                block
            } else null
        }
    }.toSet()

    val babSet = bab.flatMap { line->
        line.windowed(3).mapNotNull { block ->
            if (block[0] == block[2] && block[1] != block[0]) {
                block
            } else null
        }.map {
            buildString {
                append(it[1])
                append(it[0])
                append(it[1])
            }
        }
    }.toSet()

    return abaSet.intersect(babSet).isNotEmpty()
}

private fun String.getStringBlocks(): Pair<MutableList<StringBuilder>, MutableList<StringBuilder>> {
    val aba = mutableListOf(StringBuilder())
    val bab = mutableListOf(StringBuilder())

    val iterator = this.iterator()

    var sink = aba

    while (iterator.hasNext()) {
        when (val ch = iterator.nextChar()) {
            '[' -> {
                sink = bab
                aba.add(StringBuilder())
            }

            ']' -> {
                sink = aba
                bab.add(StringBuilder())
            }

            else -> sink.last().append(ch)
        }
    }
    return Pair(aba, bab)
}


private fun part1(input: List<String>): Int {
    return input.count { it.isABBA() }
}

private fun part2(input: List<String>): Int {
    return input.count { it.isSSL() }
}

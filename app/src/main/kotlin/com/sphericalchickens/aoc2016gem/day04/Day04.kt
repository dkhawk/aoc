package com.sphericalchickens.aoc2016gem.day04

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.readInputLines

/**
 * This file provides a refactored solution for Advent of Code 2016, Day 4.
 * It follows the principles of literate programming, where the code and its explanation
 * are woven together to form a coherent narrative. The program's primary goal is to
 * validate and decrypt room codes to find the sum of sector IDs for real rooms and
 * to locate a specific room related to "north pole objects".
 *
 * The core of this solution is the `Room` data class. It encapsulates all the logic
 * related to a single room: parsing, validation, and decryption. This approach
 * provides a clean, object-oriented design where each object is self-sufficient.
 */

fun main() {
    println("--- Advent of Code 2016, Day 4 (Refactored) ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day04_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")
}

private fun runTests() {
    val testCases = """
        aaaaa-bbb-z-y-x-123[abxyz] real
        a-b-c-d-e-f-g-h-987[abcde] real
        not-a-real-room-404[oarel] real
        totally-real-room-200[decoy] decoy
    """.trimIndent().lines()

    testCases.forEach {
        val (roomString, expected) = it.split(" ")
        val room = Room.from(roomString)!!
        val expectedIsReal = (expected == "real")
        check("Room '$roomString' validity", expectedIsReal, room.isReal)
    }

    val decryptionTestRoom = Room.from("qzmt-zixmtkozy-ivhz-343[zimth]")!!
    check(
        "Decryption of 'qzmt-zixmtkozy-ivhz-343'",
        "very encrypted name",
        decryptionTestRoom.decryptedName
    )
}

/**
 * Solves Part 1 of the puzzle.
 * The logic is implemented as a processing pipeline:
 * 1. `mapNotNull { Room.from(it) }`: Each line of the input is transformed into a `Room` object.
 *    The `from` factory method handles parsing and returns `null` for invalid lines, which
 *    `mapNotNull` conveniently filters out.
 * 2. `filter { it.isReal }`: We keep only the `Room` objects that are real (i.e., have a valid checksum).
 * 3. `sumOf { it.sectorId }`: We sum the `sectorId` of the remaining real rooms.
 *
 * @param input A list of strings, where each string is a room code like "aaaaa-bbb-z-y-x-123[abxyz]".
 * @return The sum of the sector IDs of all real rooms.
 */
fun part1(input: List<String>): Int {
    return input.mapNotNull { Room.from(it) }
        .filter { it.isReal }
        .sumOf { it.sectorId }
}

/**
 * Solves Part 2 of the puzzle.
 * This pipeline is optimized for efficiency by using a `Sequence`.
 * 1. `asSequence()`: Converts the input list into a lazy sequence, avoiding the creation of intermediate collections.
 * 2. `mapNotNull { Room.from(it) }`: Lazily transforms each line into a `Room` object.
 * 3. `first { ... }`: The sequence is processed item-by-item until the first room that is both
 *    real and has a decrypted name containing "north" is found. All processing stops at this point.
 * 4. `.sectorId`: We then return the sector ID of that specific room.
 *
 * @param input A list of strings, where each string is a room code.
 * @return The sector ID of the room whose decrypted name contains "north".
 */
fun part2(input: List<String>): Int {
    return input.asSequence()
        .mapNotNull { Room.from(it) }
        .first { it.isReal && it.decryptedName.contains("north", ignoreCase = true) }
        .sectorId
}

/**
 * Represents a single room from the input.
 * This data class holds the parsed components of a room code and provides
 * functionality to validate and decrypt it.
 *
 * @property encryptedName The original, encrypted name (e.g., "aaaaa-bbb-z-y-x").
 * @property sectorId The integer ID of the room (e.g., 123).
 * @property checksum The provided checksum from the input (e.g., "abxyz"). It is private
 *           as it's an implementation detail used only for internal validation.
 */
data class Room(
    val encryptedName: String,
    val sectorId: Int,
    private val checksum: String
) {
    /**
     * Determines if the room is real by calculating the expected checksum and comparing
     * it to the one provided. The calculation is performed lazily and the result is cached.
     * This is efficient because the checksum is only ever calculated once, the first time
     * this property is accessed.
     */
    val isReal: Boolean by lazy { calculateIsReal() }

    /**
     * The decrypted name of the room, produced by shifting the letters of the `encryptedName`
     * forward by the `sectorId`. This is also a `lazy` property to ensure the decryption
     * only happens if the decrypted name is actually needed (i.e., for Part 2).
     */
    val decryptedName: String by lazy { decrypt() }

    private fun calculateIsReal(): Boolean {
        // To calculate the checksum, we first count the occurrences of each character.
        val calculatedChecksum = encryptedName
            .filter { it.isLetter() } // Ignore dashes
            .groupingBy { it }        // Group by character
            .eachCount()              // Count occurrences of each
            .entries
            // Sort the characters first by frequency (descending), then alphabetically (ascending) for ties.
            .sortedWith(compareByDescending<Map.Entry<Char, Int>> { it.value }.thenBy { it.key })
            .take(5) // Take the top 5
            .joinToString("") { it.key.toString() } // Join them into a string

        return calculatedChecksum == checksum
    }

    private fun decrypt(): String {
        val shift = sectorId % 26
        return encryptedName.map { char ->
            when (char) {
                '-' -> ' ' // Dashes become spaces
                // For letters, we shift them by `shift` positions, wrapping around the alphabet.
                else -> 'a' + (char - 'a' + shift) % 26
            }
        }.joinToString("")
    }

    /**
     * A companion object serves as a namespace for factory methods and constants
     * related to the `Room` class but not tied to a specific instance.
     */
    companion object {
        // This regex is used to parse the entire room string into its constituent parts.
        // It uses named capture groups (`?<name>`, `?<id>`, `?<sum>`) for clarity.
        private val ROOM_REGEX = """(?<name>[a-z-]+)-(?<id>\d+)\[(?<sum>[a-z]{5})\]""".toRegex()

        /**
         * A factory method to safely construct a `Room` from a raw string.
         * It returns a nullable `Room?`, which is an idiomatic way to handle parsing
         * failures in Kotlin without resorting to exceptions for invalid input formats.
         *
         * @param roomString The raw string to parse.
         * @return A `Room` instance if parsing is successful, otherwise `null`.
         */
        fun from(roomString: String): Room? {
            val match = ROOM_REGEX.matchEntire(roomString) ?: return null
            // Safely extract values from capture groups.
            val name = match.groups["name"]?.value ?: return null
            val sectorId = match.groups["id"]?.value?.toIntOrNull() ?: return null
            val checksum = match.groups["sum"]?.value ?: return null

            return Room(
                encryptedName = name,
                sectorId = sectorId,
                checksum = checksum
            )
        }
    }
}

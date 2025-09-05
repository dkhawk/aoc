package com.sphericalchickens.aoc2016.day04

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.println
import com.sphericalchickens.utils.readInputLines

/*
A room is real (not a decoy) if the checksum is the five most common letters in the encrypted name, in order, with ties broken by alphabetization. For example:

aaaaa-bbb-z-y-x-123[abxyz] is a real room because the most common letters are a (5), b (3), and then a tie between x, y, and z, which are listed alphabetically.
a-b-c-d-e-f-g-h-987[abcde] is a real room because although the letters are all tied (1 of each), the first five are listed alphabetically.
not-a-real-room-404[oarel] is a real room.
totally-real-room-200[decoy] is not.
Of the real rooms from the list above, the sum of their sector IDs is 1514.

What is the sum of the sector IDs of the real rooms?
 */

fun main() {
    println("--- Advent of Code 2016, Day 4 ---")

    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    val input = readInputLines("aoc2016/day04_input.txt")

    val part1Result = part1(input)
    println("🎁 Part 1: $part1Result")

    val part2Result = part2(input)
    println("🎀 Part 2: $part2Result")

}

fun part1(input: List<String>): Int {
    return input.map { it.toRoom() }.filter { it.type == RoomType.REAL }.sumOf { it.sectorId }
}

fun part2(input: List<String>): Int {
    val candidates = input.map { it.toRoom() }.filter { it.type == RoomType.REAL }.filter { it.decryptedName().contains("north", ignoreCase = true) }

    if (candidates.size == 1) {
        return candidates.first().sectorId
    }

    error("Too many candidate rooms")
}

private val roomRe = Regex("""(?<encrypted>[a-z-]+)-(?<sectorId>\d+)\[(?<sum>[a-z]+)]""")

private data class Room(
    val encryptedName: String,
    val checkSum: String,
    val sectorId: Int
) {
    fun decryptedName(): String {
        return encryptedName.map {
            when (it) {
                '-' -> ' '
                else -> 'a' + (((it - 'a') + sectorId) % 26)
            }
        }.joinToString("")
    }

    val type: RoomType = if (verifyChecksum(encryptedName, checkSum)) RoomType.REAL else RoomType.DECOY
}

private fun verifyChecksum(encryptedName: String, checkSum: String): Boolean {
    val hist = encryptedName.filterNot { it == '-' }.groupingBy { it }.eachCount()

    val actualSum = hist.entries.sortedWith(
        compareByDescending<Map.Entry<Char, Int>> { it.value }
            .thenBy { it.key }
    ).map { (key, _) -> key }.take(5).joinToString("")

    return actualSum == checkSum
}

enum class RoomType {
    REAL,
    DECOY
}

private fun runTests() {
    val testInput = """
        aaaaa-bbb-z-y-x-123[abxyz] real
        a-b-c-d-e-f-g-h-987[abcde] real
        not-a-real-room-404[oarel] real
        totally-real-room-200[decoy] decoy
    """.trimIndent().lines().map {
        val (roomCode, expected) = it.trim().split(" ")
        roomCode.toRoom() to expected.toRoomType()
    }

    testInput.forEach {(room, type) ->
        check(
            "$room expected to be $type",
            type,
            room.type
        )
    }

    check(
        "part 2",
        "very encrypted name",
        "qzmt-zixmtkozy-ivhz-343[abcde]".toRoom().decryptedName()
    )
}

private fun String.toRoomType(): RoomType {
    return when (this) {
        "real" -> RoomType.REAL
        "decoy" -> RoomType.DECOY
        else -> error("Invalid room type: $this")
    }
}

private fun String.toRoom(): Room {
    val match = roomRe.matchEntire(this) ?: error("Invalid room \"$this\"")
    return Room(
        encryptedName = match.groups["encrypted"]!!.value,
        checkSum = match.groups["sum"]!!.value,
        sectorId = match.groups["sectorId"]!!.value.toInt(),
    )
}

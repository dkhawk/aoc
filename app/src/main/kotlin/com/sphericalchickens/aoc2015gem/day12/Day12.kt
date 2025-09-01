package com.sphericalchickens.aoc2015gem.day12

import com.sphericalchickens.utils.readInputLines
import kotlinx.serialization.json.*

/**
 * # Advent of Code 2015, Day 12: JSAbacusFramework.io
 *
 * This program solves the puzzle for Day 12 of Advent of Code 2015.
 * The task is to sum all the numbers in a JSON document, with an added twist for part 2.
 *
 * This solution uses the `kotlinx.serialization.json` library to parse the JSON,
 * which is a more robust and idiomatic approach than manual parsing.
 */
fun main() {
    println("--- Advent of Code 2015, Day 12 (Gemini) ---")

    val puzzleInput = readInputLines("aoc2015/day12_input.txt").joinToString("")

    // Part 1: Sum all numbers in the JSON document.
    val part1Result = sumJson(puzzleInput, ignoreRed = false)
    println("🎁 Part 1: $part1Result")

    // Part 2: Sum all numbers, but ignore any object (and all of its children)
    // that has a property with the value "red".
    val part2Result = sumJson(puzzleInput, ignoreRed = true)
    println("🎀 Part 2: $part2Result")
}

/**
 * Parses a JSON string and calculates the sum of all numbers within it.
 *
 * @param jsonString The JSON string to parse.
 * @param ignoreRed If `true`, any object containing a property with the value "red" will be ignored.
 * @return The sum of all numbers in the JSON document.
 */
private fun sumJson(jsonString: String, ignoreRed: Boolean): Int {
    val json = Json.parseToJsonElement(jsonString)
    return sum(json, ignoreRed)
}

/**
 * Recursively traverses a `JsonElement` and sums all the numbers within it.
 *
 * @param element The `JsonElement` to traverse.
 * @param ignoreRed If `true`, any object containing a property with the value "red" will be ignored.
 * @return The sum of all numbers in the `JsonElement`.
 */
private fun sum(element: JsonElement, ignoreRed: Boolean): Int {
    return when (element) {
        is JsonObject -> {
            // If we need to ignore "red" and this object contains a "red" property, its sum is 0.
            if (ignoreRed && element.values.any { it is JsonPrimitive && it.content == "red" }) {
                0
            } else {
                // Otherwise, sum the values of all its children.
                element.values.sumOf { sum(it, ignoreRed) }
            }
        }
        // For an array, sum the values of all its elements.
        is JsonArray -> element.sumOf { sum(it, ignoreRed) }
        is JsonPrimitive -> {
            // If the primitive is a string, its value is 0. Otherwise, try to parse it as an Int.
            if (element.isString) 0 else element.content.toIntOrNull() ?: 0
        }
    }
}
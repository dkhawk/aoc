package com.sphericalchickens.app.aoc2015.day02

import com.sphericalchickens.utils.readInputLines

/**
 * # Advent of Code 2015, Day 2: I Was Told There Would Be No Math
 *
 * This program solves the puzzle by calculating the amount of wrapping paper and ribbon
 * needed for a list of presents. The logic is structured to be read top-down,
 * starting with the main execution function.
 *
 * ## Principles Applied
 * 1.  **Literate Programming**: The code is organized to be explanatory. The `main` function
 * reads like a set of instructions, and documentation explains the purpose of each part.
 * 2.  **Idiomatic Kotlin**: We use features like data classes, companion object factories,
 * the `sumOf` aggregate function, and `lazy` initialization to write concise and readable code.
 * 3.  **Data-Oriented Design**: A `Box` data class is introduced to represent a present,
 * encapsulating its dimensions and related calculations. This replaces scattered extension
 * functions and makes the logic much cleaner.
 */
fun main() {
    // --- Verification ---
    // Before processing the real data, we run checks against known examples from the puzzle
    // description. This ensures our core logic is correct. If a check fails, the program
    // will stop with an informative error.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // The puzzle input is a list of dimensions (e.g., "4x2x3"). We read these from a
    // file located in the project's resources folder, filtering out any blank lines.
    val presentDimensions = readInputLines("aoc2015/day02_input.txt")
    println("🎁 Found ${presentDimensions.size} presents to wrap.")


    // --- Part 1: Calculate Total Wrapping Paper ---
    // We use the `sumOf` function to iterate through each dimension string,
    // calculate the required paper for that box, and sum the results.
    val totalWrappingPaper = presentDimensions.sumOf { calculateWrappingPaper(it) }
    println("📜 Part 1 Result: Total wrapping paper needed is $totalWrappingPaper sq. ft.")

    // --- Part 2: Calculate Total Ribbon ---
    // Similarly, we calculate the total ribbon needed by summing the requirements for each box.
    val totalRibbon = presentDimensions.sumOf { calculateRibbonLength(it) }
    println("🎀 Part 2 Result: Total ribbon needed is $totalRibbon ft.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * Represents a rectangular box with length, width, and height.
 *
 * This data class is the central abstraction of our program. It holds the dimensions
 * and provides computed properties for all the necessary calculations, such as surface area,
 * volume, and perimeters. Using `lazy` ensures that these values are computed only
 * once, the first time they are accessed.
 */
private data class Box(val l: Int, val w: Int, val h: Int) {
    /** The areas of the three unique faces (l*w, w*h, and h*l). */
    private val faceAreas: List<Int> by lazy { listOf(l * w, w * h, h * l) }

    /** The perimeters of the three unique cross-sections. */
    private val facePerimeters: List<Int> by lazy { listOf(2 * (l + w), 2 * (w + h), 2 * (h + l)) }

    /** The total surface area of the box. */
    val surfaceArea: Int by lazy { faceAreas.sum() * 2 }

    /** The area of the smallest face, required for extra paper. */
    val smallestFaceArea: Int by lazy { faceAreas.min() }

    /** The smallest perimeter of any face, for wrapping the ribbon. */
    val smallestFacePerimeter: Int by lazy { facePerimeters.min() }

    /** The volume of the box, for the ribbon's bow. */
    val volume: Int by lazy { l * w * h }

    companion object {
        /**
         * A factory method to create a `Box` instance by parsing a string like "2x3x4".
         * Using a factory in the companion object is a common Kotlin pattern.
         */
        fun from(dimensions: String): Box {
            val (l, w, h) = dimensions.split('x').map { it.toInt() }
            return Box(l, w, h)
        }
    }
}

/**
 * ## Part 1: Calculates the wrapping paper needed for one box.
 * The formula is the box's total surface area plus the area of its smallest side as slack.
 */
private fun calculateWrappingPaper(dimensions: String): Int {
    val box = Box.from(dimensions)
    return box.surfaceArea + box.smallestFaceArea
}

/**
 * ## Part 2: Calculates the ribbon needed for one box.
 * The formula is the smallest face perimeter (to wrap around the box) plus the
 * box's volume (for the bow).
 */
private fun calculateRibbonLength(dimensions: String): Int {
    val box = Box.from(dimensions)
    return box.smallestFacePerimeter + box.volume
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes a series of checks to validate the core logic against the examples
 * provided in the puzzle description.
 */
private fun runTests() {
    // Part 1 Test Cases
    check(calculateWrappingPaper("2x3x4") == 58) { "Test failed for 2x3x4 paper" }
    check(calculateWrappingPaper("1x1x10") == 43) { "Test failed for 1x1x10 paper" }

    // Part 2 Test Cases
    check(calculateRibbonLength("2x3x4") == 34) { "Test failed for 2x3x4 ribbon" }
    check(calculateRibbonLength("1x1x10") == 14) { "Test failed for 1x1x10 ribbon" }
}
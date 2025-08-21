package com.sphericalchickens.app.aoc2015.day01

import com.sphericalchickens.utils.readInputText

/**
 * # Advent of Code 2015, Day 1: Not Quite Lisp
 *
 * This program solves a puzzle about determining Santa's floor in an infinitely tall
 * apartment building based on a series of instructions.
 *
 * ## Principles Applied
 * 1.  **Literate Programming**: The `main` function is structured as a narrative that explains
 * each step: testing, reading input, and solving each part of the puzzle.
 * 2.  **Idiomatic Kotlin**:
 * - For Part 1, we use the `count()` function, which is a highly readable and direct
 * way to solve the problem without manual looping or mapping.
 * - For Part 2, a simple `for` loop with `withIndex()` is used. While a functional
 * chain is possible, the imperative loop is often clearer and more efficient for
 * "find the first" type problems.
 * - The original `toValues()` and `State` class are removed in favor of more
 * direct, inline logic.
 */
fun main() {
    // --- Verification ---
    // We start by running checks against the known examples from the puzzle description.
    // This ensures our logic is sound before we apply it to the real puzzle input.
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    // The puzzle input is a single, long string of parentheses. We read the entire file
    // from the resources directory. This is more robust than a hardcoded file path.
    val puzzleInput = readInputText("aoc2015/day01_input.txt")
    println("\n--- Advent of Code 2015, Day 1 ---")


    // --- Part 1: Calculate Final Floor ---
    // The final floor is simply the number of 'up' instructions minus the number of 'down'
    // instructions. We call our specialized function and print the result.
    val finalFloor = findFinalFloor(puzzleInput)
    println("🎅 Part 1: Santa's final floor is $finalFloor.")


    // --- Part 2: Find First Basement Entry ---
    // Next, we find the exact instruction that causes Santa to enter the basement for the
    // first time and print its position.
    val basementPosition = findFirstBasementEntryPosition(puzzleInput)
    println("地下 Part 2: Santa first enters the basement at position $basementPosition.")
}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

/**
 * ## Part 1: Calculates Santa's final floor.
 *
 * An opening parenthesis `(` means go up one floor (+1), and a closing parenthesis `)`
 * means go down one floor (-1). The final floor can be calculated directly by
 * subtracting the total count of `)` from the total count of `(`.
 *
 * @param instructions A string of parentheses, e.g., "(())".
 * @return The final integer floor number.
 */
private fun findFinalFloor(instructions: String): Int {
    return instructions.count { it == '(' } - instructions.count { it == ')' }
}

/**
 * ## Part 2: Finds the position of the first character that causes Santa to enter the basement.
 *
 * This function iterates through the instructions, tracking the floor level. It returns
 * the 1-based position of the character that first makes the floor level `-1`.
 *
 * @param instructions A string of parentheses.
 * @return The character position (1-based) for entering the basement, or -1 if never.
 */
private fun findFirstBasementEntryPosition(instructions: String): Int {
    var currentFloor = 0
    for ((index, instruction) in instructions.withIndex()) {
        currentFloor += if (instruction == '(') 1 else -1

        if (currentFloor == -1) {
            return index + 1 // The problem asks for a 1-based position
        }
    }
    return -1 // Should not happen based on puzzle constraints, but good practice
}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {
    // Part 1 Test Cases
    check(findFinalFloor("(())") == 0)
    check(findFinalFloor("()()") == 0)
    check(findFinalFloor("(((") == 3)
    check(findFinalFloor("(()(()(") == 3)
    check(findFinalFloor("))(((((") == 3)
    check(findFinalFloor("())") == -1)
    check(findFinalFloor("))(") == -1)
    check(findFinalFloor(")))") == -3)
    check(findFinalFloor(")())())") == -3)

    // Part 2 Test Cases
    check(findFirstBasementEntryPosition(")") == 1)
    check(findFirstBasementEntryPosition("()())") == 5)
}
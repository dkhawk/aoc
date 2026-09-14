import json
import os

with open("tools/aoc_titles.json") as f:
    titles = json.load(f)

def generate_placeholder(year, day, title):
    dp = f"{day:02d}"
    return f'''package com.sphericalchickens.aoc{year}.day{dp}

import com.sphericalchickens.utils.check
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines
import kotlin.time.measureTimedValue

/**
 * # Advent of Code {year}, Day {day}: {title}
 *
 * Placeholder template for Day {day}.
 */
fun main() {{
    // --- Verification ---
    println("🧪 Running tests...")
    runTests()
    println("✅ Tests passed!")

    // --- Setup ---
    val puzzleInput = readInputLines("aoc{year}/day{dp}_input.txt")
    println("\\n--- Advent of Code {year}, Day {day}: {title} ---")

    // --- Part 1 ---
    val (part1Result, part1Duration) = measureTimedValue {{
        part1(puzzleInput)
    }}
    println("🎁 Part 1: $part1Result")
    println("Part 1 runtime: ${{formatDuration(part1Duration)}}")

    // --- Part 2 ---
    val (part2Result, part2Duration) = measureTimedValue {{
        part2(puzzleInput)
    }}
    println("🎀 Part 2: $part2Result")
    println("Part 2 runtime: ${{formatDuration(part2Duration)}}")
}}

// ---------------------------------------------------------------------------------------------
// Core Logic
// ---------------------------------------------------------------------------------------------

fun part1(input: List<String>): Int {{
    // TODO: Implement Part 1
    return 0
}}

fun part2(input: List<String>): Int {{
    // TODO: Implement Part 2
    return 0
}}

// ---------------------------------------------------------------------------------------------
// Utilities & Test Functions
// ---------------------------------------------------------------------------------------------

/**
 * Executes checks to validate the core logic against known test cases.
 */
private fun runTests() {{
    val testInput = """
        sample
    """.trimIndent().lines()

    // Non-failing stub checks
    check("Part 1 Sample", 0, part1(testInput))
    check("Part 2 Sample", 0, part2(testInput))
}}
'''

# 2016 Days 18..22
for d in [18, 19, 20, 21, 22]:
    dp = f"{d:02d}"
    title = titles.get("2016", {}).get(str(d), f"Day {d}")
    path = f"app/src/main/kotlin/com/sphericalchickens/aoc2016/day{dp}/Day{dp}.kt"
    with open(path, "w") as f:
        f.write(generate_placeholder(2016, d, title))
    print(f"Generated 2016 Day {dp}: {title}")

# 2023 Days 1..25
for d in range(1, 26):
    dp = f"{d:02d}"
    title = titles.get("2023", {}).get(str(d), f"Day {d}")
    path = f"app/src/main/kotlin/com/sphericalchickens/aoc2023/day{dp}/Day{dp}.kt"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(generate_placeholder(2023, d, title))
    print(f"Generated 2023 Day {dp}: {title}")

# 2019 incomplete days: 2, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16
for d in [2, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16]:
    dp = f"{d:02d}"
    title = titles.get("2019", {}).get(str(d), f"Day {d}")
    path = f"app/src/main/kotlin/com/sphericalchickens/aoc2019/day{dp}/Day{dp}.kt"
    with open(path, "w") as f:
        f.write(generate_placeholder(2019, d, title))
    print(f"Generated 2019 Day {dp}: {title}")

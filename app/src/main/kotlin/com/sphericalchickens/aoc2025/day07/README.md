[< Day 06](../day06/README.md) | [AoC 2025 >](../README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/7)

## Setup

You land in a North Pole teleporter lab with a glitchy teleporter. To fix it, you analyze a "tachyon manifold" diagram. The diagram contains splitters (`^`) that affect the path of tachyon beams falling from a source (`S`).

## Solution

### Part 1

In Part 1, we simulate a single tachyon beam falling through the manifold.
*   The beam falls downwards.
*   When it hits a splitter (`^`), it splits into two beams (left and right).
*   We track the active beam positions row by row.
*   The answer is the total number of splits that occur.

### Part 2

Part 2 introduces a "quantum" interpretation: we must count the total number of distinct timelines (paths) a single particle could take.
*   Since the particle takes *both* paths at a splitter in different timelines, the number of paths grows exponentially.
*   We use **recursion with memoization** to solve this efficiently.
*   We define the state by `(beam_position, rows_remaining)`.
*   We cache the number of valid timelines for each state to avoid recalculating identical sub-problems.

<img src="day07_art.png" width="400" alt="Day 7 Art" />

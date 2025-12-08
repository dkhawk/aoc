[< Day 05](../day05/README.md) | [Day 07 >](../day07/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/6)

## Setup

After falling into a garbage compactor and meeting a family of cephalopods, you are tasked with helping the youngest one with her math homework. The homework involves a list of problems where numbers are either added or multiplied.

The input is given in a strange format: vertical columns of numbers with an operator at the bottom. However, Part 2 reveals that these are actually numbers written in "Cephalopod math" (right-to-left columns).

## Solution

### Part 1

For Part 1, we treat the input as if it were simply "unrolled". We tackle the vertical structure by transposing the input grid, turning columns into rows.
*   We identify the operator at the end of each row (`+` or `*`).
*   We parse the numbers in the row.
*   We apply the operator to the list of numbers (starting with 0 for addition, 1 for multiplication).
*   The answer is the sum of all individual problem results.

### Part 2

Part 2 clarifies that the vertical columns are digits of single numbers, written right-to-left.
*   **Parsing**: We find the start and width of each number by scanning the operator line for non-space characters.
*   **Construction**: We extract the vertical digits for each number and form the full integer value.
*   **Calculation**: We pair the constructed numbers with their operators and compute the results just like in Part 1.

<img src="day06_art.png" width="400" alt="Day 6 Art" />

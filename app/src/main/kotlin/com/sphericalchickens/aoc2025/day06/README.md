# Day 6: Trash Compactor

<p align="center">
  <img src="day06_art.png" alt="Day 6 Art" width="400"/>
</p>

## Problem Description

[https://adventofcode.com/2025/day/6](https://adventofcode.com/2025/day/6)

## Solution Explanation

### Part 1: Unrolling the Worksheet

The input for this problem is presented in a somewhat unusual format: numbers arranged vertically in columns, with an operator at the bottom. To solve this, I first **transposed** the input text. This turned the vertical columns into horizontal rows, making it much easier to process each "problem" as a standard list of numbers.

For each row (now a single problem), I identified the operator (the last character) and the sequence of numbers.
- If the operator was `+`, I started with an accumulator of `0` and added each number.
- If the operator was `*`, I started with `1` and multiplied.

The answer is the sum of the results of all these individual problems.

### Part 2: Cephalopod Math

Part 2 reveals that the input should be read right-to-left, and the vertical columns actually represent single numbers (digits top-to-bottom).

1.  **Parsing**: I identified the starting column of each number by looking for non-space characters in the bottom row (the operator line).
2.  **Number Construction**: Using these column indices, I extracted the vertical slices of digits from the grid and converted them into long integers.
3.  **Operation**: I paired these constructed numbers with their corresponding operators found in the bottom row.
4.  **Calculation**: Just like simpler math, I applied the operations (Sum or Product) to the list of numbers and summed the grand total.

## Stats

| Part | Answer | Runtime |
|:---|:---|:---|
| Part 1 | 6635273135233 | 3ms |
| Part 2 | 12542543681221 | 5ms |

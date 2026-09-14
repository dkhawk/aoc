[< Day 04](../day04/README.md) | [AoC 2015](../README.md) | [Day 06 >](../day06/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2015/day/5)

## Setup

Santa's intern elves need to categorize strings as nice or naughty based on specific textual rules.

## Solution

### Part 1

A string is nice if it contains at least 3 vowels, at least one letter appearing twice in a row, and does NOT contain `ab`, `cd`, `pq`, or `xy`.

### Part 2

A string is nice if it contains a pair of any two letters appearing twice without overlapping, and at least one letter repeating with exactly one letter between them (e.g. `xyx`).

## Performance

| Part | Runtime |
|:---:|:---:|
| Part 1 | 2.0ms |
| Part 2 | 4.0ms |
| **Total** | **6.0ms** |

<img src="day05_art.png" width="400" alt="Day 5 Art" />

[< Day 09](../day09/README.md) | [Day 11 >](../day11/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/10)

## Setup

Across the hall from the movie theater, you enter a large factory where all machines are offline.
The initialization section of the manual was eaten by a Shiba Inu, leaving only indicator light diagrams (`[.##.]`) and button wiring schematics.

## Solution

### Part 1

Each machine has indicator lights initially off (`.`). Pushing a button toggles specific lights on (`#`) and off (`.`).
We compute the fewest total button presses required to configure the indicator lights to match the machine's diagram using BFS graph search.

### Part 2

Part 2 solves the minimum button press configuration subject to exact joltage constraints using exact rational Gaussian elimination (Row Echelon Form) to solve the underlying linear system.

## Performance

| Part | Runtime |
|:---:|:---:|
| Part 1 | 12ms |
| Part 2 | 901ms |
| **Total** | **913ms** |

<img src="day10_art.png" width="400" alt="Day 10 Art" />

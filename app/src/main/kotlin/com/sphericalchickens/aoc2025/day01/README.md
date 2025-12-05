[< AoC 2025](../README.md) | [Day 02 >](../day02/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/1)

## Setup

The Elves have discovered that the secret entrance to the North Pole base has a new password security system. The system uses a dial with numbers 0 through 99. The dial starts at 50.

You are given a list of instructions (e.g., `L68`, `R48`), where `L` means turn left (decreasing numbers) and `R` means turn right (increasing numbers) by a certain amount. The dial wraps around (0 to 99).

The password is the number of times the dial ends up pointing exactly at **0** after a rotation.

## Solution

The solution simulates the movement of the dial.

### Part 1

We treat the dial instructions as signed distances:
- `R` becomes positive.
- `L` becomes negative.

We then use a `runningFold` to calculate the cumulative position of the dial starting from 50.
For each position, we check if `position.mod(100) == 0`.

```kotlin
private fun part1(input: List<String>): Int {
    return input
        .map(String::toSignedDistance)
        .runningFold(50) { acc, distance ->
            acc + distance
        }.count { it.mod(100) == 0 }
}
```

### Part 2

The second part (implemented as `part2c`) seems to involve counting how many times the dial passes through 0 (or a multiple of 100) during the rotation, effectively counting full rotations or crossings.

<img src="day01_art.png" width="400" alt="Day 1 Art" />

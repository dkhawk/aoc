[< Day 10](../day10/README.md) | [Day 12 >](../day12/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/11)

## Setup

Climbing down a ladder through a floor hatch in the factory, you discover a large toroidal reactor powering the facility.
Elves are connecting a new server rack to the reactor. Input lines define a directed acyclic network of device outputs leading from `you` to `out`.

## Solution

### Part 1

Data flows downstream from device outputs. We count every distinct path from the `you` device label to the reactor `out` connection using memoized DFS on the DAG.

### Part 2

Part 2 analyzes multi-branch signal paths and feedback loops to determine total network flow capacity.

## Performance

| Part | Runtime |
|:---:|:---:|
| Part 1 | 1ms |
| Part 2 | 1ms |
| **Total** | **2ms** |

<img src="day11_art.png" width="400" alt="Day 11 Art" />

[< Day 07](../day07/README.md) | [Day 09 >](../day09/README.md)

[View Problem on Advent of Code](https://adventofcode.com/2025/day/8)

## Setup

Equipped with teleporter maintenance knowledge, you rematerialize in a giant underground playground.
The Elves are connecting electrical junction boxes suspended in 3D space with strings of lights to power a Christmas decoration project.
Each junction box is specified as an `X,Y,Z` 3D coordinate.

## Solution

### Part 1

We connect pairs of junction boxes that are as close together as possible using 3D Euclidean distance.
When two junction boxes are connected, they form a unified circuit. We determine the state of connected circuits as junction boxes are wired together.

### Part 2

Part 2 finds the final connection that completes the minimum spanning network such that electricity can reach every single junction box.

<img src="day08_art.png" width="400" alt="Day 8 Art" />

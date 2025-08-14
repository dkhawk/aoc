package com.sphericalchickens.app.aoc2015.day01

import java.io.File

/*
Santa is trying to deliver presents in a large apartment building, but he can't find the right floor - the directions he got are a little confusing. He starts on the ground floor (floor 0) and then follows the instructions one character at a time.

An opening parenthesis, (, means he should go up one floor, and a closing parenthesis, ), means he should go down one floor.

The apartment building is very tall, and the basement is very deep; he will never find the top or bottom floors.

For example:

(()) and ()() both result in floor 0.
((( and (()(()( both result in floor 3.
))((((( also results in floor 3.
()) and ))( both result in floor -1 (the first basement level).
))) and )())()) both result in floor -3.
To what floor do the instructions take Santa?


) causes him to enter the basement at character position 1.
()()) causes him to enter the basement at character position 5.

 */

val testInputs1 = listOf(
    "(())" to 0,
    "()()" to 0,
    "(((" to 3,
    "(()(()(" to 3,
    "))(((((" to 3,
    "())" to -1,
    "))(" to -1,
    ")))" to -3,
    ")())())" to -3
)

val testInputs2 = listOf(
    ")" to 1,
    "()())" to 5,
)

fun runTests1() {
    testInputs1.forEach {
        check(part1(it.first) == it.second) {
            "part1(${it.first}) != ${it.second}"
        }
    }
}

fun runTests2() {
    testInputs2.forEach {
        check(part2(it.first) == it.second) {
            "part2(${it.first}) != ${it.second}"
        }
    }
}

fun readInput(): String {
    return File("/Users/dkhawk/IdeaProjects/advent-of-code/app/src/main/kotlin/aoc2015/inputs/01.txt").readText()
}

fun runPart1() {
    println(part1(readInput()))
}

fun runPart2() {
    println(part2(readInput()))
}

fun run() {
    runPart2()
}

fun part1(input: String): Int {
  return input.map {
      when (it) {
          '(' -> 1
          ')' -> -1
          else -> 0
      }
  }.sum()
}

fun part2(input: String): Int {
    var floor = 0
    var position = 0

    val iter = input.iterator()

    while (floor != -1 && iter.hasNext()) {
        floor += when (iter.nextChar()) {
            '(' -> 1
            ')' -> -1
            else -> 0
        }
        position += 1
    }

    return position
}

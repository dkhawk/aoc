package com.sphericalchickens.app.aoc2015.day02

import com.sphericalchickens.utils.println
import java.io.File
import kotlin.collections.drop
import kotlin.collections.forEach

val testInputs1 = listOf(
    "2x3x4" to 58,
    "1x1x10" to 43,
)

val testInputs2 = listOf(
    "2x3x4" to 34,
    "1x1x10" to 14,
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
    return File("/Users/dkhawk/IdeaProjects/advent-of-code/app/src/main/kotlin/aoc2015/inputs/02.txt").readText()
}

fun runPart1() {
    readInput().split("\n").filterNot(String::isEmpty).sumOf { part1(it) }.println()
}

fun runPart2() {
    readInput().split("\n").filterNot(String::isEmpty).sumOf { part2(it) }.println()
}

fun run() {
    runTests1()
    runPart1()
    runTests2()
    runPart2()
}

fun part1(input: String): Int {
    val sides = input.split("x").map { it.toInt() }.sides().sorted()
    return sides.sum() * 2 + sides.first()
}

private fun List<Int>.sides(): List<Int> {
    var index = 0
    val list = this

    return buildList {
        val result = this
        while (index < list.size - 1) {
            list.drop(index + 1).forEach {
                result.add(it * list[index])
            }

            index++
        }
    }
}

private fun List<Int>.perimeters(): List<Int> {
    var index = 0
    val list = this

    return buildList {
        val result = this
        while (index < list.size - 1) {
            list.drop(index + 1).forEach {
                result.add((it + list[index]) * 2)
            }

            index++
        }
    }
}

fun part2(input: String): Int {
    val sizes = input.split("x").map { it.toInt() }
    val vol = sizes.fold(1) { product, ele -> product * ele }
    val perimeters = sizes.perimeters()
    return vol + perimeters.min()
}
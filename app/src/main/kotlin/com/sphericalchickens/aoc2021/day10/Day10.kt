package com.sphericalchickens.aoc2021.day10

import com.sphericalchickens.utils.*


import java.util.LinkedList
import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day10 {
  companion object {
    fun run() {
      Day10().part1()
      Day10().part2()
    }
  }

  val sample = """
      [({(<(())[]>[[{[]{<()<>>
      [(()[<>])]({[<{<<[]>>(
      {([(<{}[<>[]}>{[]{[(<()>
      (((({<>}<{<{<>}{[]{[]{}
      [[<[([]))<([[{}[[()]]]
      [{[{({}]{}}([{[{{{}}([]
      {<[[]]>}<{[{[{[]{()[[[]
      [<(<(<(<{}))><([]([]()
      <{([([[(<>()){}]>(<<{{
      <{([{{}}[<[[[<>{}]]]>[]]
      """.trimIndent().split("\n").filter { it.isNotBlank() }

  val s2 = """
    {([(<{}[<>[]}>{[]{[(<()>
    [[<[([]))<([[{}[[()]]]
    [{[{({}]{}}([{[{{{}}([]
    [<(<(<(<{}))><([]([]()
    <{([([[(<>()){}]>(<<{{""".trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): List<String> {
    return if (useRealInput) {
      Input.readAsLines("10")  // <====== TODO Set the day number!!!!
    } else {
      sample
    }
  }

  private val corruptionScore = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
  )

  private val completionScore = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
  )

  private val bracketPairs = listOf(
    "()",
    "[]",
    "{}",
    "<>"
  ).map { it.toCharArray().let { it.first() to it.last() } }

  private val openToClose = bracketPairs.toMap()
  private val closeToOpen = bracketPairs.associate { it.second to it.first }

  private fun part1() {
    val real = true
    val inputs = getInput(useRealInput = real)

    val answer = inputs.sumOf { isCorrupt(it) }

    println(answer)
    if (!real) {
      println(answer == 26397)
    }
  }

  private fun isCorrupt(line: String): Int {
    val stack = LinkedList<Char>()

    return line.firstOrNull { c ->
      when (c) {
        in openToClose -> {
          stack.add(c)
          false
        }
        in closeToOpen -> {
          stack.removeLastOrNull() != closeToOpen[c]
        }
        else -> false
      }

    }?.let { corruptionScore[it] } ?: 0
  }

  private fun part2() {
    val inputs = getInput(useRealInput = true)

    val scores = inputs.asSequence().filter {
      isCorrupt(it) == 0
    }.map {
      completeMe(it)
    }.map { it.joinToString("") }
      .map { it to scoreMe(it) }
      .sortedBy { it.second }.toList()

    val median = scores[scores.size / 2]
    println(median)
  }

  private fun scoreMe(s: String): Long = s.fold(0L) { acc, c -> acc * 5 + completionScore[c]!! }

  private fun completeMe(line: String): List<Char> {
    val stack = LinkedList<Char>()

    line.forEach { c ->
      when (c) {
        in openToClose -> stack.add(c)
        in closeToOpen -> {
          if (stack.isEmpty()) return@forEach
          if (stack.removeLastOrNull() != closeToOpen[c]) throw Exception("bad character $c")
        }
        else -> throw Exception("bad character $c")
      }
    }

    return stack.map { c -> openToClose[c]!! }.asReversed()
  }
}

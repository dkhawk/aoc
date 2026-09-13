package com.sphericalchickens.aoc2021.day12

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day12 {
  companion object {
    fun run() {
      Day12().part1()
      Day12().part2()
    }
  }

  val sample = """
    start-A
    start-b
    A-c
    A-b
    b-d
    A-end
    b-end""".trimIndent().split("\n").filter { it.isNotBlank() }

  val sample2 = """
    dc-end
    HN-start
    start-kj
    dc-start
    dc-HN
    LN-dc
    HN-end
    kj-sa
    kj-HN
    kj-dc""".trimIndent().split("\n").filter { it.isNotBlank() }

  val sample3 = """
    fs-end
    he-DX
    fs-he
    start-DX
    pj-DX
    end-zg
    zg-sl
    zg-pj
    pj-he
    RW-he
    fs-DX
    pj-RW
    zg-RW
    start-pj
    he-WI
    zg-he
    pj-fs
    start-RW""".trimIndent().split("\n").filter { it.isNotBlank() }

  private fun getInput(useRealInput: Boolean): List<String> {
    val input = if (useRealInput) {
      Input.readAsLines("12")  // <====== TODO Set the day number!!!!
    } else {
      sample2
    }

    return input
  }

  private fun part1() {
    val inputs = getInput(useRealInput = true)

    val graph = createGraph(inputs)

    val paths = getTraversals(graph, usedSmallCaveException = true)
    println(paths.size)
  }

  private fun part2() {
    val inputs = getInput(useRealInput = true)

    val graph = createGraph(inputs)

    val paths = getTraversals(graph, usedSmallCaveException = false)
    println(paths.size)
  }

  private fun createGraph(inputs: List<String>): Map<String, List<String>> {
    return inputs.map { line ->
      parseLine(line)
    }.flatMap { p ->
      if (p.first == "start" || p.second == "end") {
        listOf(p)
      } else {
        listOf(p, p.second to p.first)
      }
    }.groupBy(keySelector = { it.first },
              valueTransform = { it.second })
  }

  private fun parseLine(line: String): Pair<String, String> =
    line.split("-").let { it.first() to it.last() }

  private fun getTraversals(
    graph: Map<String, List<String>>,
    node: String = "start",
    used: List<String> = listOf("start"),
    usedSmallCaveException: Boolean = false
  ): List<List<String>> {
    if (node == "end") {
      return listOf(used)
    }

    val children = graph[node] ?: throw Exception("No such node: $node")

    return getNextNodes(children, used, usedSmallCaveException).flatMap { newNode ->
      val usedException = if (usedSmallCaveException) {
        usedSmallCaveException
      } else {
        if (newNode == "end" || newNode.isUpperCase()) {
          usedSmallCaveException
        } else {
          newNode in used
        }
      }
      val newUsed = used.toMutableList().also { it.add(newNode) }
      getTraversals(graph, newNode, newUsed, usedException)
    }
  }

  private fun getNextNodes(
    children: List<String>,
    used: List<String>,
    usedSmallCaveException: Boolean,
  ) = children
    .filter {
      if (it == "start" || it == "end") {
        it == "end"
      } else if (it.isUpperCase()) {
        true
      } else {
        if (!usedSmallCaveException) {
          true
        } else {
          it !in used
        }
      }
    }.sorted()
}

private fun String.isUpperCase(): Boolean {
  return uppercase() == this
}

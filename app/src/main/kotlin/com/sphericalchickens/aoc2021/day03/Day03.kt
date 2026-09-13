package com.sphericalchickens.aoc2021.day03

import com.sphericalchickens.utils.*


import kotlin.math.roundToInt
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.Template

@OptIn(ExperimentalStdlibApi::class)
class Day03 {
  companion object {
    fun run() {
//      Day03().part1()
//      Day03().part2()
      Day03().part1a()
      Day03().part2a()
    }
  }

  // val sample = "".split(Regex("\\s+")).filter(String::isNotBlank).map(String::toInt)
  val sample = """
      00100
      11110
      10110
      10111
      10101
      01111
      00111
      11100
      10000
      11001
      00010
      01010""".trimIndent().split("\n").filter { it.isNotBlank() }

  @Template("#0 #1")
  data class Move(val dir: String, val dist: Int)

  private fun getInput(useRealInput: Boolean): List<String> {
    val input = if (useRealInput) {
      Input.readAsLines(3)
    } else {
      sample
    }

//    val inputFactory = InputFactory(Move::class)
//    val inputs = input.map { inputFactory.lineToClass<Move>(it) }
    return input
  }

  private fun part1() {
    val inputs = getInput(useRealInput = true)

    val counts = HashMap<Int, Int>()

    inputs.forEach { s ->
      s.toCharArray().forEachIndexed { index, c ->
        counts[index] = counts.getOrDefault(index, 0) + if (c == '1') 1 else 0
      }
    }
    println(counts)

    val gamma = counts.map { (k, v) ->
      if (v > inputs.size / 2) 1 else 0
    }

    val epsilon = gamma.map { it.xor(1)}

    println(gamma)
    println(epsilon)

    val g = gamma.joinToString("").toInt(2)
    val e = epsilon.joinToString("").toInt(2)
    println(g)
    println(e)
    val r = g * e
    println(r)
  }

  private fun part1a() {
    val inputs = getInput(useRealInput = true).map { it.toCharArray().map { it - '0' } }

    val mostCommon = inputs
      .reduce { acc, list ->
        acc.zip(list).map { (a, b) -> a + b }
      }.map { (it.toFloat() / inputs.size).roundToInt() }
      .toInt()

    val mask = 1.shl(inputs.first().size) - 1

    val leastCommon = mostCommon.xor(mask)

    println(mostCommon * leastCommon)
  }

  fun filterInts(readingsAsInts: List<List<Int>>) {
    generateSequence {  }
  }

  private fun part2a() {
    val inputs = getInput(useRealInput = true)

    val readingsAsInts = inputs.map { it.toCharArray().map { it.toString().toInt() } }

    filterInts(readingsAsInts)

    val oxy = getSensorReading(readingsAsInts, criteria = 1)
    val co2 = getSensorReading(readingsAsInts, criteria = 0)

    println(oxy * co2)
  }

  private fun getSensorReading(inputs: List<List<Int>>, index: Int = 0, criteria: Int): Int {
    if (inputs.size == 1) {
      return inputs.first().joinToString("").toInt(2)
    }

    val bitSum = inputs.sumOf { it[index] }

    val criteriaBit = if (bitSum >= (inputs.size - bitSum)) criteria else criteria.xor(1)
    val filtered = inputs.filter { it[index] == criteriaBit }

    return getSensorReading(filtered, index + 1, criteria)
  }

  private fun part2() {
    val inputs = getInput(useRealInput = true)

    val counts = HashMap<Int, Int>()

    var bitNum = 0
    var filtered = inputs

    while (bitNum < inputs.first().length && filtered.size > 1) {
      var numOnes = 0
      var numZeros = 0
      filtered.map { it[bitNum] }.forEach {
        if (it == '1') numOnes++ else numZeros++
      }
      filtered = if (numOnes >= numZeros) {
        filtered.filter { it[bitNum] == '1' }
      } else {
        filtered.filter { it[bitNum] == '0' }
      }
      bitNum++
    }

    println(filtered)
    val oxy = filtered.first().toInt(2)

    bitNum = 0
    filtered = inputs

    while (bitNum < inputs.first().length && filtered.size > 1) {
      var numOnes = 0
      var numZeros = 0
      filtered.map { it[bitNum] }.forEach {
        if (it == '1') numOnes++ else numZeros++
      }
      if (numOnes < numZeros) {
        filtered = filtered.filter { it[bitNum] == '1' }
      } else {
        filtered = filtered.filter { it[bitNum] == '0' }
      }
      bitNum++
    }

    println(filtered)
    val co2 = filtered.first().toInt(2)

    println(oxy)
    println(co2)
    println(oxy * co2)
  }
}

private fun List<Int>.toInt(): Int {
  return this.fold(0) { sum, element ->
    sum.shl(1).or(element)
  }
}


private fun <E> List<List<E>>.transposed(): List<List<E>> {
  return transpose(this)
}

fun <E> transpose(xs: List<List<E>>): List<List<E>> {
  // Helpers
  fun <E> List<E>.head(): E = this.first()
  fun <E> List<E>.tail(): List<E> = this.takeLast(this.size - 1)
  fun <E> E.append(xs: List<E>): List<E> = listOf(this).plus(xs)

  xs.filter { it.isNotEmpty() }.let { ys ->
    return when (ys.isNotEmpty()) {
      true -> ys.map { it.head() }.append(transpose(ys.map { it.tail() }))
      else -> emptyList()
    }
  }
}

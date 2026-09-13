package com.sphericalchickens.aoc2021.day04

import com.sphericalchickens.utils.*


import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day04 {
  companion object {
    fun run() {
      val time = measureTimeMillis {
//      Day04().part1()
//        Day04().part2()

        Day04().part1a()
        Day04().part2a()

      }
      println("millis: $time")
    }
  }

  val sample = """7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1
    22 13 17 11  0
     8  2 23  4 24
    21  9 14 16  7
     6 10  3 18  5
     1 12 20 15 19

     3 15  0  2 22
     9 18 13 17  5
    19  8  7 25 23
    20 11 10 24  4
    14 21 16 12  6

    14 21 17 24  4
    10 16 15  9 19
    18  8 23 26 20
    22 11 13  6  5
     2  0 12  3  7  
  """.trimIndent().split("\n").filter(String::isNotBlank)

  private fun getInput(useRealInput: Boolean): List<String> {
    val input = if (useRealInput) {
      Input.readAsLines(4)
    } else {
      sample
    }

//    val inputFactory = InputFactory(Move::class)
//    val inputs = input.map { inputFactory.lineToClass<Move>(it) }
    return input
  }

  fun readDraw(input: List<String>): List<Int> = input.first().split(",").map(String::toInt)

  class Card(val id: Int, val data: List<List<Int>>) {
    override fun toString(): String {
      return "Card #$id\n${data.joinToString("\n")}"
    }
  }

  fun readCards(input: List<String>): List<Card> {
    val lists = input.drop(1).windowed(5, 5, false) { lines ->
      lines.map { line ->
        line.trim().split(Regex(" +")).filter(String::isNotBlank).map { it.toInt() }
      }
    }

    return lists  // .map { it + transpose(it) }
      .mapIndexed { index, list ->
        Card(index, list)
      }
  }

  private fun part1() {
//    val draw = sampleDraw.map { it.toInt() }
//    val cardsIn = sampleCards

//    val input = getInput(false)
//    val draws = readDraw(input)
//    val cards = readCards(input)
//
//    var index = 5
//    while (index <= draws.lastIndex) {
//      val d = draws.subList(0, index).toSet()
//      val w = cards.map { it to winner(it, d) }
//      val winner = w.firstOrNull { it.second }
//      if (winner != null) {
//        println("winner: ${winner.first}")
//        // sum of all unmarked numbers
//        val card = winner.first.flatten()
//        val s = card.map { if (d.contains(it)) 0 else it }.sum()
//        println(s)
//        println(s * d.last())
//        break
//      }
//
//      index += 1
//    }
  }

  private fun winner(card: List<List<Int>>, draw: Set<Int>) : Boolean {
    val marked = card.map { row ->
      row.map { if (draw.contains(it)) 1 else 0 }
    }

    if (marked.firstOrNull { it.sum() >= 5 } != null) {
      return true
    }

    if (transpose(marked).firstOrNull { it.sum() >= 5 } != null) {
      return true
    }

    return false
  }

  data class numCoords(val cardId: Int, val row: Int, val col: Int)

  private fun part1a() {
    val input = getInput(true)
    val draws = readDraw(input)
    val cards = readCards(input)

    val numberMap = cards.flatMap { card ->
      card.data.flatMapIndexed { rowId, row ->
        row.mapIndexed { colId, value ->
          value to numCoords(card.id, rowId, colId + 5)
        }
      }
    }.groupBy({it.first}, {it.second})

    val cardStates = cards.map { card ->
      card.data.map { it.toMutableSet() } + transpose(card.data).map { it.toMutableSet() }
    }

    val drawIter = draws.iterator()
    val winningCards = listOf<Int>().toSortedSet()

    var draw = -1
    while (drawIter.hasNext() && winningCards.isEmpty()) {
      draw = drawIter.next()
      numberMap[draw]?.let { listOfCoords ->
        listOfCoords.forEach { coords ->
          if (!winningCards.contains(coords.cardId)) {
            cardStates[coords.cardId].let { lineState ->
              lineState[coords.row].remove(draw)
              lineState[coords.col].remove(draw)
              if (lineState[coords.row].isEmpty() || lineState[coords.col].isEmpty()) {
                winningCards.add(coords.cardId)
              }
            }
          }
        }
      }
    }

    val winner = winningCards.first()
    println(winner)
    println(cards[winner])

    val score = cardStates[winner].take(5).sumOf { it.sum() }
    println(score * draw)
  }

  private fun part2a() {
    val input = getInput(true)
    val draws = readDraw(input)
    val cards = readCards(input)

    val numberMap = cards.flatMap { card ->
      card.data.flatMapIndexed { rowId, row ->
        row.mapIndexed { colId, value ->
          value to numCoords(card.id, rowId, colId + 5)
        }
      }
    }.groupBy({it.first}, {it.second})

    val cardStates = cards.map { card ->
      card.data.map { it.toMutableSet() } + transpose(card.data).map { it.toMutableSet() }
    }

    val drawIter = draws.iterator()
    val winningCards = mutableListOf<Int>()

    var draw = -1
    while (drawIter.hasNext() && winningCards.size < cards.size) {
      draw = drawIter.next()
      numberMap[draw]?.let { listOfCoords ->
        listOfCoords.forEach { coords ->
          if (!winningCards.contains(coords.cardId)) {
            cardStates[coords.cardId].let { lineState ->
              lineState[coords.row].remove(draw)
              lineState[coords.col].remove(draw)
              if (lineState[coords.row].isEmpty() || lineState[coords.col].isEmpty()) {
                winningCards.add(coords.cardId)
              }
            }
          }
        }
      }
    }

    val winner = winningCards.last()
    println(cards[winner])

    val score = cardStates[winner].take(5).sumOf { it.sum() }
    println(score * draw)
  }


  private fun part2() {
//    val draw = sampleDraw.map { it.toInt() }
//    val cardsIn = sampleCards

//    val draw = realDraw.map { it.toInt() }
//    val cardsIn = realCards
//
//    val cards = cardsIn.map { card ->
//      card.map { row ->
//        row.split(Regex("""\s+""")).filter(String::isNotBlank).map { it.toInt() }
//      }
//    }
//
//    var index = 5
//    var previousWinners = HashSet<Int>()
//    var lastWinner = -1
//    while (index <= draw.lastIndex) {
//      val d = draw.subList(0, index).toSet()
//
//      val w = cards.mapIndexed { index, it ->
//        it to if (previousWinners.contains(index)) true else winner(it, d)
//      }
//
//      if (lastWinner == -1 && (cards.size - previousWinners.size == 1)) {
//        lastWinner = cards.indices.toSet().subtract(previousWinners).first()
//      }
//
//      w.forEachIndexed { index, result ->
//        if (result.second) {
//          previousWinners.add(index)
//        }
//      }
//
//      if (cards.size == previousWinners.size) {
//        println("done")
//        var lastCard = cards[lastWinner]
//        val card = lastCard.flatten()
//        val s = card.map { if (d.contains(it)) 0 else it }.sum()
//        println(s)
//        println(s * d.last())
//        break
//      }
//
//      index += 1
//    }
  }
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

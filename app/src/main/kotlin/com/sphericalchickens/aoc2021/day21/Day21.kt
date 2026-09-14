package com.sphericalchickens.aoc2021.day21

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import java.util.LinkedList
import java.util.Queue
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalStdlibApi::class)
class Day21 {
  companion object {
    fun run() {
      measureTimeMillis {
        Day21().part1()
      }.also {
        println("millis: $it")
      }
      measureTimeMillis {
        Day21().part2()
      }.also {
        println("millis: $it")
      }
    }
  }

  class Player(val id: Int, var position: Int, var score: Int)

  sealed class StartingState(val player1: Player, val player2: Player)
  object Sample : StartingState(
    Player(1, 4, 0),
    Player(2, 8, 0),
  )
  object Real : StartingState(
    Player(1, 2, 0),
    Player(2, 5, 0),
  )

  private fun part1() {
    //    val start = Sample
    val start = Real
    val players = mutableListOf(start.player1, start.player2)
    var whoseTurn = 0
    var numberOfRolls = 1
    val die = generateSequence(1) {
      numberOfRolls += 1
      (((it + 1) - 1) % 100) + 1
    }.iterator()
    var turnNumber = 0

    while (players.firstOrNull { it.score >= 1000 } == null) {
      val rolls = die.take(3)
      val moves = rolls.sum()
      with(players[whoseTurn]) {
        position = (((position + moves) - 1) % 10) + 1
        score += position
      }

      whoseTurn = (whoseTurn + 1) % 2
      turnNumber += 1
    }

    val loser = players.first { it.score < 1000 }
    println(loser.score)
    println(numberOfRolls)
    println((loser.score * numberOfRolls))
  }

  data class GameState(val pos1: Int, val pos2: Int, val score1: Int, val score2: Int, val whoseTurn: Int)

  private fun part2() {
    val allRolls = permutations(listOf(1, 2, 3))
    val sums = allRolls.map { it.sum() }
    val rollGroups = sums.groupingBy { it }.eachCount()

    //    val start = Sample
    val start = Real
    val initialGameState = GameState(start.player1.position, start.player2.position, 0, 0, 0)

    // Next states map
    val nextStatesMap = HashMap<GameState, MutableSet<GameState>>()
    val parentStatesMap = HashMap<GameState, MutableSet<GameState>>()

    val queue: Queue<GameState> = LinkedList()
    queue.add(initialGameState)
    nextStatesMap[initialGameState] = mutableSetOf()
    parentStatesMap[initialGameState] = mutableSetOf()

    val player1Wins = mutableSetOf<GameState>()
    val player2Wins = mutableSetOf<GameState>()

    val numberRollsTo = mutableMapOf<Pair<GameState, GameState>, Int>()

    // Generate all the win paths
    while (queue.isNotEmpty()) {
      val gameState = queue.remove()

      // next states for all possible rolls
      val nextStates = rollGroups.map { (rollTotal, count) ->
        count to if (gameState.whoseTurn == 0) {
          val newPosition = move(gameState.pos1, rollTotal)
          val newScore = gameState.score1 + newPosition
          gameState.copy(pos1 = newPosition, score1 = newScore, whoseTurn = 1)
        } else {
          val newPosition = move(gameState.pos2, rollTotal)
          val newScore = gameState.score2 + newPosition
          gameState.copy(pos2 = newPosition, score2 = newScore, whoseTurn = 0)
        }
      }

      nextStates.forEach { (count, state) ->
        // remember how many rolls would have worked for this state transition
        numberRollsTo[gameState to state] = count
        if (state !in parentStatesMap) {
          parentStatesMap[state] = mutableSetOf(gameState)
        } else {
          parentStatesMap[state]?.add(gameState)
        }

        if (state !in nextStatesMap) {
          nextStatesMap[state] = mutableSetOf()

          if (state.score1 < 21 && state.score2 < 21) {
            // Don't add finished states
            queue.add(state)
          } else {
            if (state.score1 >= 21) {
              player1Wins.add(state)
            } else {
              player2Wins.add(state)
            }
          }
        }
      }

      nextStatesMap[gameState]?.addAll(nextStates.map { it.second })
    }

    // This is the total number of ways to get to the state
    val gamesStatesCountsMap = HashMap<GameState, Long>()
    // This will only ever be one!
    gamesStatesCountsMap[initialGameState] = 1

    // Another BF traversal to propagate the counts
    queue.add(initialGameState)
    while (queue.isNotEmpty()) {
      val state = queue.remove()

      val nextState = nextStatesMap[state]!!
      val parentCount = gamesStatesCountsMap[state]!!
      nextState.forEach { newState ->
        // How many rolls would have gotten to this state all this path
        val rolls = numberRollsTo[state to newState]!!
        val countUpdate = rolls * parentCount

        // Add to the rolls from other possible paths
        gamesStatesCountsMap[newState] = gamesStatesCountsMap.getOrDefault(newState, 0) + countUpdate

        // We can only propagate this count once we have accounted for all incoming parent paths
        val parentStates = parentStatesMap[newState]!!
        parentStates.remove(state)
        if (parentStates.isEmpty()) {
          queue.add(newState)
        }
      }
    }

    val p1wins = player1Wins.mapNotNull {
      gamesStatesCountsMap[it]
    }.sum()

    val p2wins = player2Wins.mapNotNull {
      gamesStatesCountsMap[it]
    }.sum()

    println(p1wins)
    println(p2wins)
  }

  private fun move(position: Int, spaces: Int): Int = (((position + spaces) - 1) % 10) + 1

  private fun <T> permutations(pool: List<T>): List<List<T>> {
    return pool.flatMap { one ->
      pool.flatMap { two ->
        pool.map { three ->
          listOf(one, two, three)
        }
      }
    }
  }
}

private fun <T> Iterator<T>.take(number: Int): List<T> = (0 until number).map { next() }


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 21 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day21.run()
        } catch (e: Exception) {
            println("Error running Day21: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

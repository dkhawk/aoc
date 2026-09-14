package com.sphericalchickens.aoc2022.day19

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val day = 19
const val year = 2022

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
    Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      val realInput = InputNew(year, day).readAsLines().filter { it.isNotBlank() }
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val blueprints = input.map { parseLine(it) }
    blueprints.take(1).map {
      val factory = Factory(it)
      factory.build(24)
      factory.geodes
    }
  }

  val regex = Regex("""\b""")
  val numRegex = Regex("""\d+""")

  private fun parseLine(line: String): Blueprint {
    val numbers =  regex.splitToSequence(line).filter { numRegex.matches(it) }.map { it.toInt() }.toList()
    var i = 0
    return Blueprint(
      numbers[i++],
      numbers[i++],
      numbers[i++],
      numbers[i++],
      numbers[i++],
      numbers[i++],
      numbers[i++],
    )
  }

  fun part2() {
  }

  fun execute() {
    job?.cancel()
    job = scope.launch {
      running = true
      running = false
    }
  }

  fun step() {
  }

  fun stop() {
    job?.cancel()
    running = false
  }

  fun reset() {
    stop()
  }

  fun updateDataSource(useRealData: Boolean) {
    this.useRealData = useRealData
    initialize()
    reset()
  }
}

data class Blueprint(
  val id: Int,
  val oreBotOreCost: Int,
  val clayBotOreCost: Int,
  val obsidianBotOreCost: Int,
  val obsidianBotClayCost: Int,
  val geodeBotOreCost: Int,
  val geodeBotObsidianCost: Int,
)

enum class Resource {
  ORE,
  CLAY,
  OBSIDIAN,
  GEODE,
}

class Factory(
  val blueprint: Blueprint
) {
  val geodes: Int
    get() = resources.getValue(Resource.GEODE)

  private val resources = mutableMapOf<Resource, Int>()
  private val robots = mutableMapOf<Resource, Int>()

  private val botPressure: MutableMap<Resource, Double> = mutableMapOf()
  private val resourcePressure: MutableMap<Resource, Double> = mutableMapOf()

  private val botFactories = listOf(
    Resource.GEODE to BotFactory(mapOf(
      Resource.ORE to blueprint.geodeBotOreCost,
      Resource.OBSIDIAN to blueprint.geodeBotObsidianCost,
    )),
    Resource.OBSIDIAN to BotFactory(mapOf(
      Resource.ORE to blueprint.obsidianBotOreCost,
      Resource.CLAY to blueprint.obsidianBotClayCost,
    )),
    Resource.CLAY to BotFactory(mapOf(
      Resource.ORE to blueprint.clayBotOreCost,
    )),
    Resource.ORE to BotFactory(mapOf(
      Resource.ORE to blueprint.oreBotOreCost,
    )),
  ).toMap()

  init {
    Resource.values().forEach {
      resources[it] = 0
      robots[it] = 0
      botPressure[it] = 0.0
      resourcePressure[it] = 0.0
    }
    robots[Resource.ORE] = 1
    resourcePressure[Resource.GEODE] = 1.0
  }

  fun build(minutes: Int) {
    var minutesLeft = minutes
    while (minutesLeft > 0) {
      println("starting resources => $resources")

      // Let's make as much ORE as possible
      val shortages = botFactories.getValue(Resource.ORE).shortages(resources)
      println(shortages)

      val newResources = robots.toList()
      println("new resources => $newResources")

      if (shortages.isEmpty()) {
        val numBots = botFactories.getValue(Resource.ORE).createBot(resources)
        println("new ore bots => $numBots")
        robots.merge(Resource.ORE, numBots, Int::plus)
        println("after manufacture resources => $resources")
      }
      println("robots => $robots")

      resources.mergeAll(newResources)
      println("resources => $resources")

      // // What do we need most?
      // resourcePressure.maxOf { it.value }
      //
      // val shortages = botFactories.getValue(Resource.GEODE).shortages(resources)
      // println(shortages)
      //
      // // Which will we get last?
      // val neededRobots = shortages.map { (res, neededUnits) ->
      //   val numBots = robots.getValue(res)
      //   if (numBots == 0) {
      //     Double.MAX_VALUE
      //   } else {
      //     neededUnits.toDouble() / numBots
      //   }
      // }
      //
      // println(neededRobots)

      minutesLeft -= 1
      // minutesLeft = 0
      println(minutesLeft)
      println()
    }
  }
}

private fun MutableMap<Resource, Int>.mergeAll(newResources: List<Pair<Resource, Int>>) {
  newResources.forEach { (key, value) ->
    this.merge(key, value, Int::plus)
  }
}

class BotFactory(
  private val recipe: Map<Resource, Int>
) {
  fun createBot(resources: MutableMap<Resource, Int>): Int {
    val bots = recipe.minOf { (key, value) ->
      resources.getValue(key) / value
    }

    if (bots > 0) {
      recipe.forEach { (key, value) ->
        resources[key] = resources.getValue(key) - (recipe.getValue(key) * bots)
      }
    }

    return bots
  }

  fun shortages(resources: Map<Resource, Int>): Map<Resource, Int> {
    return recipe.map { neededResource ->
      neededResource.key to neededResource.value - resources.getValue(neededResource.key)
    }.filter { (_, value) -> value > 0 }.toMap()
  }
}

fun main() {
    println("--- Advent of Code 2022, Day 19 ---")
    val solver = Day(kotlinx.coroutines.GlobalScope)
    solver.useRealData = true
    try { solver.initialize() } catch (e: Exception) {}
    println("Solving Part 1:")
    val (_, p1Duration) = measureTimedValue {
        try { solver.part1() } catch (e: Exception) { println("Part 1: " + e.message) }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Solving Part 2:")
    val (_, p2Duration) = measureTimedValue {
        try { solver.part2() } catch (e: Exception) { println("Part 2: " + e.message) }
    }
    println("Part 2 runtime: ${formatDuration(p2Duration)}")
}

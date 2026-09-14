package com.sphericalchickens.aoc2018.day07

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import java.util.PriorityQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  private var job: kotlinx.coroutines.Job? = null
  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  var numWorkers = 2
  var baseTime = 0

  val sampleInput = """
    Step C must be finished before step A can begin.
    Step C must be finished before step F can begin.
    Step A must be finished before step B can begin.
    Step A must be finished before step D can begin.
    Step B must be finished before step E can begin.
    Step D must be finished before step E can begin.
    Step F must be finished before step E can begin.
  """.trimIndent().split("\n")

  init {
  }

  fun initialize() {
    input = if (useRealData) {
      numWorkers = 5
      baseTime = 60
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput
    }
  }

  fun part1() {
    val m = input.map { line -> line[5] to line[36] }
    val allSteps = m.unzip().let { l -> l.first + l.second }.toSet().sorted().toMutableList()

    val depMatrix = CharGrid(allSteps.size, '.')

    val depMap = m.groupBy( { it.second }, { it.first } )

    depMap.forEach { (blocked, blockers) ->
      blockers.forEach { blocker ->
        depMatrix[(blocker to blocked).toCoords()] = '*'
      }
    }

    val result = StringBuilder()
    while (allSteps.isNotEmpty()) {
      val next = allSteps.map { it - 'A' }.first { rowNumber ->
        depMatrix.getRow(rowNumber).none { it == '*' }
      }

      depMatrix.setColumn(next, '.')
      ('A' + next).let {
        result.append(it)
        allSteps.remove(it)
      }
    }

    println(result)
  }

  data class Job(val finishTime: Int, val stepNumber: Int) : Comparable<Job> {
    private val jobComparator = compareBy<Job> { it.finishTime }.thenBy { it.stepNumber }

    override fun compareTo(other: Job) = jobComparator.compare(this, other)

    override fun toString(): String {
      return "Job(finishTime=$finishTime, stepNumber=${'A' + stepNumber})"
    }
  }

  fun part2() {
    val m = input.map { line -> line[5] to line[36] }
    val allSteps = m.unzip().let { l -> l.first + l.second }.toSet().sorted().toMutableList()

    val depMatrix = CharGrid(allSteps.size, '.')

    val depMap = m.groupBy( { it.second }, { it.first } )

    depMap.forEach { (blocked, blockers) ->
      blockers.forEach { blocker ->
        depMatrix[(blocker to blocked).toCoords()] = '*'
      }
    }

    var currentTime = 0
    val workerPool = PriorityQueue<Job>()

    val jobQueue = PriorityQueue<Int>()

    val result = StringBuilder()
    while (allSteps.isNotEmpty() || jobQueue.isNotEmpty() || workerPool.isNotEmpty()) {
      val unblocked = allSteps.map { it - 'A' }.filter { rowNumber ->
        depMatrix.getRow(rowNumber).none { it == '*' }
      }

      jobQueue.addAll(unblocked)
      allSteps.removeAll(unblocked.map { 'A' + it })

      while (workerPool.size < numWorkers && jobQueue.isNotEmpty()) {
        val stepNumber = jobQueue.remove()
        val processingTime = stepNumber + 1 + baseTime
        workerPool.offer(
          Job(
            processingTime + currentTime,
            stepNumber,
          )
        )
      }

      if (workerPool.isEmpty()) {
        break
      }

      // Wait for the next job to finish
      val nextJobToFinish = workerPool.remove()

      currentTime = nextJobToFinish.finishTime

      depMatrix.setColumn(nextJobToFinish.stepNumber, '.')
      ('A' + nextJobToFinish.stepNumber).let {
        result.append(it)
        allSteps.remove(it)
      }
    }

    println(result)
    println(currentTime)
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

private fun Pair<Char, Char>.toCoords(): Vector {
  val a = first - 'A'
  val b = second - 'A'
  return Vector(a, b)
}


fun main() {
    println("--- Advent of Code 2018, Day 7 ---")
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

package com.sphericalchickens.aoc2021.day23

import com.sphericalchickens.utils.*


import java.util.LinkedList
import java.util.Queue
import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.COLORS
import com.sphericalchickens.utils.CharGrid
import com.sphericalchickens.utils.Vector

@OptIn(ExperimentalStdlibApi::class)
class Day23 {
  companion object {
    fun run() {
      measureTimeMillis {
        Day23().part1()
      }.also {
        println("millis: $it")
      }
      measureTimeMillis {
    //  Day23().part2()
      }.also {
        println("millis: $it")
      }
    }

    val goalLocations = mapOf(
      'A' to listOf(Vector(3, 2), Vector(3, 3)),
      'B' to listOf(Vector(5, 2), Vector(5, 3)),
      'C' to listOf(Vector(7, 2), Vector(7, 3)),
      'D' to listOf(Vector(9, 2), Vector(9, 3)),
    )
    val movementCost = mapOf(
      'A' to 1,
      'B' to 10,
      'C' to 100,
      'D' to 1000,
      'a' to 1,
      'b' to 10,
      'c' to 100,
      'd' to 1000,
    )
  }

  val sample = """
    #############
    #...........#
    ###b#c#B#D###
    ###a#d#C#A###
    #############""".trimIndent().split("\n").filter { it.isNotBlank() }.let {
      CharGrid(it)
  }

  val solved = """
    #############
    #...........#
    ###A#B#C#D###
    ###A#B#C#D###
    #############""".trimIndent().split("\n").filter { it.isNotBlank() }

  val real = """
    #############
    #...........#
    ###a#d#A#B###
    ###b#c#D#C###
    #############""".trimIndent().split("\n").filter { it.isNotBlank() }
    .let { CharGrid(it) }

  val amphipods = movementCost.keys.toSet()

  val solvedGrid = CharGrid(solved)

  val emptyGrid = CharGrid(solvedGrid.grid.map {
    if (it !in listOf('.', '#')) {
      '.'
    } else {
      it
    }
  }.toCharArray(), solvedGrid.width)

  val roomDoors = listOf(
    Vector(3, 1),
    Vector(5, 1),
    Vector(7, 1),
    Vector(9, 1),
  )

  val safeSpaces = emptyGrid.copy().also { grid ->
    roomDoors.forEach {
      grid[it] = 'x'
    }
  }

  private val roomCells = Companion.goalLocations.values.flatten().toSet()

  data class Path(val locations: List<Vector>, val amphipod: Char, val cost: Int)

  data class Paths(val paths: List<Path> = emptyList())

  data class Goal(val amphipod: Char, val target: Vector) {
    constructor(pair: Pair<Char, Int>) : this(
      pair.first,
      goalLocations[pair.first.uppercaseChar()]!![pair.second]
    )
  }

  data class WorldState(
    val grid: CharGrid,
    val unmetGoals: List<Goal>,
    val paths: Paths = Paths(),
    val cost: Int = 0,
  )

  private fun solutionSequence(): Sequence<List<Pair<Char, Int>>> {
    val perms = stringPermutations("ABCD").iterator()

    var amphipods = perms.next()

    var permutations = amphipods.map { amphipod ->
      permutations(amphipod)
    }

    var indices = IntArray(amphipods.length)

    return sequence {
      while (true) {
        yield(indices.mapIndexed { index, value -> permutations[index][value] }.flatten())

        var i = indices.lastIndex
        while (true) {
          indices[i] = (indices[i] + 1) % 4
          if (indices[i] > 0) {
            break
          }
          i -= 1
          if (i < 0) {
            if (perms.hasNext()) {
              amphipods = perms.next()
              indices.indices.forEach { indices[it] = 0 }
              permutations = amphipods.map { amphipod ->
                permutations(amphipod)
              }
              break
            } else {
              return@sequence
            }
          }
        }
      }
    }
  }

  private fun part1() {
    val grid = sample
//    println(grid)

    val solutionSpace = solutionSequence().count()
    println(solutionSpace)

    return


    return

    val costs = solutionSequence().map { solutionOrder ->
      val orderToSolve = solutionOrder.map { (amphipod, goalIndex) ->
        print("'$amphipod' to $goalIndex, ")
        amphipod to Companion.goalLocations[amphipod.uppercaseChar()]!![goalIndex]
      }

      println()

      val (solvedGrid, paths) = solve(orderToSolve, grid)
      // replayPaths(paths, grid, showEachMove = false)
      calculateCost(paths, grid) to paths
    }.toList()

    val cheapest = costs.minByOrNull { it.first }!!
    println(cheapest.first)
    replayPaths(cheapest.second, grid, showEachMove = false)
  }

  private fun stringPermutations(input: String): List<String> {
    if (input.length == 1) {
      return listOf(input)
    }

    return input.mapIndexed { index, c ->
      val perms = stringPermutations(
        input.substring(0 until index) + input.substring(index + 1)
      )
      perms.map { it -> c.toString() + it }
    }.flatten()
  }

  fun calculateCost(paths: List<List<Vector>>, grid: CharGrid): Int {
    val outputGrid = grid.copy()
    return paths.sumOf { path ->
      val amphipod = outputGrid[path.first()]
      outputGrid[path.last()] = outputGrid[path.first()]
      outputGrid[path.first()] = '.'
      val movementCost = Companion.movementCost[amphipod.uppercaseChar()]!!
      movementCost * (path.size - 1)  // Subtract the starting location
    }
  }

  fun solve(world: WorldState): WorldState {
    if (world.unmetGoals.isEmpty()) {
      return world
    }

    val currentGoal = world.unmetGoals.first()

    val path = getPath(currentGoal, world.grid)

    showPath(path, world.grid)

    getSolutions(path, world)

    TODO("Not finished")
  }

  private fun getSolutions(path: Path, world: WorldState) {
    val obstacles = getObstacles(path, world)
  }

  private fun getObstacles(path: Path, world: WorldState): List<Pair<Vector, Char>> {
    return path.locations.mapNotNull { location ->
      if (world.grid[location] in amphipods) {
        location to world.grid[location]
      } else {
        null
      }
    }
  }

  private fun getPath(goal: Goal, grid: CharGrid): Path {
    val start = whereIs(goal.amphipod, grid)
    val target = goal.target

    val movements = getPath(start, target)
    val cost = goal.amphipod.cost() * movements.size

    return Path(movements, goal.amphipod, cost)
  }

  fun solve(orderToSolve: List<Pair<Char, Vector>>, grid: CharGrid): Pair<CharGrid, List<List<Vector>>> {
    var currentGrid = grid.copy()
    val paths = mutableListOf<List<Vector>>()

    orderToSolve.indices.forEach { index ->
      (0..index).forEach {
        val (amphipod, goal) = orderToSolve[it]
        val start = whereIs(amphipod, currentGrid)
        if (start != goal) {
          val result = solveTask(start, goal, currentGrid)
          currentGrid = result.first
          if (result.second.isNotEmpty()) {
            paths.addAll(result.second)
          }
        }
      }
    }

    return currentGrid to paths
  }

  private fun solveTask(
    start: Vector,
    goal: Vector,
    grid: CharGrid
  ): Pair<CharGrid, List<List<Vector>>> {
    if (start == goal) {
      return grid to emptyList()
    }

    val path = getPath(start, goal)
    return executeMove(path, grid)
  }

  private fun permutations(amphipod: Char): List<List<Pair<Char, Int>>> {
    return listOf(
      listOf(
        amphipod to 0,
        amphipod.lowercaseChar() to 1,
      ),
      listOf(
        amphipod.lowercaseChar() to 1,
        amphipod to 0,
      ),
      listOf(
        amphipod to 1,
        amphipod.lowercaseChar() to 0,
      ),
      listOf(
        amphipod.lowercaseChar() to 0,
        amphipod to 1,
      )
    )
  }

  fun replayPaths(paths: List<List<Vector>>, grid: CharGrid, showEachMove: Boolean = true) {
    val outputGrid = grid.copy()
    paths.forEach { path ->
      if (showEachMove) {
        path.windowed(2, 1) { steps ->
          outputGrid[steps.last()] = outputGrid[steps.first()]
          outputGrid[steps.first()] = '.'
          println(
            outputGrid.toStringWithMultipleHighlights(
              COLORS.INVERTED_GREEN.toString() to { c, v -> v == steps.last() },
              COLORS.INVERTED_RED.toString() to { c, v -> v == steps.first() },
            )
          )
        }
      } else {
        outputGrid[path.last()] = outputGrid[path.first()]
        outputGrid[path.first()] = '.'
        println(
          outputGrid.toStringWithMultipleHighlights(
            COLORS.INVERTED_RED.toString() to { c, v -> v in path },
          )
        )
      }
    }
  }

  private fun whereIs(amphipod: Char, grid: CharGrid): Vector {
    return grid.findCharacter(amphipod)!!
  }

  private fun showPath(path: Path, grid: CharGrid) {
    println(
      grid.toStringWithMultipleHighlights(
        COLORS.LT_RED.toString() to { c, v -> v == path.locations.first() },
        COLORS.LT_GREEN.toString() to { c, v -> v == path.locations.last() },
        COLORS.INVERTED_GREEN.toString() to { c, v -> v in path.locations },
      )
    )
  }

  private fun showPath(path: List<Vector>, grid: CharGrid) {
    val pathGrid = grid.copy()
    path.forEach {
      pathGrid[it] = '*'
    }

    println(
      pathGrid.toStringWithMultipleHighlights(
        COLORS.LT_GREEN.toString() to { c, v -> c == 'o' },
        COLORS.LT_RED.toString() to { c, v -> c == '*' },
      )
    )
  }

  private fun executeMove(
    path: List<Vector>,
    inputGrid: CharGrid,
  ): Pair<CharGrid, List<List<Vector>>> {
    if (path.size <= 1) {
      // The goal and destination are the same!
      return inputGrid to listOf(path)
    }

    val amphipod = inputGrid[path.first()]

    val (clearedGrid, obstaclePaths) = clearPath(path, inputGrid)
    val newStart = whereIs(amphipod, clearedGrid)
    val newPath = getPath(newStart, path.last())

    val outputGrid = clearedGrid.copy().also { grid ->
      grid[newPath.last()] = grid[newPath.first()]
      grid[newPath.first()] = '.'
    }
    val paths = obstaclePaths.toMutableList().also { it.add(newPath) }

    return outputGrid to paths
  }

  fun getPath(start: Vector, target: Vector): List<Vector> {
    val queue: Queue<Vector> = LinkedList()
    queue.add(start)
    val seen = mutableSetOf<Vector>()
    val paths = mutableMapOf<Vector, Vector>()

    while (queue.isNotEmpty()) {
      val location = queue.remove()
      seen.add(location)

      if (location == target) {
        val path = mutableListOf<Vector>()
        path.add(location)
        while (path.last() != start) {
          path.add(paths[path.last()]!!)
        }
        return path.reversed()
      }

      emptyGrid.getNeighborsWithLocation(location).forEach { neighbor ->
        if (neighbor.second == '.') {
          val loc = neighbor.first
          if (loc !in seen && loc !in queue) {
            queue.add(loc)
            paths[loc] = location
          }
        }
      }
    }

    throw Exception("Path not found")
  }

  fun getObstacles(path: List<Vector>, grid: CharGrid): List<Pair<Vector, Char>> {
    return path.mapNotNull { location ->
      if (grid[location] in amphipods) {
        location to grid[location]
      } else {
        null
      }
    }
  }

  private fun part2() {
    val grid = sample
    println(grid)
    TODO("Not yet implemented")
  }

  private fun clearPath(path: List<Vector>, grid: CharGrid): Pair<CharGrid, List<List<Vector>>> {
    var outputGrid = grid.copy()
    val paths = mutableListOf<List<Vector>>()
    while (true) {
      val obstacles = getObstacles(path.drop(1), outputGrid).sortedByDescending { it.second }
      if (obstacles.isNotEmpty()) {
        val (newGrid, obstaclePath) = moveObstacleToFreeSpace(obstacles.first(), outputGrid, path)
        outputGrid = newGrid
        paths.addAll(obstaclePath)
      } else {
        break
      }
    }
    return outputGrid to paths
  }

  private fun moveObstacleToFreeSpace(
    obstacle: Pair<Vector, Char>,
    inputGrid: CharGrid,
    path: List<Vector>,
  ): Pair<CharGrid, List<List<Vector>>> {
    val start = obstacle.first
    val amphipod = obstacle.second

    val goalGrid = safeSpaces.copy().also { grid ->
      // Block any room except the room where the amphipod can go
      roomCells.forEach {
        grid[it] = 'X'
      }

      // Block the path cells
      path.forEach {
        grid[it] = '*'
      }
    }

    val obstaclePath = pathToClosestFreeCell(start, goalGrid)
    return executeMove(obstaclePath, inputGrid)
  }

  fun pathToClosestFreeCell(start: Vector, goalGrid: CharGrid): List<Vector> {
    val queue: Queue<Vector> = LinkedList()
    queue.add(start)
    val seen = mutableSetOf<Vector>()
    val paths = mutableMapOf<Vector, Vector>()

    while (queue.isNotEmpty()) {
      val location = queue.remove()
      seen.add(location)

      if (goalGrid[location] == '.') {
        val path = mutableListOf<Vector>()
        path.add(location)
        while (path.last() != start) {
          path.add(paths[path.last()]!!)
        }
        return path.reversed()
      }

      goalGrid.getNeighborsWithLocation(location).forEach { neighbor ->
        if (neighbor.second in listOf('*', '.', 'x')) {
          val loc = neighbor.first
          if (loc !in seen && loc !in queue) {
            queue.add(loc)
            paths[loc] = location
          }
        }
      }
    }

    throw Exception("Path not found")
  }

}

private fun Char.cost(): Int {
  return Day23.movementCost[this]!!
}

private fun <E> List<E>.toPair(): Pair<E, E> {
  return this[0] to this[1]
}

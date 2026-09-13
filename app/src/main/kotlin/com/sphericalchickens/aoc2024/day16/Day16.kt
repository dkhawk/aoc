package com.sphericalchickens.aoc2024.day16

import com.sphericalchickens.utils.*



import com.sphericalchickens.utils.Vector
import java.util.*

val testInput1 = """
    ###############
    #.......#....E#
    #.#.###.#.###.#
    #.....#.#...#.#
    #.###.#####.#.#
    #.#.#.......#.#
    #.#.#####.###.#
    #...........#.#
    ###.#.#####.#.#
    #...#.....#.#.#
    #.#.#.###.#.#.#
    #.....#...#.#.#
    #.###.#.#.#.#.#
    #S..#.....#...#
    ###############
""".trimIndent().lines()

val testInput2 = """
    ################
    #...#...#...#..E#
    #.#.#.#.#.#.#.#.#
    #.#.#.#...#...#.#
    #.#.#.#.###.#.#.#
    #...#.#.#.....#.#
    #.#.#.#.#.#####.#
    #.#...#.#.#.....#
    #.#.#####.#.###.#
    #.#.#.......#...#
    #.#.###.#####.###
    #.#.#...#.....#.#
    #.#.#.#####.###.#
    #.#.#.........#.#
    #.#.#.#########.#
    #S#.............#
    #################
""".trimIndent().lines()

fun main() {
    check(part1(testInput1) == 7036)
    check(part1(testInput2) == 11048)
    check(part2(testInput1) == 45)
    check(part2(testInput2) == 64)

    val input = readLines("inputs/16")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val grid = input.toGrid()
    val bounds = grid.getBounds()

    val start = (bounds.min.x + 1 to bounds.max.y - 1).toVector()
    val end = (bounds.max.x - 1 to bounds.min.y + 1).toVector()

    val initialState = State(
        location = start,
        heading = Heading.EAST
    )

    return solveMaze(grid, initialState, end)
}

data class State(val location: Vector, val heading: Heading)

data class StateWithCost(val location: Vector, val heading: Heading, val cost: Int) : Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost): Int {
        return cost.compareTo(other.cost)
    }
}

private fun State.toStateWithCost(cost: Int) = StateWithCost(location, heading, cost)

fun solveMaze(grid: Map<Vector, Char>, initialState: State, end: Vector): Int {
    val visited = mutableMapOf<State, Int>()

    visited[initialState] = 0
    val queue = PriorityQueue<StateWithCost>()
    var bestFinish = Int.MAX_VALUE
    queue.offer(initialState.toStateWithCost(0))

    fun add(stateWithCost: StateWithCost) {
        val (location, heading, cost) = stateWithCost

        // Only consider this location if it has the chance to be the best
        if (cost < bestFinish) {
            // Check if we have already been here
            val state = State(location, heading)
            val previousVisit = visited[state] ?: Int.MAX_VALUE
            if (cost < previousVisit) {
                visited[state] = cost
                queue.offer(stateWithCost)
            }
        }
    }

    while (queue.isNotEmpty()) {
        val (location, heading, cost) = queue.poll()

        val nextLocation = location.advance(heading)
        if (grid[nextLocation] != '#') {
            if (nextLocation == end) {
                if (cost < bestFinish) {
                    bestFinish = cost + 1 // +1 to account for the finial move to the finish
                }
                continue
            }

            val nextStateWithCost = StateWithCost(nextLocation, heading, cost + 1)
            add(nextStateWithCost)
        }

        add(StateWithCost(location, heading.turnRight(), cost + 1000))
        add(StateWithCost(location, heading.turnLeft(), cost + 1000))
    }

    return bestFinish
}

private fun part2(input: List<String>): Int {
    val grid = input.toGrid()
    val bounds = grid.getBounds()

    val start = (bounds.min.x + 1 to bounds.max.y - 1).toVector()
    val end = (bounds.max.x - 1 to bounds.min.y + 1).toVector()

    val initialState = State(
        location = start,
        heading = Heading.EAST
    )

    return solveMaze2(grid, initialState, end)
}

data class StateFull(
    val location: Vector,
    val heading: Heading,
    val cost: Int,
    val visited: Set<Vector>
) {
    fun toState() = State(location, heading)
}

data class CostWithVisited(val cost: Int, val visited: Set<Vector>)

fun solveMaze2(grid: Map<Vector, Char>, initialState: State, end: Vector): Int {
    val visited = mutableMapOf<State, StateFull>()

    val queue = PriorityQueue<StateWithCost>()
    var bestFinish = Int.MAX_VALUE
    queue.offer(initialState.toStateWithCost(0))

    fun tryAdd(stateFull: StateFull) {
        val state = stateFull.toState()
        val previous = visited[state]
        if (previous == null) {
            // First time!
            visited[state] = stateFull
            queue.offer(StateWithCost(state.location, state.heading, stateFull.cost))
            return
        }

        if (previous.cost < stateFull.cost) {
            // Ignore more expensive ways of getting here
            return
        }

        if (stateFull.cost < previous.cost) {
            // This way is cheaper!!
            visited[state] = stateFull
            queue.offer(StateWithCost(state.location, state.heading, stateFull.cost))
            return
        }

        // We found another path to get here that is the same cost
        val newStateFull = stateFull.copy(
            visited = previous.visited + stateFull.visited
        )
        visited[state] = newStateFull
    }

    val stateFull = StateFull(
        location = initialState.location,
        heading = Heading.EAST,
        cost = 0,
        visited = setOf(initialState.location)
    )

    tryAdd(stateFull)

    val endVisitedLocations = mutableSetOf(end)

    while (queue.isNotEmpty()) {
        val (location, heading, cost) = queue.poll()

        val visitedLocations = visited.getValue(State(location, heading)).visited

        val nextLocation = location.advance(heading)
        if (grid[nextLocation] != '#') {
            if (nextLocation == end) {
                val trueCost = cost + 1

                if (trueCost == bestFinish) {
                    endVisitedLocations.addAll(visitedLocations)
                    endVisitedLocations.add(end)
                } else if (trueCost < bestFinish) {
                    bestFinish = trueCost
                    endVisitedLocations.clear()
                    endVisitedLocations.addAll(visitedLocations)
                    endVisitedLocations.add(end)
                }

                continue
            }

            val stateFull = StateFull(
                location = nextLocation,
                heading = heading,
                cost = cost + 1,
                visited = visitedLocations + nextLocation
            )

            tryAdd(stateFull)
        }

        tryAdd(
            StateFull(location = location, heading = heading.turnRight(), cost = cost + 1000, visited = visitedLocations)
        )
        tryAdd(
            StateFull(location = location, heading = heading.turnLeft(), cost = cost + 1000, visited = visitedLocations)
        )
    }

//    bestFinish.println()
//    val visitedLocations =  visited.keys.filter { it.location == end }.mapNotNull { state ->
//        visited[state]?.visited
//    }.flatten().toSet()
//
//    visitedLocations.println()
//    endVisitedLocations.println()
//    endVisitedLocations.count().println()

    return endVisitedLocations.count()
}

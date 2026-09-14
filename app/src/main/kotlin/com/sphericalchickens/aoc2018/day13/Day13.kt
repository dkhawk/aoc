package com.sphericalchickens.aoc2018.day13

import com.sphericalchickens.utils.*
import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration


import com.sphericalchickens.aoc2018.day13.Day.Cart.Companion.cartSymbolToHeading
import com.sphericalchickens.aoc2018.day13.Day.Cart.Companion.cartSymbols
import kotlinx.coroutines.CoroutineScope

class Day(private val scope: CoroutineScope) {
  var useRealData = (false)
  private lateinit var input: List<String>

  var running = (false)
  var delayTime = ( 500L)
  val maxDelay = 500L

  val sampleInput = """
    /->-\        
    |   |  /----\
    | /-+--+-\  |
    | | |  | v  |
    \-+-/  \-+--/
      \------/   
  """.trimIndent().split("\n")

  val sampleInput2 = """
    />-<\  
    |   |  
    | /<+-\
    | | | v
    \>+</ |
      |   ^
      \<->/
  """.trimIndent().split("\n")

  fun initialize() {
    input = if (useRealData) {
      val (year, day) = packageToYearDay(this.javaClass.packageName)
      val realInput = InputNew(year, day).readAsLines()
      realInput
    } else {
      sampleInput2
    }
  }

  enum class TURN {
    LEFT, STRAIGHT, RIGHT;

    fun advance(heading: Heading): Heading {
      return when (this) {
        LEFT -> heading.turnLeft()
        STRAIGHT -> heading
        RIGHT -> heading.turnRight()
      }
    }

    fun next(): TURN {
      return when(this) {
        LEFT -> STRAIGHT
        STRAIGHT -> RIGHT
        RIGHT -> LEFT
      }
    }
  }

  data class Cart(val location: Vector, val heading: Heading, val nextTurn: TURN = TURN.LEFT) {
    fun move(grid: NewGrid<Char>) : Cart {
      val newLocation = location.advance(heading)

      var newHeading = heading
      var newNextTurn = this.nextTurn

      when (grid[newLocation]) {
        '\\' -> {
          newHeading = when (heading) {
            Heading.EAST -> Heading.SOUTH
            Heading.WEST -> Heading.NORTH
            Heading.NORTH -> Heading.WEST
            Heading.SOUTH -> Heading.EAST
          }
        }
        '/' -> {
          newHeading = when (heading) {
            Heading.EAST -> Heading.NORTH
            Heading.WEST -> Heading.SOUTH
            Heading.NORTH -> Heading.EAST
            Heading.SOUTH -> Heading.WEST
          }
        }
        '+' -> {
          newHeading = nextTurn.advance(heading)
          newNextTurn = nextTurn.next()
        }
        else -> {
        }
      }

      return Cart(newLocation, newHeading, newNextTurn)
    }

    companion object {
      val cartSymbolToHeading = mapOf(
        '^' to Heading.NORTH,
        '>' to Heading.EAST,
        'v' to Heading.SOUTH,
        '<' to Heading.WEST
      )

      val cartSymbols = cartSymbolToHeading.keys
    }
  }


  private val cartHeadingToDirectionSymbol = mapOf(
    Heading.NORTH to '|',
    Heading.SOUTH to '|',
    Heading.EAST to '-',
    Heading.WEST to '-',
  )

  fun part1() {
    val world = input.flatMapIndexed { row, line ->
      line.mapIndexed { col, char ->
        Vector(col, row) to char
      }
    }.toMap().toMutableMap()


    val cartLocations = world.filter { it.value in cartSymbols }

    val carts = cartLocations.map { (location, headingSymbol) ->
      Cart(location, cartSymbolToHeading.getValue(headingSymbol), nextTurn = TURN.LEFT)
    }.associateBy { it.location }.toMutableMap()

    val grid = world.toGrid()

    // Let's fix the grid to fix the cart origins
    carts.forEach { (location, cart) ->
      grid[location] = cartHeadingToDirectionSymbol.getValue(cart.heading)
    }

    var crashLocation: Vector? = null
    var ticks = 0
    do {
      val cartsToMove = carts.keys.sortedWith(compareBy({ it.y }, { it.x }))

      cartsToMove.forEach { location ->
        if (crashLocation == null) {
          val cart = carts.getValue(location).move(grid)
          carts.remove(location)
          if (carts[cart.location] == null) {
            carts[cart.location] = cart
          } else {
            crashLocation = cart.location
          }
        }
      }

      ticks += 1

    } while (crashLocation == null)

    println(crashLocation)
  }

  private fun printWorld(grid: NewGrid<Char>, carts: Iterable<Cart>, crashLocation: Vector?) {
    val g = grid.makeCopy()
    carts.forEach { cart ->
      g[cart.location] = cart.heading.toSymbol()
    }

    if (crashLocation != null) g[crashLocation] = 'X'

    println(g.toStringWithHighlights { c, _ -> c in cartSymbols })
  }

  fun part2() {
    val world = input.flatMapIndexed { row, line ->
      line.mapIndexed { col, char ->
        Vector(col, row) to char
      }
    }.toMap().toMutableMap()


    val cartLocations = world.filter { it.value in cartSymbols }

    val carts = cartLocations.map { (location, headingSymbol) ->
      Cart(location, cartSymbolToHeading.getValue(headingSymbol), nextTurn = TURN.LEFT)
    }.associateBy { it.location }.toMutableMap()

    val grid = world.toGrid()

    // Let's fix the grid to fix the cart origins
    carts.forEach { (location, cart) ->
      grid[location] = cartHeadingToDirectionSymbol.getValue(cart.heading)
    }

    var ticks = 0
    do {
      val cartsToMove = carts.keys.sortedWith(compareBy({ it.y }, { it.x }))

      cartsToMove.forEach { location ->
          carts[location]?.let { oldCart ->
            val cart = oldCart.move(grid)
            carts.remove(oldCart.location)
            if (carts[cart.location] == null) {
              carts[cart.location] = cart
            } else {
              carts.remove(cart.location)
            }
          }
      }

      ticks += 1

    } while (carts.size > 1)

    println(carts.map { it.key })
  }
}

private fun Heading.toSymbol(): Char {
  return when (this) {
    Heading.NORTH -> '^'
    Heading.EAST -> '>'
    Heading.SOUTH -> 'v'
    Heading.WEST -> '<'
  }
}

private fun MutableMap<Vector, Char>.toGrid(): NewGrid<Char> {
  val (min, max) = this.keys.minMax()

  return NewGrid(max.x + 1, max.y + 1, '.').also { grid ->
    forEach { (location, value) ->
      grid[location] = value
    }
  }
}

private fun MutableSet<Vector>.minMax(): Pair<Vector, Vector> {
  val a = this.unzip().toList().map {
    it.minMax()
  }

  return Vector(a[0].first, a[1].first) to Vector(a[0].second, a[1].second)
}

private fun List<Int>.minMax(): Pair<Int, Int> {
  return this.minOrNull()!! to this.maxOrNull()!!
}


fun main() {
    println("--- Advent of Code 2018, Day 13 ---")
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

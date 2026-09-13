package com.sphericalchickens.aoc2019.day15

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.io.File
import java.util.*

class Day15 {
    companion object {
        val inputProgram = File("/Users/dkhawk/Downloads/2019/input-15.txt").readText().trim().split(",").map(String::toLong)

        fun test() {
            Day15().part1(inputProgram)
        }

        val NORTH = 1
        val SOUTH = 2
        val WEST = 3
        val EAST = 4
    }

    class Location(var x: Int, var y: Int)

    private val width = 10000

    class Grid(private val width: Int) {
        fun update(direction: Int, result: Int) {
            when(result) {
                0 -> {
                    val index = when(direction) {
                        NORTH -> { boundary.yMin--; ((robot.y - 1) * width) + robot.x }
                        SOUTH -> { boundary.yMax++; ((robot.y + 1) * width) + robot.x }
                        EAST -> { boundary.xMax++; (robot.y * width) + robot.x + 1 }
                        WEST -> { boundary.xMin--; (robot.y * width) + robot.x - 1 }
                        else -> throw RuntimeException("Illegal direction: $direction")
                    }
                    grid[index] = '#'
                }
                2,1 -> {
                    val index = (robot.y * width) + robot.x
                    grid[index] = if (result == 1) '.' else '+'
                    when(direction) {
                        NORTH -> { boundary.yMin--; robot.y-- }
                        SOUTH -> { boundary.yMax++; robot.y++ }
                        EAST -> { boundary.xMax++; robot.x++ }
                        WEST -> { boundary.xMin--; robot.x-- }
                        else -> throw RuntimeException("Illegal direction: $direction")
                    }
                    val rindex = robot.y * width + robot.x
                    grid[rindex] = '@'
                }
                else ->  throw RuntimeException("Illegal state update: $result")
            }
        }

        val height = width
        val grid = CharArray(width * height)
        val robot = Location(width / 2, height / 2)
        val boundary = Boundary(robot.x + 1, robot.y + 1, robot.x - 1, robot.y -1)

        init {
            grid.fill(' ')
            val rindex = robot.y * width + robot.x
            grid[rindex] = '@'
        }
    }

    class Boundary(var xMin: Int, var yMin: Int, var xMax: Int, var yMax: Int)

    /*
    north (1), south (2), west (3), and east (4)
     */

    /*
    0: The repair droid hit a wall. Its position has not changed.
1: The repair droid has moved one step in the requested direction.
2: The repair droid has moved one step in the requested direction; its new position is the location of the oxygen system.
     */

    val grid = Grid(width)

    private fun part1(inputProgram: List<Long>) {
        val inputQueue = ArrayDeque<Long>()
        val outputQueue = ArrayDeque<Long>()
        val computer = Computer(inputProgram, inputQueue, outputQueue)

        inputQueue.offer(NORTH.toLong())
        computer.execute()
        grid.update(NORTH, outputQueue.remove().toInt())
        drawGrid(grid)

        inputQueue.offer(SOUTH.toLong())
        computer.execute()
        grid.update(SOUTH, outputQueue.remove().toInt())
        drawGrid(grid)

        inputQueue.offer(EAST.toLong())
        computer.execute()
        grid.update(EAST, outputQueue.remove().toInt())
        drawGrid(grid)

        repeat(10) {
            inputQueue.offer(WEST.toLong())
            computer.execute()
            grid.update(WEST, outputQueue.remove().toInt())
            drawGrid(grid)
        }
    }

    private fun drawGrid(grid: Grid) {
        println("vvvvvvvvvvvvvvvvvvvvv")
        for (row in (grid.boundary.yMin - 1)..(grid.boundary.yMax + 1)) {
            val index = row * width
            val min = index + (grid.boundary.xMin - 1)
            val max = index + (grid.boundary.xMax + 1)
            println("|${grid.grid.slice(min..max).joinToString("")}|")
        }
        println("^^^^^^^^^^^^^^^^^^^^^")
    }
}

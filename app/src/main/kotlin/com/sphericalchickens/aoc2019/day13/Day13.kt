package com.sphericalchickens.aoc2019.day13

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.io.File
import java.lang.Integer.signum
import java.util.*
import kotlin.collections.HashMap

class Day13 {
    companion object {
        var testOutput = "1,2,3,6,5,4".split(",").map(String::toLong)

        val inputProgram = File("/Users/dkhawk/Downloads/2019/input-13.txt").readText().trim().split(",").map(String::toLong)

        fun test() {
            Day13().part2(inputProgram)
        }
    }

    /*
    0 is an empty tile. No game object appears in this tile.
1 is a wall tile. Walls are indestructible barriers.
2 is a block tile. Blocks can be broken by the ball.
3 is a horizontal paddle tile. The paddle is indestructible.
4 is a ball tile. The ball moves diagonally and bounces off objects.
     */

    data class Location(val x: Int, val y: Int)

    private fun part1(input: List<Long>) {
        var screen = HashMap<Location, Int>()
        val bufferIn = ArrayDeque<Long>()
        val bufferOut = ArrayDeque<Long>()
//        bufferIn.offer(2)
        val computer = Computer(input, bufferIn, bufferOut)
        computer.execute()
//        println(bufferOut.joinToString())
        bufferOut.map(Long::toInt).windowed(3,3).forEach {
            screen[Location(it[0], it[1])] = it[2]
        }

        println(screen.filterValues{ it == 2}.size)
    }

    /*
    If the joystick is in the neutral position, provide 0.
If the joystick is tilted to the left, provide -1.
If the joystick is tilted to the right, provide 1.
     */

    private fun part2(input: List<Long>) {
        val screen = HashMap<Location, Int>()
        var score = 0
        var paddle = Location(0, 0)
        var ball = Location(0, 0)

        val bufferIn = ArrayDeque<Long>()
        val bufferOut = ArrayDeque<Long>()
//        bufferIn.offer(2)
        val computer = Computer(input, bufferIn, bufferOut)
        // play for free
        computer.data[0] = 2

        var iteration = 0
        while (computer.state != Computer.DONE) {
            computer.execute()
            // println(bufferOut.joinToString())
            bufferOut.map(Long::toInt).windowed(3, 3).forEach {
                if (it[0] == -1 && it[1] == 0) {
                    score = it[2]
                } else {
                    val location = Location(it[0], it[1])
                    screen[location] = it[2]
                    if (it[2] == 3) {
                        paddle = location
                    } else if (it[2] == 4) {
                        ball = location
                    }
                }
            }


//            if (iteration % 10 == 0) {
//                println("===================================")
//                println(score)
//                drawScreen(screen)
//            }

            val joy = signum(ball.x - paddle.x)
            bufferIn.offer(joy.toLong())
        }
        println(score)
    }

    private fun drawScreen(screen: HashMap<Location, Int>) {
        val xMax = screen.keys.maxBy { it.x }!!.x
        val yMax = screen.keys.maxBy { it.y }!!.y
        val xMin = screen.keys.minBy { it.x }!!.x
        val yMin = screen.keys.minBy { it.y }!!.y

        val width = xMax - xMin + 1
        val height = yMax - yMin + 1

        val grid = CharArray(width * height)
        screen.forEach { (location, type) ->
            val index = location.x + location.y * width
            grid[index] = when(type) {
                1 -> '#'
                2 -> '*'
                3 -> '='
                4 -> 'o'
                else -> ' '
            }
        }
        grid.toList().windowed(width, width).forEach { line ->
            println(line.joinToString(""))
        }
    }
}

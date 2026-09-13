package com.sphericalchickens.aoc2019.day10

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.util.*
import kotlin.collections.ArrayList

class Day10 {
    companion object {
        val in1 = """
            .#..#
            .....
            #####
            ....#
            ...##
        """.trimIndent().split("\n").map(String::trim)

        val in2 = """
            .#...
            .....
            ..#..
            .....
            ...#.
        """.trimIndent().split("\n").map(String::trim)

        val in3 = """
            ......#.#.
            #..#.#....
            ..#######.
            .#.#.###..
            .#..#.....
            ..#....#.#
            #..#....#.
            .##.#..###
            ##...#..#.
            .#....####
        """.trimIndent().split("\n").map(String::trim)

        val in7 = """
            .#..##.###...#######
            ##.############..##.
            .#.######.########.#
            .###.#######.####.#.
            #####.##.#.##.###.##
            ..#####..#.#########
            ####################
            #.####....###.#.#.##
            ##.#################
            #####.##.###..####..
            ..######..##.#######
            ####.##.####...##..#
            .#####..#.######.###
            ##...#.##########...
            #.##########.#######
            .####.#.###.###.#.##
            ....##.##.###..#####
            .#.#.###########.###
            #.#.#.#####.####.###
            ###.##.####.##.#..##
        """.trimIndent().split("\n").map(String::trim)

        val realInput = """
            #.#....#.#......#.....#......####.
            #....#....##...#..#..##....#.##..#
            #.#..#....#..#....##...###......##
            ...........##..##..##.####.#......
            ...##..##....##.#.....#.##....#..#
            ..##.....#..#.......#.#.........##
            ...###..##.###.#..................
            .##...###.#.#.......#.#...##..#.#.
            ...#...##....#....##.#.....#...#.#
            ..##........#.#...#..#...##...##..
            ..#.##.......#..#......#.....##..#
            ....###..#..#...###...#.###...#.##
            ..#........#....#.....##.....#.#.#
            ...#....#.....#..#...###........#.
            .##...#........#.#...#...##.......
            .#....#.#.#.#.....#...........#...
            .......###.##...#..#.#....#..##..#
            #..#..###.#.......##....##.#..#...
            ..##...#.#.#........##..#..#.#..#.
            .#.##..#.......#.#.#.........##.##
            ...#.#.....#.#....###.#.........#.
            .#..#.##...#......#......#..##....
            .##....#.#......##...#....#.##..#.
            #..#..#..#...........#......##...#
            #....##...#......#.###.#..#.#...#.
            #......#.#.#.#....###..##.##...##.
            ......#.......#.#.#.#...#...##....
            ....##..#.....#.......#....#...#..
            .#........#....#...#.#..#....#....
            .#.##.##..##.#.#####..........##..
            ..####...##.#.....##.............#
            ....##......#.#..#....###....##...
            ......#..#.#####.#................
            .#....#.#..#.###....##.......##.#.
        """.trimIndent().split("\n").map(String::trim)

        fun test() {
            Day10().part2(realInput)
        }
    }

    var width = 0
    var height = 0

    data class Location(val x: Int, val y: Int)

    val astroMap = ArrayList<Location>()

    private fun part1(input: List<String>) {
        buildAstroMap(input)

        var max = Int.MIN_VALUE
        var mindex = -1
        astroMap.forEachIndexed { index, astroid ->
            val n = findLineOfSightAstroids(astroid)
            if (n > max) {
                max = n
                mindex = index
            }
        }

        println("$mindex(${astroMap[mindex]}) -> $max")
    }

    private fun part2(input: List<String>) {
        buildAstroMap(input)
        val asteroid = astroMap[214]
        val groups = findAstroAngles(asteroid)

        val sortedGroups = TreeMap<Double, ArrayDeque<Location>>()

        groups.forEach { (key, value) ->
            var angle = key
            while (angle < 0) {
                angle += Math.PI * 2
            }
            sortedGroups[angle] = ArrayDeque(value.map { it.second }.sortedBy { dist(asteroid, it) })
        }

        println(sortedGroups.size)
        println(sortedGroups)

        val destroyedAstros = ArrayList<Location>()

        while (sortedGroups.isNotEmpty()) {
            val emptyAngles = ArrayList<Double>()
            sortedGroups.forEach { key, value ->
                destroyedAstros.add(value.removeFirst())
                // Could stop at 200...
                if (value.isEmpty()) {
                    emptyAngles.add(key)
                }
            }
            emptyAngles.forEach { sortedGroups.remove(it) }
        }
        println(destroyedAstros)
        destroyedAstros[199].let {
            println(it)
            println("Answer: ${it.x * 100 + it.y}")
        }
    }

    private fun dist(asteroid: Location, it: Location): Int {
        val dx = (asteroid.x - it.x)
        val dy = (asteroid.y - it.y)
        return (dx * dx) + (dy * dy)
    }

    private fun buildAstroMap(input: List<String>) {
        width = input[0].length
        height = input.size

        input.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == '#') {
                    astroMap.add(Location(colIndex, rowIndex))
                }
            }
        }
    }

    fun findLineOfSightAstroids(astroid: Location): Int {
        val groups = findAstroAngles(astroid)
        return groups.size
    }

    private fun findAstroAngles(astroid: Location): Map<Double, List<Pair<Double, Location>>> {
        val anglesToAstros = ArrayList<Pair<Double, Location>>()

        astroMap.forEach { other ->
            if (other != astroid) {
                val xdiff = other.x - astroid.x
                val ydiff = other.y - astroid.y
    //                val angle = Math.atan2(ydiff.toDouble(), xdiff.toDouble())
                val angle = Math.atan2(xdiff.toDouble(), -ydiff.toDouble())
                anglesToAstros.add(Pair(angle, other))
            }
        }

        //        anglesToAstros.sortBy { it.first }
        val groups = anglesToAstros.groupBy { it.first }
        return groups
    }
}

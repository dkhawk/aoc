package com.sphericalchickens.aoc2024.day14

import com.sphericalchickens.utils.*



import java.lang.Math.floorMod
import kotlin.math.sign

val testInput = """
    p=0,4 v=3,-3
    p=6,3 v=-1,-3
    p=10,3 v=-1,2
    p=2,0 v=2,-1
    p=0,0 v=1,3
    p=3,0 v=-2,-2
    p=7,6 v=-1,-3
    p=3,0 v=-1,-2
    p=9,3 v=2,3
    p=7,3 v=-1,2
    p=2,4 v=2,-3
    p=9,5 v=-3,-3
""".trimIndent().lines()

fun main() {
    val testAreaSize = Vector(11, 7)
    check(part1(testInput, testAreaSize) == 12L)

    val input = readLines("inputs/14")

    val realAreaSize = Vector(101, 103)
    val (p1, d1) = kotlin.time.measureTimedValue { part1(input, realAreaSize) }
    p1.println()
    println("Part 1 runtime: ${formatDuration(d1)}")
    val (p2, d2) = kotlin.time.measureTimedValue { part2(input, realAreaSize) }
    p2.println()
    println("Part 2 runtime: ${formatDuration(d2)}")
}

data class Robot(
    val location: VectorLong,
    val velocity: VectorLong,
)

private fun Robot.boundLocation(areaSize: Vector) = copy(
    location = VectorLong(
        floorMod(location.x, areaSize.x).toLong(),
        floorMod(location.y, areaSize.y).toLong(),
    )
)

private enum class Quadrant(val sign: Vector) {
    NORTHWEST(Vector(-1, -1)), NORTHEAST(Vector(1, -1)),
    SOUTHWEST(Vector(-1, 1)),  SOUTHEAST(Vector(1, 1));
}

fun part1(input: List<String>, areaSize: Vector): Long {
    val robots = input.map { it.toRobot() }

    val seconds = 100L

    val futureBots = robots.map { robot ->
        robot.copy(location = robot.location + robot.velocity * seconds)
    }.map { robot ->
        robot.boundLocation(areaSize)
    }

    val center =
        VectorLong(
            ((areaSize.x / 2)).toLong(),
            ((areaSize.y / 2)).toLong(),
        )

    val shifted = futureBots.map { robot ->
        robot.location - center
    }

    return shifted.mapNotNull { location ->
        location.toQuadrant()
    }.groupingBy { it }.eachCount().values.fold(1L) { a, b -> a * b}
}

fun part2(input: List<String>, areaSize: Vector): Long {
    val robots = input.map { it.toRobot() }

    val variancesBySecond = List(1000) { secondsInt ->
        val seconds = secondsInt.toLong()

        val futureBots = robots.advance(seconds, areaSize)

        val xv = variance(futureBots.map { robot -> robot.location.x })
        val yv = variance(futureBots.map { robot -> robot.location.y })
        secondsInt to (xv to yv)
    }

    val (xPeriod, yPeriod) = findPeriods(variancesBySecond)

    fun createSource(period: Period) = sequence {
        var seconds = period.origin
        yield(seconds)

        while (true) {
            seconds += period.period
            yield(seconds)
        }
    }.asIterable().iterator()

    val xSource = createSource(xPeriod)
    val ySource = createSource(yPeriod)

    var maxTries = 1000000

    var x = xSource.next()
    var y = ySource.next()

    while (maxTries > 0 && x != y) {
        maxTries--

        if (x < y) x = xSource.next()
        if (y < x) y = ySource.next()

        if (x == y) {
            val bots = robots.advance(x.toLong(), areaSize)

            // Find the clusters...
            val clusters = createClusters(bots)

            val topClusters = clusters.sortedByDescending { it.size }.take(2)

            printBots(bots, areaSize) { location, char ->
                if (topClusters.first().contains(location)) {
                    "${COLORS.GREEN}*${COLORS.NONE}"
                } else if (topClusters[1].contains(location)) {
                    "${COLORS.RED}*${COLORS.NONE}"
                } else {
                    "$char"
                }
            }
            return x.toLong()
        }
    }

    return -1
}

fun createClusters(bots: List<Robot>): List<Set<Vector>> {
    val unclusteredBots = bots.map { robot -> robot.location.toVector() }.toMutableSet()

    val grid = bots.associate { it.location.toVector() to '*' }.withDefault { '.' }

    return buildList {
        while (unclusteredBots.isNotEmpty()) {
            val firstBot = unclusteredBots.first()
            unclusteredBots.remove(firstBot)

            val queue = ArrayDeque(listOf(firstBot))
            val cluster = mutableSetOf<Vector>()
            val visited = mutableSetOf(firstBot)

            while (queue.isNotEmpty()) {
                val next = queue.removeFirst()
                cluster.add(next)

                next.allNeighbors(grid)
                    .filterNot { visited.contains(it.first) }
                    .filter { it.second == '*' }
                    .forEach { neighbor ->
                        visited.add(neighbor.first)
                        queue.add(neighbor.first)
                    }
            }

            add(cluster)
            unclusteredBots.removeAll(cluster)
        }

    }
}

private fun Vector.allNeighbors(grid: Map<Vector, Char>): List<Pair<Vector, Char>> {
    return Heading.entries.map {heading ->
        val loc = this.advance(heading)
        loc to grid.getValue(loc)
    }
}

private fun List<Robot>.advance(seconds: Long, areaSize: Vector): List<Robot> {
    return this.map { robot ->
        robot.copy(location = robot.location + robot.velocity * seconds)
    }.map { robot ->
        robot.boundLocation(areaSize)
    }
}

data class Period(
    val origin: Int,
    val period: Int,
)

fun findPeriods(variances: List<Pair<Int, Pair<Double, Double>>>): Pair<Period, Period> {
    val xVariances = variances.map { it.first to it.second.first }
    val yVariances = variances.map { it.first to it.second.second }

    var xOrigin: Int
    var yOrigin: Int

    val xPeriods = xVariances.sortedBy { it.second }.take(5).sortedBy { it.first }.also { xOrigin = it.first().first }.windowed(2, 1).map { it[1].first - it[0].first }
    val yPeriods = yVariances.sortedBy { it.second }.take(5).sortedBy { it.first }.also { yOrigin = it.first().first }.windowed(2, 1).map { it[1].first - it[0].first }

    check(xPeriods.all { it == xPeriods.first() })
    check(yPeriods.all { it == yPeriods.first() })

    return Period(origin = xOrigin, period = xPeriods.first()) to Period(origin = yOrigin, period = yPeriods.first())
}

fun variance(numbers: List<Long>): Double {
    val mean = numbers.average()
    return numbers.map { number ->
        val d = (number - mean)
        d * d
    }.average()
}

private fun printBots(robots: List<Robot>, areaSize: Vector, block: (Vector, Char) -> String) {
    val grid = robots.associate { it.location.toVector() to '*' }.withDefault { '.' }

    val bounds = Bounds(
        Vector(0, 0),
        areaSize
    )

    grid.printGrid(bounds, formatter = block)
}

private fun VectorLong.toQuadrant(): Quadrant? {
    val sign = Vector(this.x.sign, this.y.sign)

    return Quadrant.entries.firstOrNull { it.sign == sign }
}

private fun String.toRobot(): Robot {
    return parseRobotLine(this)
}

val robotRegex = """p=(?<px>-?[0-9]*),(?<py>-?[0-9]*) v=(?<vx>-?[0-9]*),(?<vy>-?[0-9]*)""".trimMargin().toRegex()

private fun parseRobotLine(line: String): Robot {
    return robotRegex.find(line)!!.groups.let { groups ->
        Robot(
            location = VectorLong(groups["px"]!!.value.toLong(), groups["py"]!!.value.toLong()),
            velocity = VectorLong(groups["vx"]!!.value.toLong(), groups["vy"]!!.value.toLong())
        )
    }
}

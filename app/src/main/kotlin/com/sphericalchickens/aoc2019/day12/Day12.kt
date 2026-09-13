package com.sphericalchickens.aoc2019.day12

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import kotlin.math.sign
import kotlin.math.abs

class Day12 {
    companion object {
        fun test() {
            Day12().part2()
        }
    }

    data class Vector(val x: Int = 0, val y: Int = 0, val z: Int = 0) {
        operator fun plus(other: Vector): Vector {
            return Vector(x + other.x, y + other.y, z + other.z)
        }

        override fun toString(): String {
            return "$x, $y, $z"
        }
    }

    class Moon(val name: String, var location: Vector) {
        var velocity = Vector()
        var acceleration = Vector()
    }

    /*
    val moons = listOf(
        Moon("Io", Vector(x=-1, y=0, z=2)),
        Moon("Europa", Vector(x=2, y=-10, z=-7)),
        Moon("Ganymede", Vector(x=4, y=-8, z=8)),
        Moon("Callisto", Vector(x=3, y=5, z=-1))
    )

    val initialStates = listOf(
        Moon("Io", Vector(x=-1, y=0, z=2)),
        Moon("Europa", Vector(x=2, y=-10, z=-7)),
        Moon("Ganymede", Vector(x=4, y=-8, z=8)),
        Moon("Callisto", Vector(x=3, y=5, z=-1))
    )
    */
/*
    val moons = listOf(
        Moon("Io", Vector(x=-8, y=-10, z=0)),
        Moon("Europa", Vector(x=5, y=5, z=10)),
        Moon("Ganymede", Vector(x=2, y=-7, z=3)),
        Moon("Callisto", Vector(x=9, y=-8, z=-3))
    )
    val initialStates = listOf(
        Moon("Io", Vector(x=-8, y=-10, z=0)),
        Moon("Europa", Vector(x=5, y=5, z=10)),
        Moon("Ganymede", Vector(x=2, y=-7, z=3)),
        Moon("Callisto", Vector(x=9, y=-8, z=-3))
    )*/

    val moons = listOf(
        Moon("Io", Vector(x=7, y=10, z=17)),
        Moon("Europa", Vector(x=-2, y=7, z=0)),
        Moon("Ganymede", Vector(x=12, y=5, z=12)),
        Moon("Callisto", Vector(x=5, y=-8, z=6))
    )

    private fun part1() {
        repeat(10) {
            moons.forEach { moon ->
                moons.forEach { other ->
                    if (moon != other) {
                        val d = direction(moon, other)
                        moon.velocity = moon.velocity + d
                    }
                }
            }

            moons.forEach { moon ->
                moon.location = moon.location + moon.velocity
            }
        }

        val energy = moons.sumBy { moon ->
            potEnergy(moon) * kinEnergy(moon)
        }
        println("Total energy: $energy")
    }

    private fun part2() {
//        val seenXStates = HashSet<Set<Pair<Int, Int>>>()
        val xStates0 = moons.map {moon ->
            Pair(moon.location.x, moon.velocity.x)
        } //.toSet()
        val yStates0 = moons.map {moon ->
            Pair(moon.location.y, moon.velocity.y)
        } //.toSet()
        val zStates0 = moons.map {moon ->
            Pair(moon.location.z, moon.velocity.z)
        } //.toSet()

        var xperiod = -1
        var yperiod = -1
        var zperiod = -1

        for (it in 0..3000000) {
            val xStates = moons.map {moon ->
                    Pair(moon.location.x, moon.velocity.x)
            } // .toSet()
            if (xStates0 == xStates) {
                println("$it: Back to initial X state: $xStates}")
                if (it > 0 && xperiod < 0) {
                    xperiod = it
                }
            }

            val yStates = moons.map {moon ->
                Pair(moon.location.y, moon.velocity.y)
            } // .toSet()
            if (yStates0 == yStates) {
                println("$it: Back to initial Y state: $yStates}")
                if (it > 0 && yperiod < 0) {
                    yperiod = it
                }
            }

            val zStates = moons.map {moon ->
                Pair(moon.location.z, moon.velocity.z)
            } // .toSet()
            if (zStates0 == zStates) {
                println("$it: Back to initial Z state: $zStates}")
                if (it > 0 && zperiod < 0) {
                    zperiod = it
                }
            }

            if (xperiod > 0 && yperiod > 0 && zperiod > 0) {
                break
            }

            moons.forEach { moon ->
                moons.forEach { other ->
                    if (moon != other) {
                        val d = direction(moon, other)
                        moon.velocity = moon.velocity + d
                    }
                }
            }

            moons.forEach { moon ->
                moon.location = moon.location + moon.velocity
            }
        }

        println("$xperiod, $yperiod, $zperiod")

        val energy = moons.sumBy { moon ->
            potEnergy(moon) * kinEnergy(moon)
        }
        println("Total energy: $energy")
    }

    private fun statesMatch(moon: Moon, other: Moon): Boolean {
        return moon.location == other.location && moon.velocity == other.velocity
    }

    private fun kinEnergy(moon: Day12.Moon): Int {
        return with (moon.location) {
            abs(x) + abs(y) + abs(z)
        }
    }

    private fun potEnergy(moon: Day12.Moon): Int {
        return with (moon.velocity) {
            abs(x) + abs(y) + abs(z)
        }
    }

    private fun dumpMoons() {
        moons.forEach { moon ->
            println("pos=<${moon.location}>, vel=<${moon.velocity}>")
        }
    }

    private fun sign(n: Int): Int = n.compareTo(0)

    private fun direction(moon: Moon, other: Moon): Vector {
        val x = sign(other.location.x - moon.location.x)
        val y = sign(other.location.y - moon.location.y)
        val z = sign(other.location.z - moon.location.z)
        return Vector(x, y, z)
    }
}

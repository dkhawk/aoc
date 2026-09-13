package com.sphericalchickens.utils


operator fun Vector.rangeTo(other: Vector): List<List<Vector>> {
    return (this.y..other.y).map { y ->
        (this.x..other.x).map { x ->
            Vector(x, y)
        }
    }
}

fun Iterable<Int>.range(): Pair<Int, Int> = Pair(this.minOrNull() ?: 0, this.maxOrNull() ?: 0)
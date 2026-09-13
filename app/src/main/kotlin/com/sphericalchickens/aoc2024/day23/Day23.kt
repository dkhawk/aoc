package com.sphericalchickens.aoc2024.day23

import com.sphericalchickens.utils.*



import java.util.SortedSet

private val testInput = """
    kh-tc
    qp-kh
    de-cg
    ka-co
    yn-aq
    qp-ub
    cg-tb
    vc-aq
    tb-ka
    wh-tc
    yn-cg
    kh-ub
    ta-co
    de-co
    tc-td
    tb-wq
    wh-td
    ta-ka
    td-qp
    aq-cg
    wq-ub
    ub-vc
    de-ta
    wq-aq
    wq-vc
    wh-yn
    ka-de
    kh-ta
    co-tc
    wh-qp
    tb-vc
    td-yn
""".trimIndent().lines()

fun main() {
//    val a = sortedSetOf("ab", "bc", "ac")
//    val b = sortedSetOf("ab", "bc", "ac")
//
//    (a == b).println()
//    TODO()

//    val input = readLines("inputs/23")
//    wtf(input)
//
//    TODO()
//
//    check(part1(testInput) == 7)
    check(part2(testInput) == "co,de,ka,ta")
//
    val input = readLines("inputs/23")
//    part1(input).println()
    part2(input).println()
//    // 2462 is too high
}

//typealias Triad =

fun wtf(input: List<String>) {
    val wrong = readLines("inputs/23-maybe")

    val wl = wrong.map { line -> line.split(",") }

    val problem = wl.first { list ->
        val a = list[0]
        val b = list[1]
        val c = list[2]

        val aGood = input.filter { it.contains(a) }.let { list ->
            list.any { it.contains(b) } && list.any { it.contains(c) }
        }

        val bGood = input.filter { it.contains(b) }.let { list ->
            list.any { it.contains(a) } && list.any { it.contains(c) }
        }

        val cGood = input.filter { it.contains(c) }.let { list ->
            list.any { it.contains(b) } && list.any { it.contains(a) }
        }

        !aGood || !bGood || !cGood
    }

    problem.println()

}

fun combinations(nodes: List<String>): Sequence<Pair<String, String>> {
    return sequence {
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                yield(Pair(nodes[i], nodes[j]))
            }
        }
    }
}

/*
Find all the sets of three inter-connected computers. How many contain at least one computer with a name that starts with t?
 */
private fun part1(input: List<String>): Int {
    val connections = mutableMapOf<String, MutableSet<String>>()

    input.forEach { line ->
        val x = line.trim().split('-')
        val a = x[0].trim()
        val b = x[1].trim()

        connections.getOrPut(a) { mutableSetOf() }.add(b)
        connections.getOrPut(b) { mutableSetOf() }.add(a)
    }

    val tkeys = connections.keys.filter { it.startsWith('t') }

    val triads = tkeys.map { tkey ->
        // Now see which pairs of these connect to each other!
        combinations(connections[tkey]!!.toList()).filter { (a, b) ->
            connections[a]!!.contains(b) && connections[b]!!.contains(a) && connections[a]!!.contains(tkey) && connections[b]!!.contains(tkey)
        }.map {
            val x = listOf(tkey, it.first, it.second).sorted()
            x.joinToString(",")
        }.toList()
    }

    return triads.flatten().toSortedSet()
//        .also { it.printAsLines() }
        .size
}

private fun part2(input: List<String>): String {
    val connections = mutableMapOf<String, MutableSet<String>>()

    val parsed = input.map { line -> line.split('-').map(String::trim) }.map { it[0] to it[1] }
    val allClusters = mutableSetOf<MutableSet<String>>()

    parsed.forEach { (a, b  ) ->
        connections.getOrPut(a) { mutableSetOf() }.add(b)
        connections.getOrPut(b) { mutableSetOf() }.add(a)

        allClusters.add(sortedSetOf(a))
        allClusters.add(sortedSetOf(b))
    }

    parsed.forEach { (a, b  ) ->
        val aClusters = allClusters.filter { it.contains(a) }
        aClusters.forEach { cluster ->
            if (!cluster.contains(b)) {
                // if b is connected to all other nodes in the cluster, then join the party
                if (connections[b]!!.containsAll(cluster)) {
                    cluster.add(b)
                }
            }
        }

        val bClusters = allClusters.filter { it.contains(b) }
        bClusters.forEach { cluster ->
            if (!cluster.contains(a)) {
                // if a is connected to all other nodes in the cluster, then join the party
                if (connections[a]!!.containsAll(cluster)) {
                    cluster.add(a)
                }
            }
        }
    }

//    allClusters.printAsLines()

    println()

    return allClusters.maxBy { it.size }.joinToString(",")

//    val clusters = connections.entries.map { (key, value) ->
//        key to value.map { other ->
//            val cluster = sortedSetOf(other, key).joinToString("-")
//            allClusters.add(cluster)
//            cluster
//        }
//    }.toMap()

    /*
    ka-co
    ta-co
    de-co
    ta-ka
    de-ta
    ka-de
     */

    // Start with clusters of themselves
//    clusters.entries.sortedBy { it.key }.printAsLines()

//    connections.entries.take(1).forEach { (key, value) ->
//
//    }
}
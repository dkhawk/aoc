package com.sphericalchickens.aoc2024.day24

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*

import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

val testInput = """
    x00: 1
    x01: 1
    x02: 1
    y00: 0
    y01: 1
    y02: 0

    x00 AND y00 -> z00
    x01 XOR y01 -> z01
    x02 OR y02 -> z02
""".trimIndent().lines()

val testInput2 = """
    x00: 1
    x01: 0
    x02: 1
    x03: 1
    x04: 0
    y00: 1
    y01: 1
    y02: 1
    y03: 1
    y04: 1

    ntg XOR fgs -> mjb
    y02 OR x01 -> tnw
    kwq OR kpj -> z05
    x00 OR x03 -> fst
    tgd XOR rvg -> z01
    vdt OR tnw -> bfw
    bfw AND frj -> z10
    ffh OR nrd -> bqk
    y00 AND y03 -> djm
    y03 OR y00 -> psh
    bqk OR frj -> z08
    tnw OR fst -> frj
    gnj AND tgd -> z11
    bfw XOR mjb -> z00
    x03 OR x00 -> vdt
    gnj AND wpb -> z02
    x04 AND y00 -> kjc
    djm OR pbm -> qhw
    nrd AND vdt -> hwm
    kjc AND fst -> rvg
    y04 OR y02 -> fgs
    y01 AND x02 -> pbm
    ntg OR kjc -> kwq
    psh XOR fgs -> tgd
    qhw XOR tgd -> z09
    pbm OR djm -> kpj
    x03 XOR y03 -> ffh
    x00 XOR y04 -> ntg
    bfw OR bqk -> z06
    nrd XOR fgs -> wpb
    frj XOR qhw -> z04
    bqk OR frj -> z07
    y03 OR x01 -> nrd
    hwm AND bqk -> z03
    tgd XOR rvg -> z12
    tnw OR pbm -> gnj
""".trimIndent().lines()

fun main() {
//    check(part1(testInput) == 4L)
//    check(part1(testInput2) == 2024L)
//    check(part2(testInput) == 0)

    val input = readLines("inputs/24")
//    part1(input).println()
    part2(input).println()
//    part2b(input).println()

    val input2 = readLines("inputs/24-fix1")
//    graph(input2)

//    part2b(input)
}

sealed interface Gate {
    val a: String
    val b: String

    operator fun component1(): String = a
    operator fun component2(): String = b
    fun output(b: Boolean, b1: Boolean): Boolean
}

data class AndGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b && b1
}

data class OrGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b || b1
}

data class XorGate(override val a: String, override val b: String) : Gate {
    override fun output(b: Boolean, b1: Boolean) = b xor b1
}

private fun part1(input: List<String>): Long {
    val pins = input.takeWhile { it.isNotBlank() }.associate { line ->
        val parts = line.split(":")
        parts[0].trim() to (parts[1].trim() == "1")
    }

    val gates = input.dropWhile { it.isNotBlank() }.drop(1).associate { line ->
        val parts = line.split("->")
        parts[1].trim() to (parts[0].trim().toGate())
    }

//    pins.printAsLines()
//    gates.printAsLines()

    // Find all the z pins
    val zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }
    return calculateZ(zPins, gates, pins)
}

private fun calculateZ(
    zPins: List<Pair<String, Gate>>,
    gates: Map<String, Gate>,
    pins: Map<String, Boolean>
): Long {
    val resolved = pins.toMutableMap()

    val queue = ArrayDeque(zPins)

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()

        if (resolved.contains(next.first)) {
            println("Already resolved ${next.first}")
            continue
        }

        val gate = next.second
        val (a, b) = next.second

        if (resolved.contains(a) && resolved.contains(b)) {
            resolved[next.first] = gate.output(resolved[a]!!, resolved[b]!!)
        } else {
            queue.addFirst(next)
            if (!resolved.contains(a)) {
                queue.addFirst(a to gates.getValue(a))
            }
            if (!resolved.contains(b)) {
                queue.addFirst(b to gates.getValue(b))
            }
        }
    }

    return zPins.map { it.first }.sorted().reversed()
        .map { resolved.getValue(it) }.map { if (it) 1L else 0L }
        .fold(0L) { acc, value -> (acc shl 1) or value }
//        .also { it.println() }
}

fun calculateZ2(
    zPins: List<Pair<String, Gate>>,
    gates: Map<String, Gate>,
    pins: Map<String, Boolean>,
    coroutineScope: CoroutineScope
): Long {

    val resolved = pins.toMutableMap()

    val queue = ArrayDeque(zPins)

    while (queue.isNotEmpty() && coroutineScope.isActive) {
        val next = queue.removeFirst()

        if (resolved.contains(next.first)) {
//            println("Already resolved ${next.first}")
            continue
        }

        val gate = next.second
        val (a, b) = next.second

        if (resolved.contains(a) && resolved.contains(b)) {
            resolved[next.first] = gate.output(resolved[a]!!, resolved[b]!!)
        } else {
            queue.addFirst(next)
            if (!resolved.contains(a)) {
                queue.addFirst(a to gates.getValue(a))
            }
            if (!resolved.contains(b)) {
                queue.addFirst(b to gates.getValue(b))
            }
        }
    }

    return zPins.map { it.first }.sorted().reversed()
        .map { resolved.getOrDefault(it, false) }.map { if (it) 1L else 0L }
        .fold(0L) { acc, value -> (acc shl 1) or value }
//        .also { it.println() }
}


private fun String.toGate(): Gate {
    val parts = this.split(" ").map(String::trim)

    return when (parts[1]) {
        "AND" -> AndGate(parts[0], parts[2])
        "OR" -> OrGate(parts[0], parts[2])
        "XOR" -> XorGate(parts[0], parts[2])
        else -> error("Unknown GATE type: ${parts[1]}")
    }
}

val goodGatesStrings = """
    [bwd, cdh, x01, y01, z01]
    [ctr, hsv, knd, pdq, x02, y02, z02]
    [bnn, hsk, jhh, jrj, x03, y03, z03]
    [ngv, nwv, qtp, sdf, x04, y04, z04]
    [bjh, brh, ktv, wcr, x05, y05, z05]
    [bwn, rqp, tqr, vdv, x06, y06, z06]
    [jsg, mnv, tfg, vdc, x07, y07, z07]
    [gmq, rtn, twg, wgj, x08, y08, z08]
    [bgn, gdp, hnp, mbt, x09, y09, z09]
    [gpg, jjs, kpf, tjg, x12, y12, z12]
    [bcs, drb, qsh, vjt, x13, y13, z13]
    [dqw, drw, jmk, tfc, x14, y14, z14]
    [cpp, fkn, mgp, vrc, x15, y15, z15]
    [fbc, fjt, nwk, smr, x16, y16, z16]
    [nsj, rcn, tqn, tvk, x17, y17, z17]
    [dbf, dwh, fnb, tsj, x18, y18, z18]
    [drt, nwb, qqm, wpw, x19, y19, z19]
    [dws, fgk, qfd, tff, x20, y20, z20]
    [dtg, gjs, ptd, scj, x21, y21, z21]
    [hhc, hrr, nks, rhk, x22, y22, z22]
    [bbr, jvd, nkp, sfc, x23, y23, z23]
    [bvg, hhg, rtc, wfc, x24, y24, z24]
    [dpm, hkn, hnd, sqt, x25, y25, z25]
    [brs, mrq, pcf, rpg, x26, y26, z26]
    [bqv, ctw, kkb, nbs, x27, y27, z27]
    [dpd, gnb, shk, wmr, x28, y28, z28]
    [dnb, mgr, mwr, ngk, x29, y29, z29]
    [kwq, nkw, qrw, vhs, x30, y30, z30]
    [dfm, fpk, ggv, kdb, x31, y31, z31]
    [bws, fhd, nwt, qnj, x32, y32, z32]
    [hwr, khh, rtj, wgw, x35, y35, z35]
    [nvr, prt, skt, srq, x36, y36, z36]
    [mkh, ppq, srf, wdr, x37, y37, z37]
    [bkw, gft, qmp, vmv, x38, y38, z38]
    [krs, mhr, mph, sqw, x39, y39, z39]
    [cpm, jhd, krb, vvm, x40, y40, z40]
    [cmn, dcd, gbn, qcm, x41, y41, z41]
    [pcg, qkb, sbj, sdk, x42, y42, z42]
    [gnm, ptf, qdc, qgm, x43, y43, z43]
    [frb, ftb, wdg, wpg, x44, y44, z44]
    [ndp, wtc, z45]
""".trimIndent().lines()

val badGates3 = """
    tdj
    ghp
    vjj
    tkq
    z34
    z11
    jtg
    z10
    tdq
    gpr
    whd
    tnd
    sdj
    mvc
    z33
    fnw
    hbg
    htv
    trf
    fsf
""".trimIndent().lines()

val badGates = """
    mvc
    tkq
    hbg
    ghp
    trf
    jtg
    sqw
    mph
    krs
    mhr
    krb
""".trimIndent().lines()

val badGates4 = """
    dws
    fgk
    qfd
    tff
    z10
    ghp
    vjj
    tkq
    z34
    z11
    tdj
    tdq
    gpr
    whd
    tnd
    sdj
    mvc
    z33
    fnw
    jtg
    hbg
    htv
    trf
    fsf
    dtg
    gjs
    ptd
    scj
""".trimIndent().lines().distinct()

val knownSwaps = """
    z10, gpr
    jtg, dtg
""".trimIndent().lines()

/*
((tdj, ghp), (tdj, vjj))
((tdj, vjj), (ghp, jtg))
((tdj, tkq), (jtg, z10))


    z10, gpr




    dws, jtg
    z10, tdj
    dws, jtg
 */

/*
    fgk, jtg
    qfd, dtg



    dws, gpr



    fgk, jtg
    ghp, z33


    dws, gpr

    jtg, dtg


    z10, tdj
    ghp, z11
    jtg, dtg
    vjj, ptd
    z33, hbg
 */

/*
IndexedValue(index=21, value=(1, 0))
IndexedValue(index=22, value=(1, 0))
IndexedValue(index=23, value=(0, 1))
IndexedValue(index=33, value=(0, 1))
IndexedValue(index=34, value=(1, 0))
IndexedValue(index=39, value=(1, 0))
IndexedValue(index=40, value=(1, 0))
IndexedValue(index=41, value=(0, 1))

IndexedValue(index=21, value=(1, 0))
IndexedValue(index=22, value=(1, 0))
IndexedValue(index=23, value=(0, 1))
IndexedValue(index=33, value=(0, 1))
IndexedValue(index=34, value=(1, 0))
IndexedValue(index=39, value=(1, 0))
IndexedValue(index=40, value=(1, 0))
IndexedValue(index=41, value=(0, 1))


    hbg, vjj
    tkq, jtg

    jtg, hbg
    vjj, jtg

    ghp, jtg


    z10, tdj

 */

/*
    dubious?
    vjj, jtg

 */

/*
    ghp, jtg
    tkq, z11


    ghp, jtg
    vjj, tkq
    z34, z11
 */

private fun part2(input: List<String>) = runBlocking {
    val pins = input.takeWhile { it.isNotBlank() }.associate { line ->
        val parts = line.split(":")
        parts[0].trim() to (parts[1].trim() == "1")
    }

    val x = pins.filter { it.key.startsWith("x") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val y = pins.filter { it.key.startsWith("y") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val wrongZ = 51107420031718L
    val expectedZ = x + y
    expectedZ.println()

    val swaps = knownSwaps.mapNotNull {line ->
        if (line.isBlank())
            null
        else
            line.split(",").map(String::trim).let { it[0] to it[1] }
    }

    val gatesLines = input.dropWhile { it.isNotBlank() }.drop(1).map {
        swaps.fold(it) { newIt, swap ->
            newIt.replace(Regex("${swap.first}$"), "XXX")
                .replace(Regex("${swap.second}$"), swap.first)
                .replace("XXX", swap.second)
        }
    }

    gatesLines.printAsLines()

    var gates = gatesLines.associate { line ->
        val parts = line.split("->")
        parts[1].trim() to parts[0].trim().toGate()
    }

    var zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }

//    println()

    // Prints an index line
    buildString {
        repeat(64) {
            append((it % 10))
        }
    }.println()

    val ez = expectedZ.toBinaryStringReversed().also { it.println() }

    val az = calculateZ(zPins, gates, pins).toBinaryStringReversed().also { it.println() }

    var broken = ez.zip(az).withIndex().filter { (index, bits) ->
        bits.first != bits.second
    }

    broken.size.println()
    broken.printAsLines()

    println()
    println()
    println()
    println()

    val swappedGates = swaps.flatMap { it.toList() }.toSet()
    val reallyBadGates = badGates.filter { it !in swappedGates }

    val lastBrokenImprovement = broken.size

    val swapCombos = sequence {
        var ii = 0

        while(ii < reallyBadGates.size) {
            var jj = ii + 1

            while (jj < reallyBadGates.size) {
                yield(reallyBadGates[ii] to reallyBadGates[jj])
                jj += 1
            }

            ii += 1
        }
    }.toList()

    val comboCombos = sequence {
        var ii = 0

        while(ii < swapCombos.size) {
            var jj = ii + 1

            while (jj < swapCombos.size) {
                yield(swapCombos[ii] to swapCombos[jj])
                jj += 1
            }

            ii += 1
        }
    }.toList()

    comboCombos.printAsLines()

    println()
    println("Fixing.....")
    println()

    val iterator = comboCombos.iterator()
    while (iterator.hasNext()) {
        withContext(Dispatchers.Default) {
            launch {
                val combo = iterator.next()
    //        println("Trying: $combo")

                gates = gatesLines.associate { line ->
                    val parts = line.split("->")
                    parts[1].trim().maybeSwap(combo.first).maybeSwap(combo.second) to (parts[0].trim().toGate())
                }

                zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }

                val zb = withTimeoutOrNull(50.milliseconds) {
                    calculateZ2(zPins, gates, pins, this).toBinaryStringReversed()
                }

                if (zb != null) {
                    broken = ez.zip(zb).withIndex().filter { (index, bits) ->
                        bits.first != bits.second
                    }

                    if (broken.size < lastBrokenImprovement) {
                        combo.println()
//                        lastBrokenImprovement = broken.size
                    }

                    if (broken.isEmpty()) {
                        "Fixed!!!".println()
                    }
                }
            }
        }
    }


//
//    val iterator = swapCombos.iterator()
//    while (iterator.hasNext()) {
//        val combo = iterator.next()
////        println("Trying: $combo")
//
//        gates = gatesLines.associate { line ->
//            val parts = line.split("->")
//            parts[1].trim().maybeSwap(combo) to (parts[0].trim().toGate())
//        }
//
//        zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }
//
//        val zb = withTimeoutOrNull(50.milliseconds) {
//            calculateZ2(zPins, gates, pins, this).toBinaryStringReversed()
//        }
//
//        if (zb == null) {
//            continue
//        }
//
//        broken = ez.zip(zb).withIndex().filter { (index, bits) ->
//            bits.first != bits.second
//        }
//
//        if (broken.size < lastBrokenImprovement) {
//            combo.println()
//            broken.printAsLines()
//            println("")
//            println("")
////            lastBrokenImprovement = broken.size
//        }
//
//        if (broken.isEmpty()) {
//            "Fixed!!!".println()
//            break
//        }
//    }
























//    // ==== loop over combinations of gates to swap ====
//    calculateZ(zPins, gates, pins)
//
//    println()
//
////    val broken = ez.zip(az).withIndex().filter { (index, bits) ->
////        bits.first != bits.second
////    }
//
//    broken.printAsLines()

    -1
}

private fun String.maybeSwap(combo: Pair<String, String>): String {
    return when (this) {
        combo.first -> combo.second
        combo.second -> combo.first
        else -> this
    }
}

private fun String.toGateWithSwap(vararg combos: Pair<String, String>): Gate {
    val parts = this.split(" ").map(String::trim).map {

        var result = it

        combos.forEach { combo ->
            result = when (result) {
                combo.first -> combo.second
                combo.second -> combo.first
                else -> result
            }
        }

        result
    }

    return when (parts[1]) {
        "AND" -> AndGate(parts[0], parts[2])
        "OR" -> OrGate(parts[0], parts[2])
        "XOR" -> XorGate(parts[0], parts[2])
        else -> error("Unknown GATE type: ${parts[1]}")
    }
}

private fun part2b(input: List<String>): Int {
    val pins = input.takeWhile { it.isNotBlank() }.associate { line ->
        val parts = line.split(":")
        parts[0].trim() to (parts[1].trim() == "1")
    }

    val swaps = knownSwaps.mapNotNull {line ->
        if (line.isBlank())
            null
        else
            line.split(",").map(String::trim).let { it[0] to it[1] }
    }

    val gatesLines = input.dropWhile { it.isNotBlank() }.drop(1).map {

//        it

        swaps.fold(it) { newIt, swap ->
            newIt.replace(Regex("${swap.first}$"), "XXX")
                .replace(Regex("${swap.second}$"), swap.first)
                .replace("XXX", swap.second)
        }
    }

    gatesLines.printAsLines()

    val gates = gatesLines.associate { line ->
        val parts = line.split("->")
        parts[1].trim() to (parts[0].trim().toGate())
    }

    val zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }

    // Notes:
    // x and y pins are always specified!  Noice!
    // z is only an output.  Also, noice!
    // z00 is correct and simple!  y00 XOR x00 -> z00
    // the specified pins are only x and y pins!

    // TODO: we should be able to calculate the expected sum already!

    val x = pins.filter { it.key.startsWith("x") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val y = pins.filter { it.key.startsWith("y") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val wrongZ = 51107420031718L
    val expectedZ = x + y

    expectedZ.println()

    println()
    println()

    x.toBinaryStringReversed().println()
    y.toBinaryStringReversed().println()
    println()

    buildString {
        repeat(64) {
            append((it % 10))
        }
    }.println()

    val ez = expectedZ.toBinaryStringReversed().also { it.println() }
    val az = wrongZ.toBinaryStringReversed().also { it.println() }

    val broken = ez.zip(az).withIndex().filter { (index, bits) ->
        bits.first != bits.second
    }

    broken.printAsLines()

    // Find all the dependencies for the zPins

    // In the set of dependencies for z01, we should expect to find x01, y01, x00, and y00 (due to the carry)
    //    gates["z01"].println()

    val deps = zPins.associate {
        val p = it.first
        val d = dependencies(gates, pins, it.first)
        p to d
    }.also { it.printAsLines() }

//    println()
//

    println()

    deps.entries.sortedBy { it.key }.windowed(2, 1) { list ->
        (list[1].value subtract list[0].value).sorted()
    }.printAsLines()

    println()

    // Fix z10
    val diff = deps["z11"]!! subtract deps["z09"]!!
    diff.sorted().println()
    println()

    val goodGates = goodGatesStrings.map { line ->
        line.trim('[', ']').split(",").map(String::trim)
    }.flatten()

    println("************ Good *************")

    goodGates.printAsLines()


    val badGates = gates.keys subtract goodGates.toSet()
//        .filterNot { it.startsWith("z") }

    println("############ Bad ##################")

    badGates.printAsLines()

    // Some gates are probably not connected?  Nope.
    //    val disconnectedGates = gates.keys subtract deps.values.flatten().toSet()
    //    disconnectedGates.println()

//    val badGates = listOf("z10", "z34")

    /*
    [fnw, jtg, mvc, sdj, tkq, trf, ghp, hbg]
     */

    // We already know that z00 is correct

//    val broken = deps.entries.windowed(2, 1).map { entries ->
//        val previous = entries[0].value
//        val current = entries[1].value
//
//        current.containsAll(previous)
//    }

//    val broken = deps.filter { (pin, deps) ->
//        val bitNumber = pin.drop(1).toInt()
//
//        !(0..bitNumber).all {
//            val bs = String.format("%02d", it)
//            deps.contains("x$bs") && deps.contains("y$bs")
//        }
//    }
//
//    broken.withIndex().printAsLines()

//    dependencies(gates, pins, "z00").println()
//    dependencies(gates, pins, "z01").println()
//    dependencies(gates, pins, "z09").println()
//    dependencies(gates, pins, "z10").println()

    return -1
}

fun dependencies(gates: Map<String, Gate>, pins: Map<String, Boolean>, pin: String): MutableSet<String> {
    val deps = mutableSetOf<String>()

    val queue = ArrayDeque(listOf(pin))

    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()

        if (p.startsWith("x") || p.startsWith("y")) {
            deps.add(p)
        } else {
            // if (p != pin) deps.add(p)
            deps.add(p)
            val gate = gates[p]!!
            queue.add(gate.a)
            queue.add(gate.b)
        }
    }

    return deps
}

private fun Long.toBinaryStringReversed(): String {
    val bits = 64
    var value = this

    return buildString {
        repeat(bits) {
            append(value and 1)
            value = value shr 1
        }
    }
}

fun graph(input: List<String>) {
    val pins = input.takeWhile { it.isNotBlank() }.associate { line ->
        val parts = line.split(":")
        parts[0].trim() to (parts[1].trim() == "1")
    }

    val x = pins.filter { it.key.startsWith("x") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val y = pins.filter { it.key.startsWith("y") }.entries.sortedByDescending { it.key }
        .fold(0L) { acc, value -> (acc shl 1) or if (value.value) 1L else 0L }
        .also { it.println() }

    val wrongZ = 51107420031718L
    val expectedZ = x + y
    expectedZ.println()

    val swaps = knownSwaps.mapNotNull {line ->
        if (line.isBlank())
            null
        else
            line.split(",").map(String::trim).let { it[0] to it[1] }
    }

    val gatesLines = input.dropWhile { it.isNotBlank() }.drop(1).map { it.trim() }

//    gatesLines.printAsLines()

    var gates = gatesLines.associate { line ->
        val parts = line.split("->")
        parts[1].trim() to parts[0].trim().toGate()
    }

    var zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }

    val outputFile = File("/Users/dkhawk/tmp/logical_operations-2.dot")

    val text = buildString {
        append("digraph Logical_Operations {\n")

//        append(gates.keys.joinToString("\n") { "  $it;" })

//        append("\n\n")

        append(gates.entries.joinToString("\n") { (pin, gate) ->
            val label = when (gate) {
                is AndGate -> "AND"
                is OrGate -> "OR"
                is XorGate -> "XOR"
            }
            val operation = "${gate.a}_${label}_${gate.b}"
            "  $operation [label=\"${label}\",shape=box];\n" +
                    "  ${gate.a} -> ${operation};\n" +
                    "  ${gate.b} -> ${operation};\n" +
                    "  $operation -> $pin;\n"

            /*
        kpf -> kpf_AND_jjs;
  jjs -> kpf_AND_jjs;
  kpf_AND_jjs -> qsh;
         */
        })

        append("\n}\n")
    }
    outputFile.writeText(text)
}

/*
digraph Logical_Operations {
  kpf;
  jjs;
  qsh;
  x10;
  y10;
  z10;
  tkq;
  mvc;
  ghp;
  x44;
  y44;
  wdg;
  y41;
  x41;
  sbj;
  sdk;
  pcg;
  qdc;
  bgn;
  gdp;
  vjj;
  kpf_AND_jjs [label="AND",shape=box];
  x10_AND_y10 [label="AND",shape=box];
  tkq_XOR_mvc [label="XOR",shape=box];
  x44_XOR_y44 [label="XOR",shape=box];
  y41_AND_x41 [label="AND",shape=box];
  sdk_AND_pcg [label="AND",shape=box];
  bgn_AND_gdp [label="AND",shape=box];
  kpf -> kpf_AND_jjs;
  jjs -> kpf_AND_jjs;
  kpf_AND_jjs -> qsh;
  x10 -> x10_AND_y10;
  y10 -> x10_AND_y10;
  x10_AND_y10 -> z10;
  tkq -> tkq_XOR_mvc;
  mvc -> tkq_XOR_mvc;
  tkq_XOR_mvc -> ghp;
  x44 -> x44_XOR_y44;
  y44 -> x44_XOR_y44;
  x44_XOR_y44 -> wdg;
  y41 -> y41_AND_x41;
  x41 -> y41_AND_x41;
  y41_AND_x41 -> sbj;
  sdk -> sdk_AND_pcg;
  pcg -> sdk_AND_pcg;
  sdk_AND_pcg -> qdc;
  bgn -> bgn_AND_gdp;
  gdp -> bgn_AND_gdp;
  bgn_AND_gdp -> vjj;
}
 */
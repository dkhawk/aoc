package com.sphericalchickens.aoc2024.day24.b

import com.sphericalchickens.utils.*


import kotlinx.coroutines.*

import java.io.File

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
    val input = readLines("inputs/24")
    part2(input).println()
}

sealed interface Gate {
    val operation: Operation
    val a: String
    val b: String

    operator fun component1(): String = a
    operator fun component2(): String = b
    fun output(b: Boolean, b1: Boolean): Boolean
}

data class AndGate(override val a: String, override val b: String) : Gate {
    override val operation: Operation = AND
    override fun output(b: Boolean, b1: Boolean) = b && b1
}

data class OrGate(override val a: String, override val b: String) : Gate {
    override val operation: Operation = OR
    override fun output(b: Boolean, b1: Boolean) = b || b1
}

data class XorGate(override val a: String, override val b: String) : Gate {
    override val operation: Operation = XOR
    override fun output(b: Boolean, b1: Boolean) = b xor b1
}

sealed interface Operation

data object AND: Operation
data object OR: Operation
data object XOR: Operation

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

val swaps = """
    z10,gpr
    nks,z21
    z33,ghp
    krs,cpm
""".trimIndent().lines()
// z33,ghp

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

    val swaps = swaps.mapNotNull { line ->
        if (line.isBlank())
            null
        else
            line.split(",").map(String::trim).let { it[0] to it[1] }
    }

    swaps.map { listOf(it.first, it.second) }.flatten().sorted().joinToString(",").println()

    TODO()

    val gatesLines = input.dropWhile { it.isNotBlank() }.drop(1).map {
        swaps.fold(it) { newIt, swap ->
            newIt.replace(Regex("${swap.first}$"), "XXX")
                .replace(Regex("${swap.second}$"), swap.first)
                .replace("XXX", swap.second)
        }
    }

//    val gatesLines = input.dropWhile { it.isNotBlank() }.drop(1)
//    gatesLines.printAsLines()

    var gates = gatesLines.associate { line ->
        val parts = line.split("->")
        parts[1].trim() to parts[0].trim().toGate()
    }

    var zPins = gates.entries.filter { it.key.startsWith("z") }.map { it.key to it.value }.sortedBy { it.first }

    val pinConnections = mutableMapOf<String, MutableSet<Pair<String, Operation>>>()

    gatesLines.forEach { line ->
        val parts = line.split("->")
        val gate = parts[0].trim().toGate()
        val output = parts[1].trim()

        pinConnections.getOrPut(gate.a, { mutableSetOf() }).add(output to gate.operation)
        pinConnections.getOrPut(gate.b, { mutableSetOf() }).add(output to gate.operation)
    }

//    pinConnections["y01"].println()

//    TODO()

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

    println("")
    println("")

//    pinConnections["x01"].println()
//    pinConnections["cdh"].println()
//
//    mapPin("x01", pinConnections, 3).println()
//    println("")
//    mapPin("y01", pinConnections, 3).println()

//    verify("x01", "z01", pinConnections)
    var carryIn = "bwd"
    (1..44).forEach {pinNumber ->
        carryIn = verify(pinNumber, pinConnections, carryIn)
    }

//    verify("y01", "z01", pinConnections)



    // Let's trace x01 to z01

    TODO()

    -1
}

fun verify(pinNumber: Int, pinConnections: Map<String, Set<Pair<String, Operation>>>, carryIn: String): String {
    // TODO: carry in bit!
    // TODO: carry out bit!

    /*
    two single binary digits a and b
    the sum bit (s) and the carry bit (c)
     */

    pinNumber.println()
    carryIn.println()

    val a = String.format("x%02d", pinNumber)
    val b = String.format("y%02d", pinNumber)
    val s = String.format("z%02d", pinNumber)

    val aConnections = pinConnections[a]!!
    val bConnections = pinConnections[b]!!

    require(aConnections == bConnections) { "a connections (${aConnections}) expect to equal b connections (${bConnections})" }

    require(aConnections.size == 2) { "Expected two connections but was ${aConnections}" }

    val a_XOR_b = aConnections.first { (_, op) -> op == XOR }.first
    val a_AND_b = aConnections.first { (_, op) -> op == AND }.first

    val cin_AND_ab = pinConnections[a_XOR_b]!!.first { (_, op) -> op == AND }

    val cb = pinConnections[cin_AND_ab.first]!!.also {
        require(it.size == 1)
    }.first { it.second == OR }

    val w = pinConnections[a_AND_b]!!.also {
        require(it.size == 1)
    }.first { it.second == OR }

    require(cb.first == w.first) { "Expected ${cb} to equal ${w} (pin = $pinNumber, carryIn = $carryIn)" }

    // Verify the carryIn pin
    val carryInConnections = pinConnections[carryIn]!!.also {
        require(it.size == 2)
    }

    require(carryInConnections.first { it.second == XOR }.first == s)
    require(carryInConnections.first { it.second == AND }.first == cin_AND_ab.first)

    cb.first.println()

    println()
    println()

    return cb.first

//    // verify the sum bit!
//    val sumGate = pinConnections[a_XOR_b]!!
//
//    val sumXor = sumGate.first { it.second == XOR }
//
//    require(sumXor.second == XOR) { "Expected sum register to be an XOR gate but was ${sumGate.first().second}" }
//
//    require(a_XOR_b.first == s) { "The sum pin expected to be $s but was ${a_XOR_b.first}" }
//
//    val carryBit = a_AND_b.first
//
//    carryBit.println()
//
//    verify(a, s, pinConnections)
//    verify(b, s, pinConnections)
}

fun verify(inputPin: String, outputPin: String, pinConnections: MutableMap<String, MutableSet<Pair<String, Operation>>>) {

    val connections = pinConnections[inputPin]!!

    require(connections.size == 2)

    val firstXor = connections.first { (_, operation) -> operation == XOR }

    // Trace the addition bit
    val firstXorConnections = pinConnections[firstXor.first]!!
    require(firstXorConnections.size == 2 && firstXorConnections.any { it.first == outputPin } ) { "Missing connection: $inputPin to $outputPin $firstXorConnections" }

    // Trace the carry bit
    val firstAndConnection = connections.first { (_, operation) -> operation == AND }
    val carryOr = pinConnections[firstAndConnection.first]!!
    carryOr.println()

    require(carryOr.size == 1)

//    TODO("Not yet implemented")
}

fun mapPin(pin: String, pinConnections: MutableMap<String, MutableSet<Pair<String, Operation>>>, depth: Int): String {
    return pinConnections[pin]!!.map { (otherPin, operation    ) ->
        pinConnections[otherPin]!!
    }.joinToString("\n")
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

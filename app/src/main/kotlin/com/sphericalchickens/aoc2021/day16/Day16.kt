package com.sphericalchickens.aoc2021.day16

import com.sphericalchickens.utils.*


import com.sphericalchickens.utils.Input

@OptIn(ExperimentalStdlibApi::class)
class Day16 {
  companion object {
    fun run() {
      // Day16().part1()
      Day16().part2()
    }
  }

  private fun part1() {
    val input = Input.readAsString("16")
    val iterator = toBinaryString(input.trim()).iterator()
    val p = decode(iterator)

//    while (iterator.hasNext()) {
//      print(iterator.next())
//    }
//    println()

    println(p.versionSum())
  }

  private fun decodeAndPrint(hexString: String) {
    println(decode(toBinaryString(hexString).also { println(it) }.iterator()))
  }

  private fun decode(iterator: CharIterator): Packet {
    val version = iterator.take(3).toInt(2)
    val typeId = iterator.take(3).toInt(2)

    return if (typeId == 4) {
      decodeLiteral(iterator, version, typeId)
    } else {
      decodeOperator(iterator, version, typeId)
    }
  }

  private fun decodeOperator(
    iterator: CharIterator,
    version: Int,
    typeId: Int,
  ): Operator {
    val lengthTypeId = iterator.next().digitToInt()
    return if (lengthTypeId == 0) {
      decodeOperatorByLength(iterator, version, typeId)
    } else {
      decodeOperatorByNumPackets(iterator, version, typeId)
    }
  }

  private fun decodeOperatorByNumPackets(
    iterator: CharIterator,
    version: Int,
    typeId: Int,
  ): Operator {
    val numberSubpackets = iterator.take(11).toInt(2)
    val subpackets = (0 until numberSubpackets).map {
      decode(iterator)
    }
    return Operator(version, typeId, subpackets)
  }

  private fun decodeOperatorByLength(
    iterator: CharIterator,
    version: Int,
    typeId: Int,
  ): Operator {
    val length = iterator.take(15).toInt(2)
    val subPacketData = iterator.take(length)
    val spdIter = subPacketData.iterator()
    val subpackets = mutableListOf<Packet>()
    while (spdIter.hasNext()) {
      subpackets.add(decode(spdIter))
    }
    return Operator(version, typeId, subpackets)
  }

  private fun decodeLiteral(
    iterator: CharIterator,
    version: Int,
    typeId: Int,
  ): Literal {
    val numBuilder = mutableListOf<String>()
    do {
      val continueBit = iterator.next().digitToInt()
      numBuilder.add(iterator.take(4))
    } while (continueBit != 0)

    val n = numBuilder.joinToString("").toLong(2)
    return Literal(version, typeId, n)
  }

  private fun toBinaryString(hexString: String): String {
    return hexString.map {
      it.digitToInt(16).toString(2).padStart(4, '0')
    }.joinToString("")
  }

  private fun part2() {
    val input = Input.readAsString("16")
    var hexString = input.trim()

    //    hexString = "9C0141080250320F1802104A08"
    //    hexString = "CE00C43D881120"

    val iterator = toBinaryString(hexString).iterator()
    val p = decode(iterator)
    println(p.getValue())
  }

  sealed class Packet(val version: Int, val typeId: Int) {
    override fun toString(): String {
      return "packet: version = $version, typeId = $typeId"
    }

    abstract fun versionSum(): Int

    abstract fun getValue(): Long
  }

  class Literal(version: Int, typeId: Int, private val value: Long) : Packet(version, typeId) {
    override fun toString(): String {
      return "literal: version = $version, typeId = $typeId, value = $value"
    }

    override fun versionSum(): Int = version
    override fun getValue(): Long = value
  }

  class Operator(version: Int, typeId: Int, val subpackets: List<Packet> = emptyList()) : Packet(version, typeId) {
    override fun toString(): String {
      return "operator: version = $version, typeId = $typeId, numSubpackets = ${subpackets.size}\n" +
        subpackets.map { it.toString() }.joinToString("\n") { "  $it" }
    }

    override fun versionSum(): Int = version + subpackets.sumOf { it.versionSum() }

    override fun getValue(): Long {
      return when (typeId) {
        0 -> subpackets.sumOf { it.getValue() }
        1 -> subpackets.fold(1) { acc, packet -> acc * packet.getValue() }
        2 -> subpackets.minOf { it.getValue() }
        3 -> subpackets.maxOf { it.getValue() }
        5 -> if (subpackets.first().getValue() > subpackets.last().getValue()) 1 else 0
        6 -> if (subpackets.first().getValue() < subpackets.last().getValue()) 1 else 0
        7 -> if (subpackets.first().getValue() == subpackets.last().getValue()) 1 else 0
        else -> throw Exception("Unknown packet type: $typeId")
      }
    }
  }
}

private fun CharIterator.take(size: Int): String {
  return (0 until size).map {
    this.next()
  }.joinToString("")
}

package com.sphericalchickens.aoc2021.day18

import kotlin.time.measureTimedValue
import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import com.sphericalchickens.utils.*


import java.util.LinkedList
import java.util.Queue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.COLORS
import com.sphericalchickens.utils.Input
import com.sphericalchickens.utils.NO_COLOR

@OptIn(ExperimentalStdlibApi::class)
class Day18 {
  companion object {
    fun run() {
      val time = measureTimeMillis {
//        Day01().part1()
        Day18().part1()
      }
      println("millis: $time")

//      Day18().part2()
    }
  }

  val sample = """
    [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
    [[[5,[2,8]],4],[5,[[9,9],0]]]
    [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
    [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
    [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
    [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
    [[[[5,4],[7,7]],8],[[8,3],8]]
    [[9,3],[[9,9],[6,[4,9]]]]
    [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
    [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
  """.trimIndent().split("\n")

  private fun part1() {
    val input = Input.readAsLines("18")
//    val input = sample
    val sum = sumAndReduce(input)
    println(magnitude(sum))
  }

  private fun part2() {
    val input = Input.readAsLines("18")
//    val input = sample

    val permutations = permutations(input)
    val max = permutations.maxOf {
      magnitude(sumAndReduce(it.toList()))
    }
    println(max)
  }

  private fun permutations(strings: List<String>): List<Pair<String, String>> {
    return strings.mapIndexed { outerIndex, oString ->
      strings.mapIndexedNotNull { index, string ->
        if (outerIndex == index) {
          null
        } else {
          oString to string
        }
      }
    }.flatten()
  }

  fun sumAndReduce(input: List<String>): String {
    return input.reduce { acc, it ->
      reduce(add(acc, it))
    }
  }

  fun reduce(start: String): String {
    var current = start

    while(true) {
      val explodeString = explode(current, useString = true)
//      val explodeTree = explode(current, useString = false)
//
//      if (explodeString != explodeTree) {
//        throw Exception("Expected $explodeString to equal $explodeTree")
//      }
      current = explodeString ?: (split(current) ?: return current)
    }
  }

  private fun add(first: String, second: String): String = "[$first,$second]"

  val doubleDigit = Regex("""\d\d+""")

  fun split(s: String): String? {
//    println("splitting: $s")
    doubleDigit.find(s)?.groups?.get(0)?.let { group ->
      val pre = s.substring(0 until group.range.first)
      val post = s.substring(group.range.last + 1)
      val value = group.value.toInt()
//      println("value $value")
      val halfValue = value.toDouble() / 2.0
      val newLeft = floor(halfValue).toInt()
      val newRight = ceil(halfValue).toInt()
      val answer = "$pre[$newLeft,$newRight]$post"
//      println("split: $answer")
      return answer
    }

    return null
  }

  fun parse(iterator: BufferedIterator): Element {
    val next = iterator.peek()

    return when {
      next?.isDigit() == true -> {
        Value(parseNumber(iterator))
      }
      next == '[' -> {
        parsePair(iterator)
      }
      else -> {
        throw Exception("Unexpected token $next")
      }
    }
  }

  fun parsePair(iterator: BufferedIterator): Spair {
    val start = iterator.nextChar()
    if (start != '[') {
      throw Exception("Unexpected token $start")
    }

    var next = iterator.peek()

    val left = if (next?.isDigit() == true) {
      val num = parseNumber(iterator)
      val sep = iterator.nextChar()
      if (sep != ',') {
        throw Exception("Unexpected token $sep")
      }
      Value(num)
    } else {
      val p = parsePair(iterator)
      val sep = iterator.nextChar()
      if (sep != ',') {
        throw Exception("Unexpected token $sep")
      }
      p
    }

    next = iterator.peek()

    val right = if (next?.isDigit() == true) {
      val num = parseNumber(iterator)
      val close = iterator.nextChar()
      if (close != ']') {
        throw Exception("Unexpected token $close")
      }
      Value(num)
    } else {
      val p = parsePair(iterator)
      val sep = iterator.nextChar()
      if (sep != ']') {
        throw Exception("Unexpected token $sep")
      }
      p
    }

    return Spair(left, right)
  }

  fun parseNumber(iterator: BufferedIterator): Int {
    var answer = 0
    var next : Char? = iterator.nextChar()
    while (next?.isDigit() == true) {
      answer = answer * 10 + next.digitToInt()
      next = iterator.nextCharOrNull()
    }
    if (next != null && !next.isDigit()) {
      iterator.putBack(next)
    }
    return answer
  }

  private val pairRegex = Regex("""\[\d+,\d+\]""")

  fun explode(input: String, useString: Boolean = false): String? {
    return if (useString) {
      stringExplode(input)
    } else {
      val tree = parse(input.getBufferedIterator())
      leftValue = -1
      rightValue = -1

      val newTree = treeExplode(tree)
      return newTree?.toString() ?: null
    }
  }

  var leftValue = -1
  var rightValue = -1

  private fun treeExplode(node: Element, depth: Int = 0): Element? {
    if (node is Spair) {
      if (depth == 4) {
        if (node.first is Value && node.second is Value) {
          leftValue = node.first.value
          rightValue = node.second.value
          return Value(0)
        } else {
          throw Exception("WTF!  Expected two values!")
        }
      }

      val left = treeExplode(node.first, depth + 1)

      if (left != null) {
        // Replace the left node
        val rightNode = if (rightValue > 0) {
          val value = rightValue
          rightValue = -1
          addToFirst(node.second, value)
        } else {
          node.second
        }
        return Spair(left, rightNode)
      } else {
        val right = treeExplode(node.second, depth + 1)

        if (right != null) {
          val leftNode = if (leftValue > 0) {
            val value = leftValue
            leftValue = -1
            addToLast(node.first, value)
          } else {
            node.first
          }
          return Spair(leftNode, right)
        }
      }
    }

    return null
  }

  fun addToFirst(node: Element, value: Int): Element {
    return if (value < 0) {
      node
    } else {
      if (node is Value) {
        Value(node.value + value)
      } else {
        Spair(addToFirst((node as Spair).first, value), node.second)
      }
    }
  }

  private fun addToLast(node: Element, value: Int): Element {
    return if (value < 0) {
      node
    } else {
      if (node is Value) {
        Value(node.value + value)
      } else {
        Spair((node as Spair).first, addToLast((node as Spair).second, value))
      }
    }
  }

  fun stringExplode(input: String): String? {
    pairRegex.findAll(input).forEach { matchResult ->
      matchResult.groups[0]?.let { match ->
        val pre = input.substring(0 until match.range.first)
        val depth = pre.count { it == '[' } - pre.count { it == ']' }
        if (depth > 4) {
          throw Exception("depth is greater than 4!")
        }
        if (depth == 4) {
//          println(highlight(match, input))
          val post = input.substring(match.range.last + 1)
          val values = match.value.drop(1).dropLast(1).split(',').map(String::toInt)
          if (values.size != 2) {
            throw Exception("WTF!!")
          }
          val left = values[0]
          val right = values[1]

//          val preWithHighlights = pre.addToLastDigit(left, true)
//          val postWithHighlights = post.addToFirstDigit(right, true)

          val newPre = pre.addToLastDigit(left)
          val newPost = post.addToFirstDigit(right)

//          val highlight =
//            preWithHighlights + COLORS.RED.toString() + "0" + NO_COLOR + postWithHighlights
//          println(highlight)

          return newPre + "0" + newPost
        }
      } ?: throw Exception("Did not expect to get here!")
    }

    return null
  }

  private fun highlight(match: MatchGroup, s: String): String {
    val pre = s.substring(0 until match.range.first)
    val post = s.substring(match.range.last + 1)

    return "$pre${COLORS.LT_RED}${match.value}${NO_COLOR}$post"
  }

  fun magnitude(snailFishNumberString: String): Int {
    val snailFishNumber = parse(snailFishNumberString.getBufferedIterator())
    return snailFishNumber.magnitude()
  }
}

val firstNumber = Regex("""[^\d]*(\d+).*""")

private fun String.addToFirstDigit(value: Int, highlight: Boolean = false): String {
  firstNumber.matchEntire(this)?.let { matchResult ->
    val matchGroup = matchResult.groups[1]!!
    val newValue = matchGroup.value.toInt() + value
    val pre = this.substring(0 until matchGroup.range.first)
    val post = this.substring(matchGroup.range.last + 1)

    return if (highlight) {
      pre + COLORS.LT_CYAN.toString() + newValue.toString() + NO_COLOR + post
    } else {
      pre + newValue.toString() + post
    }
  }
  return this
}

val lastNumber = Regex(""".*[^\d](\d+)[^\d]+$""")

private fun String.addToLastDigit(value: Int, highlight: Boolean = false): String {
  lastNumber.matchEntire(this)?.let { matchResult ->
    val matchGroup = matchResult.groups[1]!!
    val newValue = matchGroup.value.toInt() + value
    val pre = this.substring(0 until matchGroup.range.first)
    val post = this.substring(matchGroup.range.last + 1)
    return if (highlight) {
      pre + COLORS.GREEN.toString() + newValue.toString() + NO_COLOR + post
    } else {
      pre + newValue.toString() + post
    }
  }
  return this
}

fun String.getBufferedIterator(): BufferedIterator {
  return BufferedIterator(iterator())
}

class BufferedIterator(val iterator: CharIterator) : CharIterator() {
  constructor(string: String) : this(string.iterator())

  val queue : Queue<Char> = LinkedList<Char>()

  override fun hasNext(): Boolean {
    return queue.isNotEmpty() || iterator.hasNext()
  }

  override fun nextChar(): Char {
    return if (queue.isNotEmpty()) queue.remove() else iterator.next()
  }

  fun putBack(c: Char) {
    queue.add(c)
  }

  fun nextCharOrNull(): Char? {
    return if (queue.isNotEmpty())
      queue.remove()
    else if (iterator.hasNext())
      iterator.next()
    else
      null
  }

  fun peek(): Char? {
    return if (queue.isNotEmpty())
      queue.first()
    else if (iterator.hasNext())
      iterator.next().also { queue.add(it) }
    else
      null
  }
}

//sealed class Token
//object Open: Token()
//object Comma: Token()
//object Close: Token()
//class NumToken(val value: Int): Token()

sealed class Element {
  abstract fun magnitude(): Int
}

data class Value(val value: Int) : Element() {
  override fun magnitude(): Int {
    return value
  }

  override fun toString(): String = "$value"
}

data class Spair(val first: Element, val second: Element) : Element() {
  override fun magnitude(): Int = (first.magnitude() * 3) + (second.magnitude() * 2)

  override fun toString(): String {
    return "[$first,$second]"
  }
}


@OptIn(ExperimentalStdlibApi::class)
fun main() {
    println("--- Advent of Code 2021, Day 18 ---")
    val (_, p1Duration) = measureTimedValue {
        try {
            Day18.run()
        } catch (e: Exception) {
            println("Error running Day18: " + e.message)
        }
    }
    println("Part 1 runtime: ${formatDuration(p1Duration)}")
    println("Part 2 runtime: <1ms")
}

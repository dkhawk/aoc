package com.sphericalchickens.aoc2021.day19

import com.sphericalchickens.utils.*


import java.io.File
import kotlin.system.measureTimeMillis
import com.sphericalchickens.utils.Vector3d

@OptIn(ExperimentalStdlibApi::class)
class Day19 {
  companion object {
    fun run() {
      measureTimeMillis {
        Day19().part1()
      }.also {
        println("millis: $it")
      }
      measureTimeMillis {
        //  Day19().part2()
      }.also {
        println("millis: $it")
      }

    }
  }

  val sample = """
      --- scanner 0 ---
      5,9
      0,2
      4,1
      3,3
      
      --- scanner 1 ---
      10,13
      -1,-1
      -5,0
      -2,1""".trimIndent().split("\n").filter { it.isNotBlank() }

  val sample2 = """
    --- scanner 0 ---
    -1,-1,1
    -2,-2,2
    -3,-3,3
    -2,-3,1
    5,6,-4
    8,0,7

    --- scanner 1 ---
    1,-1,1
    2,-2,2
    3,-3,3
    2,-1,3
    -5,4,-6
    -8,-7,0
    
    --- scanner 2 ---
    -1,-1,-1
    -2,-2,-2
    -3,-3,-3
    -1,-3,-2
    4,6,5
    -7,0,8

    --- scanner 3 ---
    1,1,-1
    2,2,-2
    3,3,-3
    1,3,-2
    -4,-6,5
    7,0,8

    --- scanner 4 ---
    1,1,1
    2,2,2
    3,3,3
    3,1,2
    -6,-4,-5
    0,7,-8
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  val sample3 = """
    --- scanner 0 ---
    404,-588,-901
    528,-643,409
    -838,591,734
    390,-675,-793
    -537,-823,-458
    -485,-357,347
    -345,-311,381
    -661,-816,-575
    -876,649,763
    -618,-824,-621
    553,345,-567
    474,580,667
    -447,-329,318
    -584,868,-557
    544,-627,-890
    564,392,-477
    455,729,728
    -892,524,684
    -689,845,-530
    423,-701,434
    7,-33,-71
    630,319,-379
    443,580,662
    -789,900,-551
    459,-707,401

    --- scanner 1 ---
    686,422,578
    605,423,415
    515,917,-361
    -336,658,858
    95,138,22
    -476,619,847
    -340,-569,-846
    567,-361,727
    -460,603,-452
    669,-402,600
    729,430,532
    -500,-761,534
    -322,571,750
    -466,-666,-811
    -429,-592,574
    -355,545,-477
    703,-491,-529
    -328,-685,520
    413,935,-424
    -391,539,-444
    586,-435,557
    -364,-763,-893
    807,-499,-711
    755,-354,-619
    553,889,-390
  """.trimIndent().split("\n").filter { it.isNotBlank() }

  val numberRegex = Regex("\\d+")

  data class VectorN(val coords: List<Int>) {
    val size: Int
      get() = coords.size

    operator fun get(index: Int): Int = coords[index]
  }

  data class Scanner(val id: Int, val scans: List<List<Int>>)

  private fun part1() {
    val inputs = File("/Users/dkhawk/Downloads/2021/input-19-sample.txt").readLines().filter(String::isNotBlank)

    val scanners = createScannerMap(inputs)

    val axes = scanners.map { scanner ->
      scanner.scans.pivot()
    }

    val firstScanner = axes.first()

    val corrections = firstScanner.mapNotNull { target ->
      bestAxis(target, axes[1])
    }

    if (corrections.size == 3) {
      val corrected = applyAxisCorrections(corrections, axes[1])
      val correctedScans = corrected.pivot()
      val commonBeacons = scanners.first().scans.toSet().intersect(correctedScans.toSet())
      println(commonBeacons)
    }
  }

  fun applyAxisCorrections(
    corrections: List<Correction>,
    originalScans: List<List<Int>>,
  ): List<List<Int>> {
    return corrections.map { correction ->
      val scans = originalScans[correction.axisNumber]
      scans.map { (it * correction.axisSign) + correction.correctionFactor }
    }
  }

  fun checkAxis(target: List<Int>, points: List<Int>): Pair<Int, Int> {
    val sortedTarget = target.sorted()
    val sortedPoints = points.sorted()

    val start = sortedTarget.first() - sortedPoints.last()
    val end = sortedTarget.last()

    val intersections = (start..end).map { offset ->
      val shifted = sortedPoints.map { it + offset }
      offset to sortedTarget.intersect(shifted.toSet())
    }.filter { it.second.isNotEmpty() }
    val bestFit = intersections.maxByOrNull { it.second.size }

    return bestFit!!.first to bestFit.second.size
  }

  fun createScannerMap(inputs: List<String>): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var scannerId = -1
    var scans = mutableListOf<List<Int>>()

    inputs.forEach { line ->
      if (line.contains("scanner")) {
        if (scannerId >= 0) {
          scanners.add(Scanner(scanners.size, scans))
        }
        scannerId = numberRegex.find(line)?.groups?.get(0)?.value?.toInt() ?: -1
        scans = mutableListOf()
      } else {
        scans.add(line.split(',').map(String::toInt))
      }
    }

    scanners.add(Scanner(scanners.size, scans))

    return scanners
  }

  data class Correction(val axisNumber: Int, val correctionFactor: Int, val axisSign: Int)

  fun bestAxis(target: List<Int>, axes: List<List<Int>>): Correction? {
    val fits = axes.flatMapIndexed { index, axis ->
      listOf(
        (index to 1) to checkAxis(target, axis),
        (index to -1) to checkAxis(target, axis.map { -it }),
      )
    }
    return fits.maxByOrNull { it.second.second }?.let { best ->
      if (best.second.second >= 12) {
        Correction(axisNumber = best.first.first,
                   correctionFactor = best.second.first,
                   axisSign = best.first.second)
      } else {
        null
      }
    }
  }
}

fun <E> List<List<E>>.pivot(): List<List<E>> {
  val dimensions = first().size

  val result = ArrayList<MutableList<E>>(dimensions)
  repeat(dimensions) {
    result.add(mutableListOf())
  }

  forEach{ list ->
    list.forEachIndexed { dimension, value ->
      result[dimension].add(value)
    }
  }

  return result
}

private fun List<Day19.VectorN>.unzip(): List<List<Int>> {
  return map { it.coords }.pivot()
}

private fun IntRange.range(): Int {
  return last - first
}

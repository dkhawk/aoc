package com.sphericalchickens.utils

class InputNew(private val year: Int, private val day: Int) {
  val baseDirectory: String get() = "app/src/main/resources/aoc$year"

  fun readAsLines(filterBlankLines: Boolean = true): List<String> {
      val fileName = "aoc$year/day${day.toString().padStart(2, '0')}_input.txt"
      return try {
          readInputLines(fileName)
      } catch (e: Exception) {
          emptyList()
      }
  }

  fun readAsString(): String {
      val fileName = "aoc$year/day${day.toString().padStart(2, '0')}_input.txt"
      return try {
          readInputText(fileName)
      } catch (e: Exception) {
          ""
      }
  }
}

object Input {
  fun readAsLines(day: Int, year: Int = 2021): List<String> =
      InputNew(year, day).readAsLines()

  fun readAsLines(dayStr: String, year: Int = 2021): List<String> =
      InputNew(year, dayStr.toInt()).readAsLines()

  fun readAsString(day: Int, year: Int = 2021): String =
      InputNew(year, day).readAsString()

  fun readAsString(dayStr: String, year: Int = 2021): String =
      InputNew(year, dayStr.toInt()).readAsString()
}

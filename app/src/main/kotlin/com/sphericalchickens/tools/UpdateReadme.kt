package com.sphericalchickens.tools

import java.io.File

object UpdateReadme {
    @JvmStatic
    fun main(args: Array<String>) {
        val runtimesFile = File("runtimes.txt")
        if (!runtimesFile.exists()) {
            println("Error: runtimes.txt not found. Run Benchmark first.")
            return
        }

        val readmeFile = File("../README.md")
        if (!readmeFile.exists()) {
            println("Error: README.md not found at ${readmeFile.absolutePath}")
            return
        }

        val runtimes = runtimesFile.readLines().map { line ->
            val parts = line.split("|")
            RuntimeEntry(
                year = parts[0].toInt(),
                day = parts[1].toInt(),
                part1 = parts[2],
                part2 = parts[3],
                total = parts[4]
            )
        }

        val tablesByYear = runtimes.groupBy { it.year }
            .toSortedMap(compareByDescending { it })
            .map { (year, entries) ->
                buildTable(year, entries)
            }.joinToString("\n\n")

        val readmeContent = readmeFile.readText()
        val newContent = updateReadmeContent(readmeContent, tablesByYear)
        
        readmeFile.writeText(newContent)
        println("README.md updated successfully.")
    }

    private fun buildTable(year: Int, entries: List<RuntimeEntry>): String {
        val sb = StringBuilder()
        sb.append("### $year\n\n")
        sb.append("| Day | Part 1 | Part 2 | Total |\n")
        sb.append("|:---:|:---:|:---:|:---:|\n")
        
        entries.sortedBy { it.day }.forEach { entry ->
            sb.append("| [Day ${entry.day}](app/src/main/kotlin/com/sphericalchickens/aoc$year/day${entry.day.toString().padStart(2, '0')}/Day${entry.day.toString().padStart(2, '0')}.kt) | ${entry.part1} | ${entry.part2} | ${entry.total} |\n")
        }
        
        return sb.toString()
    }

    private fun updateReadmeContent(content: String, newTables: String): String {
        val header = "## Runtimes"
        val start = content.indexOf(header)
        
        if (start == -1) {
            return "$content\n\n$header\n\n$newTables\n"
        }
        
        // Find the end of the Runtimes section. 
        // Assuming it ends at the next ## Header or end of file.
        // Actually, let's just replace everything after ## Runtimes
        // But wait, what if there are other sections after?
        // Let's assume Runtimes is at the end or we look for the next `## `
        
        val afterHeader = content.substring(start + header.length)
        val nextHeaderIndex = afterHeader.indexOf("\n## ")
        
        return if (nextHeaderIndex == -1) {
             content.substring(0, start + header.length) + "\n\n" + newTables + "\n"
        } else {
             content.substring(0, start + header.length) + "\n\n" + newTables + "\n" + afterHeader.substring(nextHeaderIndex)
        }
    }

    data class RuntimeEntry(
        val year: Int,
        val day: Int,
        val part1: String,
        val part2: String,
        val total: String
    )
}

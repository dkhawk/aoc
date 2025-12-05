package com.sphericalchickens.tools

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.toDuration

object Benchmark {
    @JvmStatic
    fun main(args: Array<String>) {
        val runtimesFile = File("runtimes.txt")
        // Clear file if running full benchmark, otherwise append? 
        // Actually, let's just append and let the user manage the file or overwrite if no args.
        if (args.isEmpty()) {
             runtimesFile.writeText("") // Clear file
        }

        val originalOut = System.out
        
        // Parse args
        // Format: "2025" or "2025 1"
        val targetYear = args.getOrNull(0)?.toIntOrNull()
        val targetDay = args.getOrNull(1)?.toIntOrNull()

        val years = if (targetYear != null) targetYear..targetYear else 2015..2025
        val days = if (targetDay != null) targetDay..targetDay else 1..25

        for (year in years) {
            for (day in days) {
                val dayString = day.toString().padStart(2, '0')
                val className = "com.sphericalchickens.aoc$year.day$dayString.Day${dayString}Kt"
                
                try {
                    val clazz = Class.forName(className)
                    val mainMethod = clazz.getMethod("main")
                    
                    println("Running $year Day $day...")
                    
                    // Capture stdout
                    val baos = ByteArrayOutputStream()
                    val newOut = PrintStream(baos)
                    System.setOut(newOut)
                    
                    var part1Time: String? = null
                    var part2Time: String? = null
                    
                    val totalTime = measureTime {
                        try {
                            mainMethod.invoke(null)
                        } catch (e: Exception) {
                            newOut.println("Error running $year Day $day: ${e.message}")
                            e.printStackTrace(newOut)
                        }
                    }
                    
                    System.setOut(originalOut)
                    val output = baos.toString()
                    
                    // Parse output for specific times
                    val part1Match = Regex("""Part 1 runtime: (.+)""").find(output)
                    if (part1Match != null) {
                        part1Time = part1Match.groupValues[1]
                    }
                    
                    val part2Match = Regex("""Part 2 runtime: (.+)""").find(output)
                    if (part2Match != null) {
                        part2Time = part2Match.groupValues[1]
                    }
                    
                    val totalTimeString = formatDuration(totalTime)
                    
                    // Format: YEAR|DAY|PART1|PART2|TOTAL
                    val resultLine = "$year|$day|${part1Time ?: ""}|${part2Time ?: ""}|$totalTimeString"
                    
                    // Write incrementally
                    runtimesFile.appendText(resultLine + "\n")
                    println("  -> $resultLine")
                    
                } catch (e: ClassNotFoundException) {
                    // Ignore if class not found
                } catch (e: Exception) {
                    System.setOut(originalOut)
                    println("Failed to load/run $className: ${e.message}")
                } finally {
                    System.setOut(originalOut)
                }
            }
        }
        
        println("Benchmark complete. Results written to runtimes.txt")
    }

    private fun formatDuration(duration: Duration): String {
        return if (duration.inWholeMilliseconds < 5) {
            "${duration.inWholeMilliseconds}ms (${duration.inWholeMicroseconds}µs)"
        } else {
            "${duration.inWholeMilliseconds}ms"
        }
    }
}

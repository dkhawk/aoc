package com.sphericalchickens.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*
import java.io.File

@Serializable
class Printer(val message: String) {
    fun printMessage() = runBlocking {
        val now: Instant = Clock.System.now()
        launch {
            delay(1000L)
            println(now.toString())
        }
        println(message)
    }
}

fun <E> Collection<E>.println() {
    println(
      this.joinToString(separator = "\n\t", prefix = "\t")
    )
}

fun Any.println() {
    println(this)
}

/**
 * Reads lines from a file in the `resources` directory.
 * This is a robust way to bundle input files with your application, as it avoids
 * hardcoded absolute paths.
 *
 * @param fileName The path to the file relative to the `resources` root.
 * @return A list of non-empty strings from the file.
 */
fun readInputLines(fileName: String): List<String> {
    val url = object {}.javaClass.classLoader.getResource(fileName)
        ?: error("File not found: $fileName. Make sure it is in `src/main/resources`.")
    return File(url.toURI()).readLines().filter(String::isNotBlank)
}

/**
 * Reads the entire content of a file from the `resources` directory as a single string.
 *
 * @param fileName The path to the file relative to the `resources` root.
 * @return The content of the file.
 */
fun readInputText(fileName: String): String {
    val url = object {}.javaClass.classLoader.getResource(fileName)
        ?: error("File not found: $fileName. Make sure it is in `src/main/resources`.")
    return File(url.toURI()).readText()
}

fun <T> check(message: String, expected: T, actual: T) {
    if (expected == actual) {
        println("✅ Test passed: $message")
    } else {
        System.err.println("❌ Test FAILED: $message")
        System.err.println("   Expected: $expected")
        System.err.println("   Actual:   $actual")
        error("Test failed: $message")
    }
}

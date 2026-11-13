package com.sphericalchickens.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*
import java.io.File
import kotlin.time.Duration

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
    check(message, expected, actual) { "" }
}

fun <T> check(message: String, expected: T, actual: T, lazyMessage: () -> Any) {
    if (java.util.Objects.deepEquals(expected, actual)) {
        println("✅ Test passed: $message")
    } else {
        val details = lazyMessage().toString()
        System.err.println("❌ Test FAILED: $message")
        System.err.println("   Expected: ${formatTestValue(expected)}")
        System.err.println("   Actual:   ${formatTestValue(actual)}")
        if (details.isNotBlank()) {
            System.err.println(details)
        }
        error("Test failed: $message")
    }
}

fun check(message: String, condition: Boolean) {
    check(message, condition) { "Assertion failed" }
}

fun check(message: String, condition: Boolean, lazyMessage: () -> Any) {
    if (condition) {
        println("✅ Test passed: $message")
    } else {
        val details = lazyMessage()
        System.err.println("❌ Test FAILED: $message")
        System.err.println(details)
        error("Test failed: $message")
    }
}

private fun formatTestValue(value: Any?): String = when (value) {
    is IntArray -> value.contentToString()
    is LongArray -> value.contentToString()
    is ShortArray -> value.contentToString()
    is ByteArray -> value.contentToString()
    is CharArray -> value.contentToString()
    is BooleanArray -> value.contentToString()
    is FloatArray -> value.contentToString()
    is DoubleArray -> value.contentToString()
    is Array<*> -> value.contentDeepToString()
    else -> value.toString()
}

fun formatDuration(duration: Duration): String {
    return if (duration.inWholeMilliseconds < 5) {
        "${duration.inWholeMilliseconds}ms (${duration.inWholeMicroseconds}µs)"
    } else {
        "${duration.inWholeMilliseconds}ms"
    }
}

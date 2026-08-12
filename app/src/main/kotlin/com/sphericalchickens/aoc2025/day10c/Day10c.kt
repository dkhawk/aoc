package com.sphericalchickens.aoc2025.day10c

import com.sphericalchickens.utils.formatDuration
import com.sphericalchickens.utils.readInputLines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.measureTimedValue

fun main() {
    // --- Development Workflow Control Panel ---
    val runPart1Tests = false
    val runPart1Solution = false
    val runPart2Tests = false
    val runPart2Solution = true
    // ----------------------------------------

    println("--- Advent of Code 2025, Day 10 (Day10c: Concurrent Monitored Search with Persistence) ---")

    val input = readInputLines("aoc2025/day10_input.txt")

    // --- Part 1 ---
    if (runPart1Tests) {
        println("🧪 Running Part 1 tests...")
        runPart1Tests()
        println("✅ Part 1 tests passed!\n")
    }
    if (runPart1Solution) {
        println("🎁 Solving Part 1...")
        val (part1Result, part1Duration) = measureTimedValue {
            part1(input)
        }
        println("   Part 1: $part1Result")
        println("Part 1 runtime: ${formatDuration(part1Duration)}\n")
    }

    // --- Part 2 ---
    if (runPart2Tests) {
        println("🧪 Running Part 2 tests...")
        runPart2Tests()
        println("✅ Part 2 tests passed!\n")
    }
    if (runPart2Solution) {
        println("🎀 Solving Part 2...")
        val (part2Result, part2Duration) = measureTimedValue {
            // Set machineTimeoutMs to 300_000L (5 minutes) or Long.MAX_VALUE for no timeout
            part2ParallelMonitoredWithCache(input, machineTimeoutMs = 300_000L)
        }
        println("\n   Part 2 Total (for solved machines): $part2Result")
        println("Part 2 runtime: ${formatDuration(part2Duration)}\n")
    }
}

// --- File Cache Utility ---

object MachineCache {
    private const val CACHE_FILE_PATH = "day10_cache.json"
    private val cache = ConcurrentHashMap<String, Long>()

    init {
        loadCache()
    }

    private fun loadCache() {
        val file = File(CACHE_FILE_PATH)
        if (!file.exists()) return

        file.useLines { lines ->
            lines.forEach { line ->
                val parts = line.split("::", limit = 2)
                if (parts.size == 2) {
                    val inputLine = parts[0]
                    val score = parts[1].toLongOrNull()
                    if (score != null) {
                        cache[inputLine] = score
                    }
                }
            }
        }
        println("💾 Loaded ${cache.size} previously solved machines from cache ($CACHE_FILE_PATH).")
    }

    fun get(line: String): Long? = cache[line]

    @Synchronized
    fun put(line: String, score: Long) {
        if (score <= 0L) return
        val currentBest = cache[line]
        if (currentBest == null || score < currentBest) {
            cache[line] = score
            saveCache()
        }
    }

    private fun saveCache() {
        val file = File(CACHE_FILE_PATH)
        file.bufferedWriter().use { writer ->
            cache.forEach { (line, score) ->
                writer.write("$line::$score\n")
            }
        }
    }
}

// --- Test Implementations ---

private fun runPart1Tests() {
    val sampleInput = listOf(
        "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
        "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
        "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"
    )
    val result = part1(sampleInput)
    check(result == 7) { "Part 1 sample failed: Expected 7, got $result" }
}

private fun runPart2Tests() {
    val sampleInput = listOf(
        "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}",
        "[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}",
        "[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}"
    )

    val answer = sampleInput.mapIndexed { idx, line ->
        println("\n--- Part 2 Test Problem #${idx + 1} ---")
        val (buttons, targets) = parseLine(line)
        solveMachineBifurcated(buttons, targets) { }
    }.sum()

    check(answer == 33L) { "Part 2 sample failed: Expected 33, got $answer" }
}

// --- Part 1 Implementation ---

private fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        val (targetMask, buttons) = parsePart1Line(line)
        solveMachinePart1(targetMask, buttons)
    }
}

private fun parsePart1Line(line: String): Pair<Int, List<Int>> {
    val targetStr = Regex("""\[([.#]+)\]""").find(line)!!.groupValues[1]
    var targetMask = 0
    targetStr.forEachIndexed { idx, ch ->
        if (ch == '#') {
            targetMask = targetMask or (1 shl idx)
        }
    }

    val buttons = Regex("""\(([^)]+)\)""").findAll(line).map { m ->
        val indices = m.groupValues[1].split(",").map { it.trim().toInt() }
        var mask = 0
        indices.forEach { idx -> mask = mask or (1 shl idx) }
        mask
    }.toList()

    return targetMask to buttons
}

private fun solveMachinePart1(targetMask: Int, buttons: List<Int>): Int {
    if (targetMask == 0) return 0

    val visited = mutableMapOf<Int, Int>()
    val queue = ArrayDeque<Int>()

    visited[0] = 0
    queue.add(0)

    while (queue.isNotEmpty()) {
        val curr = queue.poll()
        val dist = visited[curr]!!

        if (curr == targetMask) return dist

        for (buttonMask in buttons) {
            val next = curr xor buttonMask
            if (next !in visited) {
                visited[next] = dist + 1
                queue.add(next)
            }
        }
    }

    error("No solution found for Part 1 line")
}

// --- Machine Status Tracking Data Structures ---

enum class MachineState {
    CACHED_SOLVED,
    NO_SOLUTION_YET,
    SOLUTION_FOUND_RUNNING,
    COMPLETED,
    TIMED_OUT
}

data class MachineProgress(
    val id: Int,
    val line: String,
    val startTime: Long = System.currentTimeMillis(),
    @Volatile var state: MachineState = MachineState.NO_SOLUTION_YET,
    @Volatile var bestMin: Long = Long.MAX_VALUE,
    @Volatile var statesVisited: Long = 0L
)

// --- Part 2 Parallel Implementation with Dashboard & Cache ---

private fun part2ParallelMonitoredWithCache(
    input: List<String>,
    machineTimeoutMs: Long = 300_000L // 5-minute timeout per machine
): Long = runBlocking {
    val totalMachines = input.size
    val machineTracker = ConcurrentHashMap<Int, MachineProgress>()

    var cachedCount = 0
    input.forEachIndexed { idx, line ->
        val cachedValue = MachineCache.get(line)
        if (cachedValue != null) {
            machineTracker[idx + 1] = MachineProgress(
                id = idx + 1,
                line = line,
                state = MachineState.CACHED_SOLVED,
                bestMin = cachedValue
            )
            cachedCount++
        } else {
            machineTracker[idx + 1] = MachineProgress(id = idx + 1, line = line)
        }
    }

    val lastImprovementPrintTime = AtomicLong(0L)
    val totalExecStartTime = System.currentTimeMillis()

    println("🚀 Starting parallel solver across ${Runtime.getRuntime().availableProcessors()} CPU threads...")
    println("⚡ Skipped $cachedCount already solved machines from local cache.")
    val timeoutDesc = if (machineTimeoutMs == Long.MAX_VALUE) "Infinite (No Timeout)" else "${machineTimeoutMs / 1000}s"
    println("📊 Dashboard will report status every 5 seconds. Timeout threshold = $timeoutDesc.\n")

    // Background Status Monitor Coroutine
    val monitorJob = launch(Dispatchers.Default) {
        var elapsedSec = 0
        while (isActive) {
            delay(1000L)
            elapsedSec++

            if (elapsedSec % 5 == 0) {
                val cached = machineTracker.values.count { it.state == MachineState.CACHED_SOLVED }
                val noSolYet = machineTracker.values.count { it.state == MachineState.NO_SOLUTION_YET }
                val solFoundRunning = machineTracker.values.count { it.state == MachineState.SOLUTION_FOUND_RUNNING }
                val completed = machineTracker.values.count { it.state == MachineState.COMPLETED }
                val timedOut = machineTracker.values.count { it.state == MachineState.TIMED_OUT }

                val currentSum = machineTracker.values
                    .filter { it.bestMin != Long.MAX_VALUE }
                    .sumOf { it.bestMin }

                println("--------------------------------------------------------------------------------")
                println("⏱️  [DASHBOARD @ ${elapsedSec}s] Completed: ${completed + cached}/$totalMachines ($cached cached) | Running with Sol: $solFoundRunning | No Sol Yet: $noSolYet | Timed Out: $timedOut")
                println("   Current Sum of Best Solutions Found: $currentSum")
                println("--------------------------------------------------------------------------------")
            }

            if (elapsedSec >= 10 && elapsedSec % 10 == 0) {
                val now = System.currentTimeMillis()
                val slowJobs = machineTracker.values.filter {
                    (it.state == MachineState.NO_SOLUTION_YET || it.state == MachineState.SOLUTION_FOUND_RUNNING) &&
                            (now - it.startTime >= 10000L)
                }

                if (slowJobs.isNotEmpty()) {
                    println("⚠️  [SLOW JOBS > 10s] The following ${slowJobs.size} machine(s) are still actively searching:")
                    slowJobs.forEach { job ->
                        val durationSec = (now - job.startTime) / 1000
                        val bestStr = if (job.bestMin == Long.MAX_VALUE) "None" else "${job.bestMin} presses"
                        println("   • Machine #${job.id} (Running for ${durationSec}s) | State: ${job.state} | Best Found: $bestStr | States: ${job.statesVisited}")
                    }
                    println()
                }
            }
        }
    }

    // Launch worker coroutines only for uncached machines
    val deferredResults = input.mapIndexed { index, line ->
        val machineId = index + 1
        val progress = machineTracker[machineId]!!

        async(Dispatchers.Default) {
            if (progress.state == MachineState.CACHED_SOLVED) {
                return@async progress.bestMin
            }

            val (buttons, targets) = parseLine(line)
            val startTime = System.currentTimeMillis()

            val result = solveMachineBifurcated(
                buttons = buttons,
                targets = targets,
                onImprovement = { newMin ->
                    progress.bestMin = newMin
                    if (progress.state == MachineState.NO_SOLUTION_YET) {
                        progress.state = MachineState.SOLUTION_FOUND_RUNNING
                    }
                    MachineCache.put(line, newMin) // Instantly record progress to cache

                    val now = System.currentTimeMillis()
                    val last = lastImprovementPrintTime.get()
                    if (now - last >= 1000L && lastImprovementPrintTime.compareAndSet(last, now)) {
                        val currentTotalSum = machineTracker.values
                            .filter { it.bestMin != Long.MAX_VALUE }
                            .sumOf { it.bestMin }
                        println("💡 [Improvement] Machine #$machineId found better solution: $newMin presses! (Global Sum: $currentTotalSum)")
                    }
                },
                shouldCancel = {
                    if (machineTimeoutMs == Long.MAX_VALUE) false
                    else (System.currentTimeMillis() - startTime) > machineTimeoutMs
                },
                onStateUpdate = { states -> progress.statesVisited = states }
            )

            if (result == -1L) {
                progress.state = MachineState.TIMED_OUT
                val bestStr = if (progress.bestMin == Long.MAX_VALUE) "No solution" else "Best was ${progress.bestMin}"
                println("⛔ [Timeout > ${machineTimeoutMs / 1000}s] Machine #$machineId aborted ($bestStr).")
                if (progress.bestMin != Long.MAX_VALUE) progress.bestMin else 0L
            } else {
                progress.state = MachineState.COMPLETED
                progress.bestMin = result
                MachineCache.put(line, result) // Save finalized score
                result
            }
        }
    }

    val results = deferredResults.map { it.await() }
    monitorJob.cancel()

    val totalTimeSec = (System.currentTimeMillis() - totalExecStartTime) / 1000
    val finalSum = results.filter { it > 0L }.sum()

    val finalCompleted = machineTracker.values.count { it.state == MachineState.COMPLETED || it.state == MachineState.CACHED_SOLVED }
    val finalTimedOut = machineTracker.values.count { it.state == MachineState.TIMED_OUT }

    println("\n================================================================================")
    println("🎉 EXECUTION COMPLETE in ${totalTimeSec}s")
    println("   • Fully Completed / Cached Scenarios: $finalCompleted/$totalMachines ($cachedCount loaded from cache)")
    println("   • Timed Out Scenarios: $finalTimedOut/$totalMachines")
    println("   • Final Computed Sum (for solved machines): $finalSum")
    println("================================================================================\n")

    finalSum
}

fun parseLine(line: String): Pair<List<List<Int>>, List<Long>> {
    val buttons = Regex("""\(([^)]+)\)""").findAll(line).map { m ->
        m.groupValues[1].split(",").map { it.trim().toInt() }
    }.toList()

    val targets = Regex("""\{([^}]+)\}""").find(line)!!
        .groupValues[1].split(",").map { it.trim().toLong() }

    return buttons to targets
}

/**
 * Core Bifurcated Domain Solver with live improvement callbacks and cancellation support.
 */
fun solveMachineBifurcated(
    buttons: List<List<Int>>,
    targets: List<Long>,
    onImprovement: (Long) -> Unit = {},
    shouldCancel: () -> Boolean = { false },
    onStateUpdate: (Long) -> Unit = {}
): Long {
    val numCounters = targets.size
    val numButtons = buttons.size
    val buttonToCounters = buttons.map { it.toSet() }

    var bestMin = Long.MAX_VALUE
    var statesVisited = 0L

    fun branch(boundsL: LongArray, boundsU: LongArray) {
        if (shouldCancel()) return

        statesVisited++
        if (statesVisited and 1023L == 0L) {
            onStateUpdate(statesVisited)
        }

        // 1. Lower Bound Pruning
        val minPossiblePresses = boundsL.sum()
        if (minPossiblePresses >= bestMin) return

        // 2. Feasibility Check (Lower bounds sum)
        val counterSumsL = LongArray(numCounters) { c ->
            var sum = 0L
            for (j in 0 until numButtons) {
                if (c in buttonToCounters[j]) sum += boundsL[j]
            }
            sum
        }
        if ((0 until numCounters).any { c -> counterSumsL[c] > targets[c] }) return

        // 3. Feasibility Check (Upper bounds sum)
        val counterSumsU = LongArray(numCounters) { c ->
            var sum = 0L
            for (j in 0 until numButtons) {
                if (c in buttonToCounters[j]) sum += boundsU[j]
            }
            sum
        }
        if ((0 until numCounters).any { c -> counterSumsU[c] < targets[c] }) return

        // 4. Base Case: Fixed Point (boundsL == boundsU)
        if (boundsL.contentEquals(boundsU)) {
            if (counterSumsL.contentEquals(targets.toLongArray())) {
                val totalPresses = boundsL.sum()
                if (totalPresses < bestMin) {
                    bestMin = totalPresses
                    onImprovement(bestMin)
                }
            }
            return
        }

        // 5. Bifurcation Step: Select the variable with the largest range [L_j, U_j]
        var splitVar = 0
        var maxRange = -1L
        for (j in 0 until numButtons) {
            val range = boundsU[j] - boundsL[j]
            if (range > maxRange) {
                maxRange = range
                splitVar = j
            }
        }

        if (boundsL[splitVar] == boundsU[splitVar]) return

        val mid = (boundsL[splitVar] + boundsU[splitVar]) / 2L

        // Branch 1 (Higher Half): [mid + 1, U]
        val rightL = boundsL.clone()
        rightL[splitVar] = mid + 1L
        val rightU = boundsU.clone()
        branch(rightL, rightU)

        // Branch 2 (Lower Half): [L, mid]
        if (!shouldCancel()) {
            val leftL = boundsL.clone()
            val leftU = boundsU.clone()
            leftU[splitVar] = mid
            branch(leftL, leftU)
        }
    }

    val initialL = LongArray(numButtons) { 0L }
    val initialU = LongArray(numButtons) { j ->
        val touchedCounters = buttonToCounters[j]
        if (touchedCounters.isEmpty()) 0L
        else touchedCounters.minOf { c -> targets[c] }
    }

    branch(initialL, initialU)

    return if (bestMin == Long.MAX_VALUE) -1L else bestMin
}
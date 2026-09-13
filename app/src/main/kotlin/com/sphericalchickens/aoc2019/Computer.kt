package com.sphericalchickens.aoc2019

import com.sphericalchickens.utils.*

import java.util.*
import kotlin.collections.HashMap

class Computer(
    initialState: List<Long>,
    val inputQueue: Queue<Long>,
    val outputQueue: Queue<Long>
) {
    companion object {
        const val RUNNING = 300
        const val DONE = 100
        const val PAUSED = 200

        const val DEBUG = 3
        const val INFO = 2
        const val VERBOSE = 1
        const val debugLevel = VERBOSE

        private val commandMap = HashMap<Int, Command>()

        abstract class Command(val name: String, val opcode: Int, val numParams: Int) {
            companion object {
                const val POSITION = 0
                const val IMMEDIATE = 1
                const val RELATIVE = 2

                fun getParameterModes(parameterMode: Int, numParams: Int) : List<Int> {
                    val pModes = ArrayList<Int>(numParams)
                    var divisor = 1

                    for (i in 0 until numParams) {
                        pModes.add((parameterMode / divisor) % 10)
                        divisor *= 10
                    }

                    return pModes
                }
            }

            abstract fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>) : Long?
        }

        init {
            var opcode2 = 1
            commandMap[opcode2] = object : Command("add", opcode2, 3) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val address = computer.getAddress(parameters[2], modes[2])
                    val op1 = computer.getValue(parameters[0], modes[0])
                    val op2 = computer.getValue(parameters[1], modes[1])

                    computer.storeValue(address, op1 + op2)
                    return null
                }
            }

            opcode2 = 2
            commandMap[opcode2] = object : Command("mult", opcode2, 3) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val address = computer.getAddress(parameters[2], modes[2])
                    val op1 = computer.getValue(parameters[0], modes[0])
                    val op2 = computer.getValue(parameters[1], modes[1])

                    computer.storeValue(address, op1 * op2)
                    return null
                }
            }

            opcode2 = 3
            commandMap[opcode2] = object : Command("read", opcode2, 1) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val address = computer.getAddress(parameters[0], modes[0])
                    val value = computer.readInput()
                    computer.storeValue(address, value)
                    return null
                }
            }

            opcode2 = 4
            commandMap[opcode2] = object : Command("write", opcode2, 1) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value = computer.getValue(parameters[0], modes[0])
                    computer.writeOutput(value)
                    return null
                }
            }

            opcode2 = 5
            commandMap[opcode2] = object : Command("jift", opcode2, 2) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value = computer.getValue(parameters[0], modes[0])
                    val address = computer.getValue(parameters[1], modes[1])
                    return if (value == 0L) {
                        null
                    } else {
                        address
                    }
                }
            }

            opcode2 = 6
            commandMap[opcode2] = object : Command("jiff", opcode2, 2) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value = computer.getValue(parameters[0], modes[0])
                    val address = computer.getValue(parameters[1], modes[1])
                    return if (value != 0L) {
                        null
                    } else {
                        address
                    }
                }
            }

            opcode2 = 7
            commandMap[opcode2] = object : Command("lt", opcode2, 3) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value1 = computer.getValue(parameters[0], modes[0])
                    val value2 = computer.getValue(parameters[1], modes[1])
                    val address = computer.getAddress(parameters[2], modes[2])
                    val result = if (value1 < value2) 1 else 0
                    computer.storeValue(address, result.toLong())
                    return null
                }
            }

            opcode2 = 8
            commandMap[opcode2] = object : Command("eq", opcode2, 3) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value1 = computer.getValue(parameters[0], modes[0])
                    val value2 = computer.getValue(parameters[1], modes[1])
                    val address = computer.getAddress(parameters[2], modes[2])
                    val result = if (value1 == value2) 1 else 0
                    computer.storeValue(address, result.toLong())
                    return null
                }
            }

            opcode2 = 9
            commandMap[opcode2] = object : Command("setrb", opcode2, 1) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    val modes = getParameterModes(parameterMode, numParams)
                    val value1 = computer.getValue(parameters[0], modes[0])
                    computer.adjustRelativeBase(value1)
                    return null
                }
            }

            opcode2 = 99
            commandMap[opcode2] = object : Command("halt", opcode2, 0) {
                override fun execute(computer: Computer, parameterMode: Int, parameters: List<Long>): Long? {
                    return null
                }
            }
        }
    }

    private fun getAddress(parameter: Long, mode: Int): Long {
        return when (mode) {
            Command.POSITION -> parameter
            Command.RELATIVE -> parameter + relativeBase
//            Command2.IMMEDIATE ->
            else -> throw RuntimeException("Illegal mode for address: $mode")
        }
    }

    private fun adjustRelativeBase(value: Long) {
        relativeBase += value.toInt()
    }

    // Use the given size or the 120% of the program size
//    val data = IntArray(if (size > 0) size else ((initialState.size * 5) / 4))
    val data = LongArray(initialState.size * 10)
    var pc = 0
    var relativeBase = 0
    var state = RUNNING

    init {
        data.fill(0, 0, data.lastIndex)
        initialState.forEachIndexed { index, value ->
            data[index] = value
        }
    }

    fun getValue(parameter: Long, mode: Int): Long {
        return when (mode) {
            0 -> if (parameter < data.size) data[parameter.toInt()] else -1
            1 -> parameter
            2 -> {
                val address = relativeBase + parameter
                if (address < data.size && address >= 0) {
                    data[address.toInt()]
                } else {
                    -1
                }
            }
            else -> throw RuntimeException("Illegal mode: $mode")
        }
    }

    fun storeValue(address: Long, value: Long) {
        data[address.toInt()] = value
    }

    fun getParameter(offset: Int): Long {
        return data[pc + offset]
    }

    fun readInput(): Long {
        return inputQueue.remove()
    }

    fun writeOutput(output: Long) {
        outputQueue.offer(output)
    }

    // start running the program
    fun execute(): Int {
        if (state == DONE) {
            return DONE
        }

        var lastDiff = -1

        while (true) {
            state = RUNNING
            val pcval = data[pc].toInt()
            val opcode = pcval % 100

            val dataCopy = data.slice(0..data.lastIndex)
            if (debugLevel >= DEBUG) {
                dumpMemory(lastDiff)
            }

            if (debugLevel >= DEBUG) {
                println("pc: $pc, pcval: $pcval, opcode: $opcode, pmodes: ${pcval / 100}, ")
            }

            // End program
            if (opcode == 99) {
                state = DONE
                return DONE
            }

            if (opcode == 3 && inputQueue.isEmpty()) {
                state = PAUSED
                return PAUSED
            }

            if (commandMap.containsKey(opcode)) {
                val cmd = commandMap[opcode]!!
                val parameters = data.slice((pc + 1)..(pc + 1 + cmd.numParams))
                val newPc = cmd.execute(this, pcval / 100, parameters)?.toInt()
                pc = newPc ?: (pc + cmd.numParams + 1)
            } else {
                throw RuntimeException("Illegal op code at $pc: ${data[pc]}")
            }

            if (debugLevel >= DEBUG) {
                lastDiff = -1
                dataCopy.toList().zip(data.toList()).forEachIndexed { index, pair ->
                    if (pair.first != pair.second) {
                        println("$index: ${pair.first} -> ${pair.second}")
                        lastDiff = index
                    }
                }
                println("======================")
            }
        }
    }

    fun dumpMemory(diff: Int) {
        val opcode = data[pc].toInt() % 100
        val cmd = commandMap[opcode] ?: return

        print("${cmd.name}: ")

        val programRangeEnd = pc + (cmd.numParams)
        val programStart = pc
        val programRange = programStart..programRangeEnd

        val dump = data.toList().mapIndexed { index, i ->
            var color = NO_COLOR
            if (index == pc && index == diff) {
                color = COLORS.MAGENTA.toString()
            } else if (index == diff) {
                color = COLORS.RED.toString()
            } else if (index == pc) {
                color = COLORS.BLUE.toString()
            } else if (index in programRange) {
                color = COLORS.GREEN.toString()
            }

            val accent = when {
                index % 10 == 0 -> ":"
                index % 5 == 0 -> "."
                else -> ""
            }
            "${color}${i}$accent${NO_COLOR}"
        }.joinToString(prefix = "[", postfix = "]")
        println(dump)
    }
}
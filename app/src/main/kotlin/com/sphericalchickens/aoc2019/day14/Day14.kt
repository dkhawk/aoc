package com.sphericalchickens.aoc2019.day14

import com.sphericalchickens.utils.*
import com.sphericalchickens.aoc2019.Computer

import java.io.File
import kotlin.math.ceil

class Day14 {
    companion object {
        val testInput1 = """
            10 ORE => 10 A
            1 ORE => 1 B
            7 A, 1 B => 1 C
            7 A, 1 C => 1 D
            7 A, 1 D => 1 E
            7 A, 1 E => 1 FUEL
        """.trimIndent().split("\n").map(String::trim)

        val testInput2 = """
            9 ORE => 2 A
            8 ORE => 3 B
            7 ORE => 5 C
            3 A, 4 B => 1 AB
            5 B, 7 C => 1 BC
            4 C, 1 A => 1 CA
            2 AB, 3 BC, 4 CA => 1 FUEL
        """.trimIndent().split("\n").map(String::trim)

        /*
        Consume 45 ORE to produce 10 A.
Consume 64 ORE to produce 24 B.
Consume 56 ORE to produce 40 C.
Consume 6 A, 8 B to produce 2 AB.
Consume 15 B, 21 C to produce 3 BC.
Consume 16 C, 4 A to produce 4 CA.
Consume 2 AB, 3 BC, 4 CA to produce 1 FUEL.
         */

        val testInput3 = """
            157 ORE => 5 NZVS
            165 ORE => 6 DCFZ
            44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
            12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
            179 ORE => 7 PSHF
            177 ORE => 5 HKGWZ
            7 DCFZ, 7 PSHF => 2 XJWVT
            165 ORE => 2 GPVTF
            3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
        """.trimIndent().split("\n").map(String::trim)

        val testInput4 = """
            2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
            17 NVRVD, 3 JNWZP => 8 VPVL
            53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
            22 VJHF, 37 MNCFX => 5 FWMGM
            139 ORE => 4 NVRVD
            144 ORE => 7 JNWZP
            5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
            5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
            145 ORE => 6 MNCFX
            1 NVRVD => 8 CXFTF
            1 VJHF, 6 MNCFX => 4 RFSQX
            176 ORE => 6 VJHF
        """.trimIndent().split("\n").map(String::trim)

        val testInput5 = """
            171 ORE => 8 CNZTR
            7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
            114 ORE => 4 BHXH
            14 VRPVC => 6 BMBT
            6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
            6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
            15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
            13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
            5 BMBT => 4 WPTQ
            189 ORE => 9 KTJDG
            1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
            12 VRPVC, 27 CNZTR => 2 XDBXC
            15 KTJDG, 12 BHXH => 5 XCVML
            3 BHXH, 2 VRPVC => 7 MZWV
            121 ORE => 7 VRPVC
            7 XCVML => 6 RJRHP
            5 BHXH, 4 VRPVC => 5 LTCX
        """.trimIndent().split("\n").map(String::trim)

        val part1Input = File("/Users/dkhawk/Downloads/2019/input-14.txt").readText().split("\n").map(String::trim)

        fun test() {
//            Day14().part1(testInput3)
//            Day14().part1(testInput5)
//            Day14().part2(testInput3)
//            Day14().part2(testInput5)
            Day14().part2(part1Input)
        }
    }

    data class Ingredient(val symbol: String, val quantity: Int)

    data class Reaction(val symbol: String, val yield: Int, val isBase: Boolean, val ingredients: List<Ingredient>)

    interface Source {
        fun get(quantity: Long, sources: HashMap<String, Source>)

        fun reset()
    }

    class Mine : Source {
        var totalExtracted = 0L
        override fun get(quantity: Long, sources: HashMap<String, Source>) {
            totalExtracted += quantity
        }

        override fun reset() {
            totalExtracted = 0
        }
    }

    class Factory(val symbol: String, val reaction: Reaction) : Source {
        var produced = 0L
        var stockpile = 0L
        override fun get(quantity: Long, sources: HashMap<String, Source>) {
            val howMuchDoWeNeedToMake = quantity - stockpile

            if (howMuchDoWeNeedToMake > 0) {
                val numReactions = ceil(howMuchDoWeNeedToMake.toDouble() / reaction.yield.toDouble()).toLong()
                reaction.ingredients.forEach { ingredient ->
                    sources[ingredient.symbol]!!.get(ingredient.quantity * numReactions, sources)
                }
                stockpile += numReactions * reaction.yield
                produced += numReactions * reaction.yield
            }

            stockpile -= quantity
        }

        override fun reset() {
            produced = 0
            stockpile = 0
        }
    }

    val sources = HashMap<String, Source>()

    val reactionMap = HashMap<String, Reaction>()

    private fun part1(input: List<String>) {
        val mine = createSources(input)

        val goal = "FUEL"

        val quantity = 1L
        craft(goal, quantity)

        println("Total ore needed: ${mine.totalExtracted}")
    }

    private fun part2(input: List<String>) {
        val mine = createSources(input)

        val goal = "FUEL"

        craft(goal, 1_000_000)
        val oreFor1M = mine.totalExtracted
        val orePerFuel = oreFor1M.toDouble() / 1_000_000
        println(oreFor1M)
        println(orePerFuel)

        val targetOre = 1_000_000_000_000

        var guess = (targetOre / orePerFuel).toLong()
        println("Guess is $guess")

        sources.values.forEach(Source::reset)
        craft(goal, guess)
        println("Used ${mine.totalExtracted} ore for ${guess} fuel")

        if (mine.totalExtracted > targetOre) {
            while (mine.totalExtracted  > targetOre) {
                sources.values.forEach(Source::reset)
                guess -= 1
                craft(goal, guess)
                println("Used ${mine.totalExtracted} ore for ${guess} fuel")
            }
        } else if (mine.totalExtracted < targetOre) {
            while (mine.totalExtracted  < targetOre) {
                sources.values.forEach(Source::reset)
                guess += 1
                craft(goal, guess)
                println("Used ${mine.totalExtracted} ore for ${guess} fuel")
            }
        }
    }

    private fun createSources(input: List<String>): Mine {
        val reactions = parseInput(input)
        println(reactions)

        val mine = Mine()
        sources["ORE"] = mine

        reactions.forEach { r ->
            val output = r.first
            val symbol = output.first
            val quantity = output.second

            val ingredients = r.second.map {
                Ingredient(it.first, it.second)
            }

            val isBase = (ingredients.size == 1 && ingredients[0].symbol == "ORE")

            reactionMap[symbol] = Reaction(symbol, quantity, isBase, ingredients)
        }

        reactionMap.forEach { (symbol, reaction) ->
            val factory = Factory(symbol, reaction)
            sources[symbol] = factory
        }
        return mine
    }

    private fun craft(goal: String, quantity: Long) {
        val source = sources[goal]!!
        source.get(quantity, sources)
    }

    private fun parseInput(input: List<String>): List<Pair<Pair<String, Int>, List<Pair<String, Int>>>> {
        return input.filter(String::isNotBlank).map { line ->
            val parts = line.split(" => ")
            val output = parts[1]
            val inputs = parts[0].split(", ")
            val allInputs = inputs.map {ingredient ->
                val s = ingredient.split(" ")
                Pair(s[1], s[0].toInt())
            }
            val o = output.split(" ")
            val out = Pair(o[1], o[0].toInt())
            Pair(out, allInputs)
        }
    }
}

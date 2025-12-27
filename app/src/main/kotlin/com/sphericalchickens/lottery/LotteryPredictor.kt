package com.sphericalchickens.lottery

import kotlin.random.Random

fun main() {
    // Configuration
    val entrantsWithTwoTickets = 209
    val entrantsWithOneTicket = 688
    val raceSpots = 300
    val totalSpots = 600 // Race + Waitlist
    val iterations = 1_000_000

    var myWinsRace = 0
    var myWinsWaitlist = 0
    var friendWinsRace = 0
    var friendWinsWaitlist = 0

    // Build the master ticket pool once (Integers represent Person IDs)
    // ID 0 = YOU (2 tickets)
    // ID 1 = FRIEND (1 ticket)
    // IDs 2..210 = Other 2-ticket holders
    // IDs 211..898 = Other 1-ticket holders
    val masterPool = ArrayList<Int>()

    // Add You
    masterPool.add(0); masterPool.add(0)
    // Add Friend
    masterPool.add(1)

    // Add others with 2 tickets
    for (i in 2..entrantsWithTwoTickets) {
        masterPool.add(i); masterPool.add(i)
    }
    // Add others with 1 ticket
    for (i in (entrantsWithTwoTickets + 1) until (entrantsWithTwoTickets + entrantsWithOneTicket)) {
        masterPool.add(i)
    }

    // Simulation Loop
    repeat(iterations) {
        // Fisher-Yates shuffle is standard for lottery randomness
        val currentDraw = masterPool.toMutableList()
        currentDraw.shuffle()

        val selectedPeople = HashSet<Int>()
        var peopleCount = 0

        for (ticketOwner in currentDraw) {
            // THE KEY LOGIC: Only count the person if they haven't been picked yet
            if (selectedPeople.add(ticketOwner)) {
                peopleCount++

                // Check if it's a Race Spot (1-300)
                if (peopleCount <= raceSpots) {
                    if (ticketOwner == 0) myWinsRace++
                    if (ticketOwner == 1) friendWinsRace++
                }
                // Check if it's a Waitlist Spot (301-600)
                else if (peopleCount <= totalSpots) {
                    if (ticketOwner == 0) myWinsWaitlist++
                    if (ticketOwner == 1) friendWinsWaitlist++
                }

                // Stop once we have filled the waitlist
                if (peopleCount == totalSpots) break
            }
        }
    }

    println("--- Results after $iterations iterations ---")
    println("YOU (2 Tickets):")
    println("  Race Spot: ${"%.2f".format(myWinsRace.toDouble() / iterations * 100)}%")
    println("  Waitlist:  ${"%.2f".format(myWinsWaitlist.toDouble() / iterations * 100)}%")
    println("  Total In:  ${"%.2f".format((myWinsRace + myWinsWaitlist).toDouble() / iterations * 100)}%")

    println("\nFRIEND (1 Ticket):")
    println("  Race Spot: ${"%.2f".format(friendWinsRace.toDouble() / iterations * 100)}%")
    println("  Waitlist:  ${"%.2f".format(friendWinsWaitlist.toDouble() / iterations * 100)}%")
    println("  Total In:  ${"%.2f".format((friendWinsRace + friendWinsWaitlist).toDouble() / iterations * 100)}%")
}
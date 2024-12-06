package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day05 {
    private val sampleRules = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13
    """.trimIndent().trimEnd('\n')

    private val sampleUpdates = """
        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent().trimEnd('\n')

    fun rules(str: String): Map<Int, Set<Int>> {
        return buildMap<Int, MutableSet<Int>> {
            for (line in str.lines()) {
                if (line.isNotEmpty()) {
                    val (page, follower) = line.split('|').map { it.toInt() }

                    val followers = getOrPut(page) { mutableSetOf() }
                    followers.add(follower)
                }
            }
        }
    }

    fun matches(update: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
        var previousNumbers = mutableSetOf<Int>()
        for (number in update) {
            val followers = rules[number]
            if (followers != null) {
                if (previousNumbers.any { it in followers }) {
                    return false
                }
            }

            previousNumbers.add(number)
        }

        return true
    }

    fun fix(update: List<Int>, rules: Map<Int, Set<Int>>): List<Int> {
        var fixed = update.toMutableList()

        var previousNumbers = mutableListOf<Int>()
        do {
            for (i in 0..<update.size) {
                val number = fixed[i]

                val followers = rules[number]
                if (followers != null) {
                    val index = previousNumbers.indexOfFirst { it in followers }
                    if (index != -1) {
                        // Move the current number to the index
                        fixed.add(index, fixed.removeAt(i))
                        break
                    }
                }

                previousNumbers.add(number)
            }

            previousNumbers.clear()
        } while (!matches(fixed, rules))

        return fixed
    }

    fun parse(updates: String): List<List<Int>> {
        return updates.lines().filter { it.isNotEmpty() }.map { line -> line.split(',').map { it.toInt() } }
    }

    fun middle(update: List<Int>): Int {
        return update[(update.size - 1) / 2]
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val rules = rules(sampleRules)
        val updates = parse(sampleUpdates)

        // When
        val matches = updates.filter { matches(it, rules) }
        for (match in matches) {
            println(match)
        }

        // Then
        assertEquals(143, matches.sumOf { middle(it) })
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val rules = rules(MR.assets.day05.rules.readText())
        val updates = parse(MR.assets.day05.updates.readText())

        // When
        val matches = updates.filter { matches(it, rules) }
        for (match in matches) {
            println(match)
        }

        // Then
        assertEquals(5713, matches.sumOf { middle(it) })
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val rules = rules(sampleRules)
        val updates = parse(sampleUpdates)

        // When
        val erroneous = updates.filterNot { matches(it, rules) }
        val fixed = erroneous.map { fix(it, rules) }

        // Then
        assertEquals(123, fixed.sumOf { middle(it) })
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val rules = rules(MR.assets.day05.rules.readText())
        val updates = parse(MR.assets.day05.updates.readText())

        // When
        val erroneous = updates.filterNot { matches(it, rules) }
        val fixed = erroneous.map { fix(it, rules) }

        // Then
        assertEquals(5180, fixed.sumOf { middle(it) })
    }
}

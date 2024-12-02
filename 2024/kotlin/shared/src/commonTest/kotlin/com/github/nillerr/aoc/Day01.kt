package com.github.nillerr.aoc

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class Day01 {
    @Test
    fun `part 1 - sample`() {
        // Given
        val input = MR.assets.day01.sample.readText()

        // When
        val (first, second) = partition(input)
        val distance = distance(first, second)

        // Then
        assertEquals(11, distance)
    }

    @Test
    fun `part 1 - puzzle`() {
        // Given
        val input = MR.assets.day01.input.readText()

        // When
        val (first, second) = partition(input)
        val distance = distance(first, second)

        // Then
        assertEquals(2580760, distance)
    }

    private fun distance(first: Int, second: Int): Int {
        return abs(first - second)
    }

    private fun distance(first: List<Int>, second: List<Int>): Int {
        return first.zip(second)
            .sumOf { (first, second) -> distance(first, second) }
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val input = MR.assets.day01.sample.readText()

        // When
        val (first, second) = partition(input)
        val similarity = similarity(first, second)

        // Then
        assertEquals(31, similarity)
    }

    @Test
    fun `part 2 - puzzle`() {
        // Given
        val input = MR.assets.day01.input.readText()

        // When
        val (first, second) = partition(input)
        val similarity = similarity(first, second)

        // Then
        assertEquals(25358365, similarity)
    }

    private fun similarity(first: List<Int>, second: List<Int>): Int {
        val occurrences = second.groupBy { it }.mapValues { it.value.size }
        return first.sumOf { it * (occurrences[it] ?: 0) }
    }
}

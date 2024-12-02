package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day02 {
    fun isSafeIncreasing(report: List<Int>): Boolean {
        return report.windowed(2).all { (a, b) -> a < b && b - a <= 3 }
    }

    fun isSafeDecreasing(report: List<Int>): Boolean {
        return report.windowed(2).all { (a, b) -> b < a && a - b <= 3 }
    }

    private fun isSafe(report: List<Int>): Boolean {
        val first = report.first()
        val last = report.last()

        return when {
            first < last -> isSafeIncreasing(report)
            last < first -> isSafeDecreasing(report)
            else -> false
        }
    }

    private fun parseReports(input: String): Sequence<List<Int>> {
        return input.lineSequence()
            .filter { it.isNotBlank() }
            .map { line -> line.split(" ").map { it.toInt() } }
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val input = MR.assets.day02.sample.readText()

        // When
        val reports = parseReports(input)
        val safe = reports.filter { isSafe(it) }.toList()

        // Then
        val expected = listOf(
            listOf(7, 6, 4, 2, 1),
            listOf(1, 3, 6, 7, 9),
        )

        assertEquals(expected, safe)
    }

    @Test
    fun `part 1 - puzzle`() {
        // Given
        val input = MR.assets.day02.input.readText()

        // When
        val reports = parseReports(input)
        val safe = reports.filter { isSafe(it) }.count()

        // Then
        assertEquals(246, safe)
    }
}

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

    fun isDampenedSafeIncreasing(report: List<Int>): Boolean {
        // [20, 22, 22, 23, 25, 29], false

        var good = report[0]

        var dampened = false

        for (i in 1 until report.size) {
            val cursor = report[i]
            val diff = cursor - good
            if (diff >= 1 && diff <= 3) {
                good = cursor
            } else if (!dampened) {
                dampened = true
            } else {
                return false
            }
        }

        return true
    }

    fun isDampenedSafeDecreasing(report: List<Int>): Boolean {
        var good = report[0]

        var dampened = false

        for (i in 1 until report.size) {
            val cursor = report[i]
            val diff = good - cursor
            if (diff >= 1 && diff <= 3) {
                good = cursor
            } else if (!dampened) {
                dampened = true
            } else {
                return false
            }
        }

        return true
    }

    private fun isDampenedSafe(report: List<Int>): Boolean {
        val first = report.first()
        val last = report.last()

        return when {
            first < last -> isDampenedSafeIncreasing(report) || isDampenedSafeDecreasing(report.asReversed())
            last < first -> isDampenedSafeDecreasing(report) || isDampenedSafeIncreasing(report.asReversed())
            else -> false
        }
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val input = MR.assets.day02.sample.readText()

        // When
        val reports = parseReports(input)
        val safe = reports.filter { isDampenedSafe(it) }.toList()

        // Then
        val expected = listOf(
            listOf(7, 6, 4, 2, 1),
            listOf(1, 3, 2, 4, 5),
            listOf(8, 6, 4, 4, 1),
            listOf(1, 3, 6, 7, 9),
        )

        assertEquals(expected, safe)
    }

    @Test
    fun `part 2 - puzzle`() {
        // Given
        val input = MR.assets.day02.input.readText()

        // When
//        val reports = listOf(
//            listOf(20, 22, 22, 23, 25, 28),
//            listOf(20, 22, 22, 22, 25, 29),
//            listOf(20, 22, 22, 23, 25, 29),
//            listOf(28, 25, 23, 22, 22, 20),
//            listOf(29, 25, 22, 22, 22, 20),
//            listOf(29, 25, 23, 22, 22, 20),
//            listOf(7, 10, 8, 10, 11),
//            listOf(29, 28, 27, 25, 26, 25, 22, 20),
//        )
        val reports = parseReports(input)
        val safe = reports.count { isDampenedSafe(it) }

        // Then
        assertEquals(318, safe)
    }
}

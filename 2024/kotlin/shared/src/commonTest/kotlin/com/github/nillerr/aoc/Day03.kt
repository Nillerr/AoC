package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day03 {
    @Test
    fun `part 1 - sample`() {
        // Given
        val input = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"

        // When
        val result = process(input)

        // Then
        assertEquals(161, result)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val input = MR.assets.day03.input.readText()

        // When
        val result = process(input)

        // Then
        assertEquals(187833789, result)
    }

    private fun process(input: String): Int {
        val pattern = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

        val result = pattern.findAll(input)
            .map { it.destructured }
            .sumOf { (lhs, rhs) -> lhs.toInt() * rhs.toInt() }

        return result
    }
    @Test
    fun `part 2 - sample`() {
        // Given
        val input = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

        // When
        val result = processConditional(input)

        // Then
        assertEquals(48, result)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val input = MR.assets.day03.input.readText()

        // When
        val result = processConditional(input)

        // Then
        assertEquals(94455185, result)
    }

    private fun processConditional(input: String): Int {
        val pattern = Regex("""do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\)""")

        var result = 0
        var isEnabled = true

        pattern.findAll(input).forEach { match ->
            when (match.value) {
                "do()" -> isEnabled = true
                "don't()" -> isEnabled = false
                else -> {
                    if (isEnabled) {
                        val (lhs, rhs) = match.destructured
                        result += lhs.toInt() * rhs.toInt()
                    }
                }
            }
        }

        return result
    }
}

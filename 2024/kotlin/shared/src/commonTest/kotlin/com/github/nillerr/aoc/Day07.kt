package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

fun Long.pow(exponent: Long): Long {
    return when (exponent) {
        0L -> 1L
        1L -> this
        else -> this * pow(exponent - 1)
    }
}

enum class Operator {
    ADD,
    MULTIPLY,
    CONCATENATE,
    ;

    fun apply(a: Long, b: Long): Long {
        return when (this) {
            ADD -> a + b
            MULTIPLY -> a * b
            CONCATENATE -> "$b$a".toLong()
        }
    }

    fun format(a: String, b: String): String {
        return when (this) {
            ADD -> "$a + $b"
            MULTIPLY -> "$a * $b"
            CONCATENATE -> "$a || $b"
        }
    }
}

sealed interface LongExpression {
    fun evaluate(): Long
}

data class LongValueExpression(val value: Long) : LongExpression {
    override fun evaluate(): Long {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}

data class BinaryLongExpression(val left: LongExpression, val operator: Operator, val right: LongExpression) :
    LongExpression {
    override fun evaluate(): Long {
        return operator.apply(left.evaluate(), right.evaluate())
    }

    override fun toString(): String {
        return "(${operator.format(left.toString(), right.toString())})"
    }
}


class Day07 {
    data class CalibrationEquation(val result: Long, val components: List<LongExpression>) {
        companion object {
            fun parse(line: String): CalibrationEquation {
                val (result, components) = line.split(": ")
                return CalibrationEquation(result.toLong(), components.split(" ").map { LongValueExpression(it.toLong()) })
            }
        }

        override fun toString(): String {
            return "$result: ${components.joinToString(" ")}"
        }
    }

    fun permutations(operators: List<Operator>, components: List<LongExpression>): List<LongExpression> {
        return when (components.size) {
            0 -> emptyList()
            1 -> listOf(components[0])

            else -> operators
                .flatMap { operator ->
                    permutations(operators, components.subList(1, components.size))
                        .map { permutation -> BinaryLongExpression(components[0], operator, permutation) }
                }
        }
    }

    private fun totalCalibrationResultOf(operators: List<Operator>, equations: List<CalibrationEquation>): Long {
        return equations
            .asSequence()
            .flatMap { equation ->
                permutations(operators, equation.components.asReversed())
                    .filter { permutation -> permutation.evaluate() == equation.result }
                    .map { permutation -> equation }
                    .distinct()
            }
            .sumOf { it.result }
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val input = MR.assets.day07.sample.readText().trim()
        val equations = input.lines().map { CalibrationEquation.parse(it) }

        // When
        val result = totalCalibrationResultOf(listOf(Operator.ADD, Operator.MULTIPLY), equations)

        // Then
        assertEquals(3749L, result)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val input = MR.assets.day07.input.readText().trim()
        val equations = input.lines().map { CalibrationEquation.parse(it) }

        // When
        val result = totalCalibrationResultOf(listOf(Operator.ADD, Operator.MULTIPLY), equations)

        // Then
        assertEquals(2314935962622L, result)
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val input = MR.assets.day07.sample.readText().trim()
        val equations = input.lines().map { CalibrationEquation.parse(it) }

        // When
        val result = totalCalibrationResultOf(listOf(Operator.ADD, Operator.MULTIPLY, Operator.CONCATENATE), equations)

        // Then
        assertEquals(11387L, result)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val input = MR.assets.day07.input.readText().trim()
        val equations = input.lines().map { CalibrationEquation.parse(it) }

        // When
        val result = totalCalibrationResultOf(listOf(Operator.ADD, Operator.MULTIPLY, Operator.CONCATENATE), equations)

        // Then
        assertEquals(401477450831495L, result)
    }
}

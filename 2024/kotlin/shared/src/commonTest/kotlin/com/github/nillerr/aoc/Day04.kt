package com.github.nillerr.aoc

import com.github.nillerr.aoc.matrix.masked
import com.github.nillerr.aoc.matrix.toCharMatrix
import kotlin.test.Test
import kotlin.test.assertEquals

class Day04 {
    val part1Masks = listOf("XMAS", "X\nM\nA\nS", "X...\n.M..\n..A.\n...S", "...X\n..M.\n.A..\nS...")
        .let { masks -> masks + masks.map { mask -> mask.reversed() } }
        .map { it.toCharMatrix() }

    val part2Masks = listOf(
        """
            M.S
            .A.
            M.S
        """.trimIndent(),
        """
            M.M
            .A.
            S.S
        """.trimIndent()
    )
        .let { masks -> masks + masks.map { mask -> mask.reversed() } }
        .map { it.toCharMatrix() }

    private val sampleInput = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent().trimEnd('\n').toCharMatrix()

    @Test
    fun `part 1 - sample`() {
        // When
        val (masked, result) = sampleInput.masked(part1Masks, '.')
        println(masked)

        // Then
        assertEquals(
            masked.toString().trimEnd('\n'),
            """
                ....XXMAS.
                .SAMXMS...
                ...S..A...
                ..A.A.MS.X
                XMASAMX.MM
                X.....XA.A
                S.S.S.S.SS
                .A.A.A.A.A
                ..M.M.M.MM
                .X.X.XMASX
            """.trimIndent()
        )
        assertEquals(18, result)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val input = MR.assets.day04.input.readText().trimEnd('\n').toCharMatrix()

        // When
        val (masked, result) = input.masked(part1Masks, '.')
        println(masked)

        // Then
        assertEquals(2462, result)
    }

    @Test
    fun `part 2 - sample`() {
        // When
        val (masked, result) = sampleInput.masked(part2Masks, '.')
        println(masked)

        // Then
        assertEquals(
            masked.toString().trimEnd('\n'),
            """
                .M.S......
                ..A..MSMS.
                .M.S.MAA..
                ..A.ASMSM.
                .M.S.M....
                ..........
                S.S.S.S.S.
                .A.A.A.A..
                M.M.M.M.M.
                ..........
            """.trimIndent()
        )
        assertEquals(9, result)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val input = MR.assets.day04.input.readText().trimEnd('\n').toCharMatrix()

        // When
        val (masked, result) = input.masked(part2Masks, '.')
        println(masked)

        // Then
        assertEquals(1877, result)
    }
}

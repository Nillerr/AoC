package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day08 {
    data class Node(val x: Int, val y: Int, val frequency: Char) {
        override fun toString(): String {
            return "$frequency ($x, $y)"
        }
    }

    fun antinodesOf(input: CharGrid): Set<Vector2> {
        val nodesByFrequency = input.groupByNotNull({ x, y, frequency -> frequency.takeUnless { it == '.' } }, ::Node)

        return buildSet {
            for ((_, nodes) in nodesByFrequency) {
                if (nodes.size == 1) {
                    continue
                }

                for (node in nodes) {
                    for (other in nodes) {
                        if (node == other) {
                            continue
                        }

                        val dx = other.x - node.x
                        val dy = other.y - node.y

                        val ax = other.x + dx
                        val ay = other.y + dy

                        if (input.contains(ax, ay)) {
                            add(Vector2(ax, ay))
                        }
                    }
                }
            }
        }
    }

    fun antinodesOf2(input: CharGrid): Set<Vector2> {
        val nodesByFrequency = input.groupByNotNull({ x, y, frequency -> frequency.takeUnless { it == '.' } }, ::Node)

        return buildSet {
            for ((_, nodes) in nodesByFrequency) {
                if (nodes.size == 1) {
                    continue
                }

                for (node in nodes) {
                    add(Vector2(node.x, node.y))

                    for (other in nodes) {
                        if (node == other) {
                            continue
                        }

                        val dx = other.x - node.x
                        val dy = other.y - node.y

                        var ax = other.x + dx
                        var ay = other.y + dy

                        while (input.contains(ax, ay)) {
                            add(Vector2(ax, ay))
                            ax += dx
                            ay += dy
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val grid = MR.assets.day08.sample.readText().trim().let(CharGrid::parse)
        println(grid)

        // When
        val result = antinodesOf(grid)
        result.forEach { (x, y) ->
            grid[x, y] = '#'
        }
        println(grid)

        // Then
        assertEquals(14, result.size)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val grid = MR.assets.day08.input.readText().trim().let(CharGrid::parse)
        println(grid)

        // When
        val result = antinodesOf(grid)
        result.forEach { (x, y) ->
            grid[x, y] = '#'
        }
        println(grid)

        // Then
        assertEquals(273, result.size)
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val grid = MR.assets.day08.sample.readText().trim().let(CharGrid::parse)
        println(grid)

        // When
        val result = antinodesOf2(grid)
        result.forEach { (x, y) ->
            if (grid[x, y] == '.') {
                grid[x, y] = '#'
            }
        }
        println(grid)

        // Then
        assertEquals(34, result.size)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val grid = MR.assets.day08.input.readText().trim().let(CharGrid::parse)
        println(grid)

        // When
        val result = antinodesOf2(grid)
        result.forEach { (x, y) ->
            if (grid[x, y] == '.') {
                grid[x, y] = '#'
            }
        }
        println(grid)

        // Then
        assertEquals(1017, result.size)
    }
}

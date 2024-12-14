package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day10 {
    fun CharGrid.score(): Int {
        var score = 0

        val stack = mutableListOf<Vector2>()

        val trailheads = locations('0')
        stack.addAll(trailheads)

        lateinit var trailhead: Vector2

        val summits = mutableSetOf<Pair<Vector2, Vector2>>()

        while (stack.isNotEmpty()) {
            val hereLoc = stack.removeLast()
            val (hx, hy) = hereLoc

            val here = this[hx, hy]
            if (here == '0') {
                score += summits.size

                trailhead = hereLoc
                summits.clear()
            }

            if (here == '9') {
                summits.add(trailhead to hereLoc)
                continue
            }

            val next = here + 1

            for (direction in Direction.entries) {
                val tx = direction.vector.applyX(hx)
                val ty = direction.vector.applyY(hy)

                if (contains(tx, ty)) {
                    val there = this[tx, ty]
                    if (there == next) {
                        val thereLoc = Vector2(tx, ty)
                        stack.add(thereLoc)
                    }
                }
            }
        }

        score += summits.size

        return score
    }

    data class Node(val elevation: Char, val location: Vector2, val children: List<Node>) {
        fun rating(): Int {
            return when (elevation) {
                '9' -> 1
                 else -> children.sumOf { it.rating() }
            }
        }
    }

    data class Tree(val nodes: List<Node>) {
        fun rating(): Int {
            return nodes.sumOf { it.rating() }
        }
    }

    fun CharGrid.childNodes(from: Vector2, next: Char): List<Node> {
        return buildList {
            for (direction in Direction.entries) {
                val tx = direction.vector.applyX(from.x)
                val ty = direction.vector.applyY(from.y)

                if (contains(tx, ty)) {
                    val there = get(tx, ty)
                    if (there == next) {
                        val thereLoc = Vector2(tx, ty)
                        val children = childNodes(thereLoc, next + 1)
                        val child = Node(there, thereLoc, children)
                        add(child)
                    }
                }
            }
        }
    }

    fun CharGrid.buildTree(): Tree {
        val nodes = buildList {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val here = get(x, y)
                    if (here == '0') {
                        val hereLoc = Vector2(x, y)
                        val children = childNodes(hereLoc, here + 1)
                        val child = Node(here, hereLoc, children)
                        add(child)
                    }
                }
            }
        }

        return Tree(nodes)
    }

    fun CharGrid.rating(): Int {
        val tree = buildTree()
        return tree.rating()
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val grid = MR.assets.day10.sample.readText().trim().let(CharGrid::parse)

        // When
        val score = grid.score()

        // Then
        assertEquals(36, score)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val grid = MR.assets.day10.input.readText().trim().let(CharGrid::parse)

        // When
        val score = grid.score()

        // Then
        assertEquals(496, score)
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val grid = MR.assets.day10.sample.readText().trim().let(CharGrid::parse)

        // When
        val score = grid.rating()

        // Then
        assertEquals(81, score)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val grid = MR.assets.day10.input.readText().trim().let(CharGrid::parse)

        // When
        val score = grid.rating()

        // Then
        assertEquals(1120, score)
    }
}

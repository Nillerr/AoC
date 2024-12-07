package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day06 {

    data class Vector2(val x: Int, val y: Int)

    enum class Direction {
        NORTH, SOUTH, EAST, WEST
    }

    class Guard(var x: Int, var y: Int, var direction: Direction) {
        val char: Char
            get() = when (direction) {
                Direction.NORTH -> '^'
                Direction.SOUTH -> 'v'
                Direction.EAST -> '>'
                Direction.WEST -> '<'
            }

        fun update(state: GameState): Boolean {
            when (direction) {
                Direction.NORTH -> {
                    val next = state.map.get(x, y - 1)
                    when (next) {
                        null -> return false
                        is Obstructed -> {
                            direction = Direction.EAST
                            return true
                        }
                        else -> {
                            y -= 1
                            return true
                        }
                    }
                }
                Direction.SOUTH -> {
                    val next = state.map.get(x, y + 1)
                    when (next) {
                        null -> return false
                        is Obstructed -> {
                            direction = Direction.WEST
                            return true
                        }
                        else -> {
                            y += 1
                            return true
                        }
                    }
                }
                Direction.EAST -> {
                    val next = state.map.get(x + 1, y)
                    when (next) {
                        null -> return false
                        is Obstructed -> {
                            direction = Direction.SOUTH
                            return true
                        }
                        else -> {
                            x += 1
                            return true
                        }
                    }
                }
                Direction.WEST -> {
                    val next = state.map.get(x - 1, y)
                    when (next) {
                        null -> return false
                        is Obstructed -> {
                            direction = Direction.NORTH
                            return true
                        }
                        else -> {
                            x -= 1
                            return true
                        }
                    }
                }
            }
        }
    }

    sealed interface Tile {
        val char: Char
    }

    sealed interface Obstructed : Tile

    data object Obstruction : Obstructed {
        override val char: Char = '#'
    }

    data object Floor : Tile {
        override val char: Char = '.'
    }

    inline fun <reified T> Grid(grid: MutableGrid<T>) = MutableGrid(
        grid.width,
        grid.height,
        Array(grid.width * grid.height) { index ->
            grid.get(1 - index % grid.width, index / grid.height)
        },
    )

    class MutableGrid<T>(
        val width: Int,
        val height: Int,
        private val cells: Array<T>,
    ) {
        companion object {
            inline fun <reified T> parse(input: String, noinline factory: (Char) -> T): MutableGrid<T> {
                return parse(::arrayOfNulls, input, factory)
            }

            @Suppress("UNCHECKED_CAST")
            fun <T> parse(destination: (Int) -> Array<T?>, input: String, factory: (Char) -> T): MutableGrid<T> {
                var width: Int = -1
                var height: Int = -1

                for (i in 0 until input.length) {
                    val c = input[i]
                    if (c == '\n') {
                        if (width == -1) {
                            width = i + 1
                        }

                        if (i > 0 && input[i - 1] != '\n') {
                            height += 1
                        }
                    }
                }

                val storage = destination(width * height)

                for (i in 0 until input.length) {
                    val c = input[i]
                    if (c != '\n') {
                        storage[i] = factory(c)
                    }
                }

                return MutableGrid(width, height, storage as Array<T>)
            }
        }

        init {
            require(cells.size >= width * height) { "The cells cannot fit the size of the grid." }
        }

        private fun index(loc: Vector2): Int {
            return loc.y * width + loc.x
        }

        fun get(loc: Vector2): T? {
            return cells.getOrNull(index(loc))
        }

        fun set(loc: Vector2, cell: T) {
            cells[index(loc)] = cell
        }

        private fun index(x: Int, y: Int): Int {
            return y * width + x
        }

        fun get(x: Int, y: Int): T? {
            return cells.getOrNull(index(x, y))
        }

        fun set(x: Int, y: Int, cell: T) {
            cells[index(x, y)] = cell
        }

        override fun toString(): String {
            return buildString {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val cell = cells[index(x, y)]
                        append(cell)
                    }

                    appendLine()
                }
            }
        }
    }

    interface GameListener {
        fun onGuardMoved(x: Int, y: Int)
    }

    class GameState(
        var map: MutableGrid<Tile>,
        var guards: MutableList<Guard>,
    ) {
        fun play(listener: GameListener) {
            while (true) {
                var changed: Boolean = false

                for (guard in guards) {
                    if (!guard.update(this)) {
                        changed = true
                    }
                }

                if (!changed) {
                    break
                }
            }
        }
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val input = MR.assets.day06.sample.readText().trimEnd()

        val map = Grid(input)

        // When
        val (result, presentation) = simulate(map)
        println(presentation)

        // Then
        assertEquals(41, result)
    }
}

package com.github.nillerr.aoc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals

class Day06 {
    class CharGrid(val width: Int, val height: Int, val storage: CharArray) {
        private fun index(x: Int, y: Int) = y * width + x

        fun contains(x: Int, y: Int): Boolean {
            return x >= 0 && x < width && y >= 0 && y < height
        }

        fun get(x: Int, y: Int): Char {
            return storage[index(x, y)]
        }

        fun set(x: Int, y: Int, char: Char) {
            storage[index(x, y)] = char
        }

        fun copy(): CharGrid {
            return CharGrid(width, height, storage.copyOf())
        }

        inline fun forEach(operation: (Int, Int, Char) -> Unit) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    operation(x, y, get(x, y))
                }
            }
        }

        fun locations(char: Char): List<Vector2> {
            return buildList {
                forEach { x, y, c ->
                    if (c == char) {
                        add(Vector2(x, y))
                    }
                }
            }
        }

        fun count(char: Char): Int {
            var count = 0
            forEach { x, y, c ->
                if (char == c) {
                    count++
                }
            }
            return count
        }

        override fun toString(): String {
            return buildString {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val c = get(x, y)
                        if (c == '.') {
                            append(' ')
                        } else {
                        append(c)
                        }
                    }

                    appendLine()
                }
            }
        }

        companion object {
            fun parse(str: String, separator: String = "\n"): CharGrid {
                val storage = str.split(separator).filter { it.isNotEmpty() }.flatMap { it.toList() }.toCharArray()
                val width = str.indexOf(separator)
                val height = storage.size / width
                return CharGrid(width, height, storage)
            }
        }
    }

    data class Vector2(val x: Int, val y: Int) {
        fun applyX(x: Int) = x + this.x
        fun applyY(y: Int) = y + this.y
    }

    enum class Direction(val vector: Vector2, val char: Char) {
        NORTH(Vector2(0, -1), '^'),
        EAST(Vector2(1, 0), '>'),
        SOUTH(Vector2(0, 1), 'v'),
        WEST(Vector2(-1, 0), '<'),
        ;

        fun clockwise(): Direction {
            return when (this) {
                NORTH -> EAST
                EAST -> SOUTH
                SOUTH -> WEST
                WEST -> NORTH
            }
        }

        companion object {
            fun valueOf(char: Char): Direction? {
                return when (char) {
                    '^' -> NORTH
                    '>' -> EAST
                    'v' -> SOUTH
                    '<' -> WEST
                    else -> null
                }
            }
        }
    }

    class GuardLoopException(val position: Vector2, val direction: Direction, val state: CharGrid) : Exception()

    fun simulate(input: CharGrid): CharGrid {
        val height = input.height
        val width = input.width

        var currentState = input.copy()

        var steps = 0
        val turns = mutableSetOf<Pair<Vector2, Direction>>()

        var finished = false

        do {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val c = currentState.get(x, y)

                    val direction = Direction.valueOf(c)
                    if (direction == null) {
                        continue
                    }

                    val position = Vector2(x, y)

                    val nextX = direction.vector.applyX(x)
                    val nextY = direction.vector.applyY(y)

                    if (currentState.contains(nextX, nextY)) {
                        val next = currentState.get(nextX, nextY)
                        currentState.set(x, y, 'X')

                        if (next == '#' || next == 'O') {
                            val newDirection = direction.clockwise()
                            currentState.set(x, y, newDirection.char)

                            if (!turns.add(position to newDirection)) {
                                throw GuardLoopException(position, direction, currentState)
                            }
                        } else {
                            steps += 1
                            currentState.set(nextX, nextY, direction.char)
                        }
                    } else {
                        currentState.set(x, y, 'X')

                        // Re-run simulate after placing an obstacle at every place in the initial path to see if
                        // it leads to a loop
                        // Maybe add better loop detection (although it's already good in `visitedVectors`
                        finished = true
                    }

                    if (finished) {
                        break
                    }
                }

                if (finished) {
                    break
                }
            }
        } while (!finished)

        return currentState
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val input = MR.assets.day06.sample.readText().trimEnd()

        val state = CharGrid.parse(input)

        // When
        val result = simulate(state)
        println(result)

        // Then
        assertEquals(41, result.count('X'))
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val input = MR.assets.day06.input.readText().trimEnd()

        val state = CharGrid.parse(input)

        // When
        val result = simulate(state)
        println(result)

        // Then
        assertEquals(4722, result.count('X'))
    }

    fun obstructions(initialState: CharGrid): Int {
        val initialLocation = initialState.locations('^').single()
        val walkedState = simulate(initialState)
        val locations = walkedState.locations('X')

        val chunkCount = 8
        val chunkSize = ceil(locations.size / chunkCount.toDouble()).toInt()
        val chunks = locations.chunked(chunkSize)

        val count = runBlocking(Dispatchers.Default) {
            chunks
                .map { chunk ->
                    async {
                        chunk.fold(0) { acc, location ->
                            if (location == initialLocation) {
                                acc
                            } else {
                                val state = initialState.copy()
                                state.set(location.x, location.y, 'O')

                                try {
                                    simulate(state)
                                    acc
                                } catch (e: GuardLoopException) {
                                    acc + 1
                                }
                            }
                        }
                    }
                }
                .awaitAll()
                .sum()
        }

        return count
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val input = MR.assets.day06.sample.readText().trimEnd()
        val state = CharGrid.parse(input)

        // When
        val result = obstructions(state)

        // Then
        assertEquals(6, result)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val input = MR.assets.day06.input.readText().trimEnd()
        val state = CharGrid.parse(input)

        // When
        val result = obstructions(state)

        // Then
        assertEquals(1602, result)
    }
}

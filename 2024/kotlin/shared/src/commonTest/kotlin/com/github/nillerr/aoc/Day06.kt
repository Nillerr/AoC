package com.github.nillerr.aoc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals

class Day06 {

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
                    val c = currentState[x, y]

                    val direction = Direction.valueOf(c)
                    if (direction == null) {
                        continue
                    }

                    val position = Vector2(x, y)

                    val nextX = direction.vector.applyX(x)
                    val nextY = direction.vector.applyY(y)

                    if (currentState.contains(nextX, nextY)) {
                        val next = currentState[nextX, nextY]
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

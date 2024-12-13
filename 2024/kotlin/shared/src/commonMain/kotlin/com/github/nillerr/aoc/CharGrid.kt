package com.github.nillerr.aoc

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

data class Vector2(val x: Int, val y: Int) {
    fun applyX(x: Int) = x + this.x
    fun applyY(y: Int) = y + this.y
}

class CharGrid(val width: Int, val height: Int, val storage: CharArray) {
    private fun index(x: Int, y: Int) = y * width + x

    fun contains(x: Int, y: Int): Boolean {
        return x >= 0 && x < width && y >= 0 && y < height
    }

    operator fun get(x: Int, y: Int): Char {
        return storage[index(x, y)]
    }

    operator fun set(x: Int, y: Int, char: Char) {
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

    inline fun <K : Any, V> groupByNotNull(operation: (Int, Int, Char) -> K?, transform: (Int, Int, Char) -> V): Map<K, List<V>> {
        return buildMap<K, MutableList<V>> {
            forEach { x, y, c ->
                val key = operation(x, y, c)
                if (key != null) {
                    val value = transform(x, y, c)
                    getOrPut(key) { mutableListOf() }.add(value)
                }
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
                        append('·')
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

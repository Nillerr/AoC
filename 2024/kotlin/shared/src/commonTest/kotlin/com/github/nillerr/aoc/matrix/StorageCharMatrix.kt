package com.github.nillerr.aoc.matrix

import kotlin.math.max

class StorageCharMatrix(override val width: Int, override val height: Int) : CharMatrix {
    val storage = CharArray(height * width)

    private fun index(x: Int, y: Int): Int {
        require(x < width) { "X must be less than the width of the matrix" }
        require(y < height) { "Y must be less than the height of the matrix" }
        return y * width + x
    }

    override fun set(x: Int, y: Int, value: String, length: Int) {
        value.toCharArray(storage, index(x, y), 0, length)
    }

    override fun get(x: Int, y: Int): Char {
        return storage[index(x, y)]
    }

    override fun set(x: Int, y: Int, value: Char) {
        storage[index(x, y)] = value
    }

    override fun slice(x: Int, y: Int, width: Int, height: Int): CharMatrixSlice {
        require(x < this.width) { "X must be less than the width of the matrix" }
        require(y < this.height) { "Y must be less than the height of the matrix" }
        return CharMatrixSlice(this, x, y, width, height)
    }

    fun set(x: Int, y: Int, matrix: CharMatrix) {
        for (my in 0..<matrix.height) {
            for (mx in 0..<matrix.width) {
                val m = matrix.get(mx, my)
                if (m != '.') {
                    set(mx + x, my + y, m)
                }
            }
        }
    }

    fun fill(value: Char) {
        for (i in 0..<storage.size) {
            storage[i] = value
        }
    }

    fun normalized(char: Char): CharMatrix {
        val size = max(width, height)

        val matrix = CharMatrix(size, size, char)
        matrix.set(0, 0, this)
        return matrix
    }

    fun transposed(char: Char): CharMatrix {
        val normal = normalized(char)

        val matrix = CharMatrix(normal.width, normal.height, char)
        for (y in 0..<normal.height) {
            for (x in 0..<normal.width) {
                if (y != x) {
                    matrix.set(y, x, normal.get(x, y))
                    matrix.set(x, y, normal.get(y, x))
                }
            }
        }

        return matrix
    }

//    fun rotated(char: Char): CharMatrix {
//        val transposed = transposed(char)
//
//        val matrix = CharMatrix(transposed.width, transposed.height, char)
//
//        for (y in 0..<transposed.height) {
//            for (x in 0..<transposed.width) {
//                if (y != x) {
//                    matrix.set(y, x, transposed.get(y, ))
//                    val c = transposed.get(x, y)
//                    val rx = x
//                }
//            }
//        }
//
//        return matrix
//    }

    override fun toString(): String {
        return buildString {
            val line = CharArray(width)
            for (y in 0 until height) {
                storage.copyInto(line, 0, index(0, y), index(width - 1, y) + 1)
                appendLine(line)
            }
        }
    }
}

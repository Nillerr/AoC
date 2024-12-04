package com.github.nillerr.aoc.matrix

class CharMatrixSlice(
    val matrix: CharMatrix,
    val x: Int,
    val y: Int,
    override val width: Int,
    override val height: Int
) : CharMatrix {
    override fun set(x: Int, y: Int, value: String, length: Int) {
        require(x < width) { "X must be less than the width of the matrix (slice)" }
        require(y < height) { "Y must be less than the height of the matrix (slice)" }
        require(length <= width) { "Length must be less than the width of the matrix (slice)" }

        return matrix.set(this.x + x, this.y + y, value, length)
    }

    override fun get(x: Int, y: Int): Char {
        require(x < width) { "X must be less than the width of the matrix (slice)" }
        require(y < height) { "Y must be less than the height of the matrix (slice)" }
        return matrix.get(this.x + x, this.y + y)
    }

    override fun set(x: Int, y: Int, value: Char) {
        require(x < width) { "X must be less than the width of the matrix (slice)" }
        require(y < height) { "Y must be less than the height of the matrix (slice)" }
        matrix.set(this.x + x, this.y + y, value)
    }

    override fun slice(x: Int, y: Int, width: Int, height: Int): CharMatrixSlice {
        return CharMatrixSlice(this, x, y, width, height)
    }

    fun matches(mask: CharMatrix): Boolean {
        for (y in 0..<height) {
            for (x in 0..<width) {
                val c = get(x, y)
                val m = mask.get(x, y)
                if (m != '.' && m != c) {
                    return false
                }
            }
        }

        return true
    }

    override fun toString(): String {
        return buildString {
            for (y in 0..<height) {
                for (x in 0..<width) {
                    append(get(x, y))
                }
                appendLine()
            }
        }
    }
}

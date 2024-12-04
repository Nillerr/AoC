package com.github.nillerr.aoc.matrix

interface CharMatrix {
    val width: Int
    val height: Int

    fun set(x: Int, y: Int, value: String, length: Int = value.length)
    fun get(x: Int, y: Int): Char
    fun set(x: Int, y: Int, value: Char)
    fun slice(x: Int, y: Int, width: Int, height: Int): CharMatrixSlice
}

fun String.toCharMatrix(): CharMatrix {
    val lines = lines()

    val height = when {
        lines.last().isEmpty() -> lines.size - 1
        else -> lines.size
    }

    val width = lines[0].length

    val matrix = StorageCharMatrix(width, height)
    for (y in 0..<height) {
        val line = lines[y]
        matrix.set(0, y, line)
    }

    return matrix
}
fun CharMatrix(width: Int, height: Int, value: Char): StorageCharMatrix {
    val matrix = StorageCharMatrix(width, height)
    matrix.fill(value)
    return matrix
}

fun CharMatrix.masked(masks: List<CharMatrix>, char: Char): Pair<CharMatrix, Int> {
    val masked = CharMatrix(width, height, char)

    var matches = 0
    masks.forEach { mask ->
        val maxY = height - mask.height
        val maxX = width - mask.width
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                val slice = slice(x, y, mask.width, mask.height)
                if (slice.matches(mask)) {
                    matches += 1
                    masked.set(x, y, mask)
                }
            }
        }
    }

    return masked to matches
}

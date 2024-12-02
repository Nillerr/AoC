package com.github.nillerr.aoc

inline fun <T, R> Sequence<T>.partition(predicate: (T) -> Boolean, transform: (List<T>) -> R): Pair<R, R> {
    val (first, second) = partition(predicate)
    return transform(first) to transform(second)
}

fun partition(input: String): Pair<List<Int>, List<Int>> {
    return input.lineSequence()
        .filter { line -> line.isNotBlank() }
        .flatMap { line -> line.split("   ").mapIndexed { col, value -> col to value.toInt() } }
        .partition({ (col, _) -> col == 0 }, { values -> values.map { it.second }.sorted() })
}

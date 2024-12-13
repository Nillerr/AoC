package com.github.nillerr.aoc

import kotlin.test.Test
import kotlin.test.assertEquals

class Day09 {
    sealed interface Block {
        data class File(val id: Int, val size: Int) : Block {
            override fun toString(): String {
                return id.toString()
            }
        }

        data object Space : Block {
            override fun toString(): String {
                return "."
            }
        }
    }

    data class DiskMap(val blocks: List<Block>) {
        override fun toString(): String {
            return blocks.joinToString("")
        }

        fun compacted(): DiskMap {
            return DiskMap(
                buildList {
                    addAll(blocks)

                    for (i in indices) {
                        val block = this[i]
                        if (block is Block.Space) {
                            var j = size - 1
                            while (j > i) {
                                val otherBlock = this[j]
                                if (otherBlock is Block.File) {
                                    set(j, set(i, otherBlock))
                                    break
                                }

                                j--
                            }
                        }
                    }
                }
            )
        }

        fun checksum(): Long {
            var checksum = 0L
            for (i in blocks.indices) {
                val block = blocks[i]
                if (block is Block.File) {
                    checksum += block.id * i
                }
            }
            return checksum
        }

        fun defragmented(): DiskMap {
            println(this)
            return DiskMap(
                buildList {
                    addAll(blocks)

                    for (spaceStart in indices) {
                        val space = this[spaceStart]
                        if (space !is Block.Space) continue

                        var contiguousSpace = 1
                        for (i in spaceStart + 1 until size) {
                            if (this[i] is Block.Space) {
                                contiguousSpace += 1
                            } else {
                                break
                            }
                        }

                        var fileEnd = size - 1
                        while (fileEnd > spaceStart) {
                            val file = this[fileEnd]
                            if (file !is Block.File) {
                                fileEnd -= 1
                                continue
                            }

                            var fileSize = 1
                            for (i in fileEnd - 1 downTo spaceStart + 1) {
                                val cursor = this[i]
                                if (cursor is Block.File && cursor.id == file.id) {
                                    fileSize += 1
                                } else {
                                    break
                                }
                            }

                            if (fileSize > contiguousSpace) {
                                fileEnd -= fileSize
                                continue
                            }

                            for (i in 0 until fileSize) {
                                set(spaceStart + i, get(fileEnd - i))
                                set(fileEnd - i, Block.Space)
                            }

                            println(DiskMap(this))

                            break
                        }
                    }
                }
            )
        }

        fun poorlyDefragmented(): DiskMap {
            return DiskMap(
                buildList {
                    addAll(blocks)

                    for (fileEnd in size - 1 downTo 0) {
                        val file = this[fileEnd]
                        if (file !is Block.File) {
                            continue
                        }

                        for (spaceStart in 0 until fileEnd) {
                            val space = this[spaceStart]
                            if (space !is Block.Space) {
                                continue
                            }

                            var contiguousSpace = 1
                            for (i in spaceStart + 1 until size) {
                                if (this[i] is Block.Space) {
                                    contiguousSpace += 1
                                } else {
                                    break
                                }
                            }

                            if (contiguousSpace < file.size) {
                                continue
                            }

                            for (i in 0 until file.size) {
                                set(spaceStart + i, get(fileEnd - i))
                                set(fileEnd - i, Block.Space)
                            }

                            break
                        }
                    }
                }
            )
        }
    }

    enum class BlockMode {
        FILE,
        SPACE,
    }

    fun blocksOf(input: String): List<Block> {
        return buildList {
            var mode = BlockMode.FILE

            var fileId = 0
            for (char in input) {
                val size = char.digitToInt()
                when (mode) {
                    BlockMode.FILE -> {
                        val file = Block.File(fileId, size)
                        repeat(size) { add(file) }

                        fileId += 1
                        mode = BlockMode.SPACE
                    }
                    BlockMode.SPACE -> {
                        val space = Block.Space
                        repeat(size) { add(space) }

                        mode = BlockMode.FILE
                    }
                }
            }
        }
    }

    fun diskMapOf(input: String): DiskMap {
        return DiskMap(blocksOf(input))
    }

    @Test
    fun `part 1 - sample`() {
        // Given
        val input = diskMapOf(MR.assets.day09.sample.readText().trim())

        // When
        val compacted = input.compacted()
        val result = compacted.checksum()

        // Then
        assertEquals(1928, result)
    }

    @Test
    fun `part 1 - input`() {
        // Given
        val input = diskMapOf(MR.assets.day09.input.readText().trim())

        // When
        val compacted = input.compacted()
        val result = compacted.checksum()

        // Then
        assertEquals(6384282079460, result)
    }

    @Test
    fun `part 2 - sample`() {
        // Given
        val input = diskMapOf(MR.assets.day09.sample.readText().trim())

        // When
        val defragmented = input.poorlyDefragmented()
        val result = defragmented.checksum()

        // Then
        assertEquals(2858, result)
    }

    @Test
    fun `part 2 - input`() {
        // Given
        val input = diskMapOf(MR.assets.day09.input.readText().trim())

        // When
        val defragmented = input.poorlyDefragmented()
        val result = defragmented.checksum()

        // Then
        assertEquals(6408966547049, result)
    }
}

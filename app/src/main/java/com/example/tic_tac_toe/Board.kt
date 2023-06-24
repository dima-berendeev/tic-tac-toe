package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable

interface Board {
    val size: Int
    fun getCellPlayer(row: Int, col: Int): PlayerType?

    fun isEmpty(row: Int, col: Int): Boolean {
        return getCellPlayer(row, col) == null
    }

    fun isWin(): Boolean {
        for (r in 0 until size) {
            var rowWin = true
            for (c in 1 until size) {
                if (getCellPlayer(r, c) == null || getCellPlayer(r, c) != getCellPlayer(r, c - 1)) {
                    rowWin = false
                }
            }
            if (rowWin) return true
        }
        for (c in 0 until size) {
            var colWin = true
            for (r in 1 until size) {
                if (getCellPlayer(r, c) == null || getCellPlayer(r, c) != getCellPlayer(r - 1, c)) {
                    colWin = false
                }
            }
            if (colWin) return true
        }
        var mainDiagonalWin = true
        var secondaryDiagonalWin = true
        for (i in 1 until size) {
            if (getCellPlayer(i, i) == null || getCellPlayer(i, i) != getCellPlayer(i - 1, i - 1)) {
                mainDiagonalWin = false
            }
            if (
                getCellPlayer(i, size - 1 - i) == null ||
                getCellPlayer(i, size - 1 - i) != getCellPlayer(i - 1, size - i)
            ) {
                secondaryDiagonalWin = false
            }
        }

        return mainDiagonalWin || secondaryDiagonalWin
    }

    fun isDraw(): Boolean {
        for (r in 0..2) {
            for (c in 0..2) {
                if (getCellPlayer(r, c) == null) return false
            }
        }
        return true
    }

}


@Immutable
interface ImmutableBoard : Board

interface MutableBoard : Board {
    fun putCellPlayer(row: Int, col: Int, playerType: PlayerType)
    fun clearCell(row: Int, col: Int)
}

fun Board.createImmutableCopy(): ImmutableBoard {
    val content = List(size) { row -> List(size) { col -> getCellPlayer(row, col) } }
    val size = size
    return object : ImmutableBoard {
        override val size = size

        override fun getCellPlayer(row: Int, col: Int): PlayerType? {
            return content[row][col]
        }


        override fun toString(): String {
            return "Board(rows=$content)"
        }
    }
}

fun Board.createMutableCopy(): MutableBoard {
    val content = MutableList(size) { row -> MutableList(size) { col -> getCellPlayer(row, col) } }
    return MutableBoard(size, content)
}

fun MutableBoard(
    size: Int,
    content: MutableList<MutableList<PlayerType?>> = createMutableEmptyContent(size)
): MutableBoard {
    return object : MutableBoard {

        override val size = size

        override fun getCellPlayer(row: Int, col: Int): PlayerType? {
            return content[row][col]
        }

        override fun putCellPlayer(row: Int, col: Int, playerType: PlayerType) {
            if (content[row][col] != playerType) {
                content[row][col] = playerType
            }
        }

        override fun clearCell(row: Int, col: Int) {
            content[row][col] = null
        }
    }
}

private fun createMutableEmptyContent(size: Int): MutableList<MutableList<PlayerType?>> {
    return MutableList(size) {
        MutableList(size) {
            null
        }
    }
}
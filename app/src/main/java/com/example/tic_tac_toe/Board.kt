package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable

class Board(
    val size: Int,
    val content: MutableList<MutableList<PlayerType?>> = createContent(size)
) {

    fun getCellPlayer(row: Int, col: Int): PlayerType? {
        return content[row][col]
    }

    fun putCellPlayer(row: Int, col: Int, playerType: PlayerType) {
        if (content[row][col] != playerType) {
            content[row][col] = playerType
        }
    }

    fun clearCell(row: Int, col: Int) {
        content[row][col] = null
    }

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

    override fun toString(): String {
        return "Board(rows=$content)"
    }
}


fun Board.getSnapshot(): BoardSnapshot {
    val content: List<List<PlayerType?>> = List(size) { row -> List(size) { col -> getCellPlayer(row, col) } }
    val size = size
    return BoardSnapshotImpl(size, content)
}



@Immutable
private class BoardSnapshotImpl constructor(override val size: Int, private val content: List<List<PlayerType?>>) : BoardSnapshot {

    override fun getCellPlayer(row: Int, col: Int): PlayerType? {
        return content[row][col]
    }

    override fun toString(): String {
        return "BoardSnapshot(size=$size, content=$content)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardSnapshotImpl

        if (size != other.size) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + content.hashCode()
        return result
    }
}

fun BoardSnapshot.createBoard(): Board {
    val content = MutableList(size) { row -> MutableList(size) { col -> getCellPlayer(row, col) } }
    return Board(size, content)
}

private fun createContent(size: Int): MutableList<MutableList<PlayerType?>> {
    return MutableList(size) {
        MutableList(size) {
            null
        }
    }
}

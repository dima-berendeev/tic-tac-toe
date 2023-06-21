package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable

class Board(val size: Int) {
    private val content: List<MutableList<PlayerType?>> = createEmptyContent()

    constructor(snapshot: Board.Snapshot) : this(snapshot.size) {
        for (row in 0 until snapshot.size) {
            for (col in 0 until snapshot.size) {
                content[row][col] = snapshot.getCellPlayer(row, col)
            }
        }
    }

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
        return content[row][col] == null
    }


    fun createBoardSnapshot(): Snapshot {
        val content = List(size) { row ->
            List(size) { col ->
                content[row][col]
            }
        }
        return SnapshotImpl(size, content)
    }

    fun isWin(): Boolean {
        for (r in 0..2) {
            if (content[r][0] != null && content[r][0] == content[r][1] && content[r][0] == content[r][2]) {
                return true
            }
        }
        for (c in 0..2) {
            if (content[0][c] != null && content[0][c] == content[1][c] && content[0][c] == content[2][c]) {
                return true
            }
        }
        if (content[0][0] != null && content[0][0] == content[1][1] && content[1][1] == content[2][2]) {
            return true
        }
        if (content[0][2] != null && content[0][2] == content[1][1] && content[1][1] == content[2][0]) {
            return true
        }
        return false
    }

    fun isDraw(): Boolean {
        for (r in 0..2) {
            for (c in 0..2) {
                if (content[r][c] == null) return false
            }
        }
        return true
    }

    private fun createEmptyContent(): List<MutableList<PlayerType?>> = (1..size).map { MutableList(size) { null } }

    override fun toString(): String {
        return "Board(rows=$content)"
    }

    @Immutable
    interface Snapshot {
        val size: Int
        fun getCellPlayer(row: Int, col: Int): PlayerType?
    }

    private class SnapshotImpl(override val size: Int, val content: List<List<PlayerType?>>) : Snapshot {
        override fun getCellPlayer(row: Int, col: Int): PlayerType? {
            return content[row][col]
        }
    }
}
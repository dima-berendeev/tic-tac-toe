package com.example.tic_tac_toe

@JvmInline
value class Row(val asInt: Int)

@JvmInline
value class Column(val asInt: Int)

class Board {
    var size: Int
    private val rows: List<MutableList<PlayerType?>>

    constructor(size: Int) {
        this.size = size
        rows = (1..size).map { MutableList(size) { null } }
    }

    private constructor(size: Int, rows: List<MutableList<PlayerType?>>) {
        this.size = size
        this.rows = rows
    }

    fun getCellPlayer(r: Int, c: Int): PlayerType? {
        return rows[r][c]
    }

    fun putCellPlayer(r: Int, c: Int, playerType: PlayerType) {
        rows[r][c] = playerType
    }

    fun clearCell(r: Int, c: Int) {
        rows[r][c] = null
    }

    fun isEmpty(r: Int, c: Int): Boolean {
        return rows[r][c] == null
    }

    fun createCopy(): Board {
        val rowsCopy = rows.map { mutableListOf<PlayerType?>().apply { addAll(it) } }
        return Board(size, rowsCopy)
    }


    fun isWin(): Boolean {
        for (r in 0..2) {
            if (rows[r][0] != null && rows[r][0] == rows[r][1] && rows[r][0] == rows[r][2]) {
                return true
            }
        }
        for (c in 0..2) {
            if (rows[0][c] != null && rows[0][c] == rows[1][c] && rows[0][c] == rows[2][c]) {
                return true
            }
        }
        if (rows[0][0] != null && rows[0][0] == rows[1][1] && rows[1][1] == rows[2][2]) {
            return true
        }
        if (rows[0][2] != null && rows[0][2] == rows[1][1] && rows[1][1] == rows[2][0]) {
            return true
        }
        return false
    }

    fun isDraw(): Boolean {
        for (r in 0..2) {
            for (c in 0..2) {
                if (rows[r][c] == null) return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "Board(rows=$rows)"
    }
}
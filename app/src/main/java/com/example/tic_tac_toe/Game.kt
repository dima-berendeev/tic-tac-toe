package com.example.tic_tac_toe

class Game {
    private var board: List<MutableList<CellState>> = getInitBoard()
    private var mode: Mode = Mode.CrossMove

    fun getMode(): Mode {
        return mode
    }

    fun reset() {
        board = getInitBoard()
        mode = Mode.CrossMove
    }

    fun getBoard(): List<List<CellState>> {
        return listOf(
            board[0].toList(),
            board[1].toList(),
            board[2].toList(),
        )
    }

    fun makeMove(r: Int, c: Int) {
        if (board[r][c] != CellState.Empty) return
        when (mode) {
            Mode.CrossMove -> {
                board[r][c] = CellState.Cross
            }
            Mode.NoughtMove -> {
                board[r][c] = CellState.Nought
            }
            else -> {
                // game is finished
                return
            }
        }
        mode = when {
            isWin() -> {
                when (mode) {
                    Mode.CrossMove -> Mode.CrossWin
                    Mode.NoughtMove -> Mode.NoughtWin
                    else -> throw IllegalStateException()
                }
            }
            isDraw() -> {
                Mode.Draw
            }
            else -> {
                when (mode) {
                    Mode.CrossMove -> Mode.NoughtMove
                    Mode.NoughtMove -> Mode.CrossMove
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    private fun isWin(): Boolean {
        for (r in 0..2) {
            if (board[r][0] != CellState.Empty && board[r][0] == board[r][1] && board[r][0] == board[r][2]) {
                return true
            }
        }
        for (c in 0..2) {
            if (board[0][c] != CellState.Empty && board[0][c] == board[1][c] && board[0][c] == board[2][c]) {
                return true
            }
        }
        return false
    }

    private fun isDraw(): Boolean {
        for (r in 0..2) {
            for (c in 0..2) {
                if (board[r][c] == CellState.Empty) return false
            }
        }
        return true
    }

    private fun getInitBoard(): List<MutableList<CellState>> {
        return listOf(
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
        )
    }

    enum class Mode {
        CrossMove,
        NoughtMove,
        CrossWin,
        NoughtWin,
        Draw;

        fun isFinished(): Boolean {
            return this != CrossMove && this != NoughtMove
        }
    }
}

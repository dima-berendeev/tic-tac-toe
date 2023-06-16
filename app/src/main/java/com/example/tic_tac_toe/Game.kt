package com.example.tic_tac_toe

class Game {
    private val board: List<MutableList<CellState>> = getInitBoard()
    private var turn: CellState = CellState.Cross

    fun getTurn(): CellState {
        return turn
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
        board[r][c] = turn
        switchTurn()
    }

    private fun switchTurn() {
        turn = if (turn == CellState.Cross) {
            CellState.Nought
        } else {
            CellState.Cross
        }
    }


    private fun getInitBoard(): List<MutableList<CellState>> {
        return listOf(
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
            mutableListOf(CellState.Empty, CellState.Empty, CellState.Empty),
        )
    }
}
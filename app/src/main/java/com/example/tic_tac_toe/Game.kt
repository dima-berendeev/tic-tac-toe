package com.example.tic_tac_toe

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Game(private val boardProvider: () -> Board) {

    fun launch(): Flow<State> = flow {
        val board = boardProvider()
        var playerType = PlayerType.Cross
        var winner: PlayerType? = null
        var isGameDraw = false
        while (winner == null && !isGameDraw) {
            when {
                board.isWin() -> {
                    winner = playerType.other
                    emit(State(board.getSnapshot(), Mode.Win(winner)))
                }
                board.isDraw() -> {
                    isGameDraw = true
                    emit(State(board.getSnapshot(), Mode.Draw))
                }
                else -> {
                    val deferredMove = CompletableDeferred<Coordinates>()

                    emit(State(board.getSnapshot(), Mode.Move(playerType, deferredMove)))
                    val move = deferredMove.await()

                    if (board.isEmpty(move.row, move.col)) {
                        board.putCellPlayer(move.row, move.col, playerType)
                        playerType = playerType.other
                    }
                }
            }
        }
    }

    data class State(
        val boardSnapshot: BoardSnapshot,
        val mode: Mode,
    )

    sealed interface Mode {
        data class Move(val playerType: PlayerType, val deferredMove: CompletableDeferred<Coordinates>) : Mode
        data class Win(val player: PlayerType) : Mode
        object Draw : Mode
    }

    data class Coordinates(val row: Int, val col: Int)
}

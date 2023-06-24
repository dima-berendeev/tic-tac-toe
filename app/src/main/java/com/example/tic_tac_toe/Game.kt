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
                    val deferredPlayerMove = CompletableDeferred<PlayerMove>()
                    val moveAction: (PlayerMove) -> Unit = { coordinates -> deferredPlayerMove.complete(coordinates) }
                    emit(State(board.getSnapshot(), Mode.Move(playerType, moveAction)))
                    val move = deferredPlayerMove.await()

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
        data class Move(val playerType: PlayerType, val moveAction: (PlayerMove) -> Unit) : Mode
        data class Win(val playerType: PlayerType) : Mode
        object Draw : Mode
    }
}

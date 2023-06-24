package com.example.tic_tac_toe

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Round(private val boardProvider: () -> Board) {

    fun launch(firstMove: PlayerType): Flow<State> = flow {
        val board = boardProvider()
        var playerType = firstMove
        var winner: PlayerType? = null
        var isGameDraw = false
        while (winner == null && !isGameDraw) {
            when {
                board.isWin() -> {
                    winner = playerType.anouther
                    emit(Finished(board.getSnapshot(), winner))
                }
                board.isDraw() -> {
                    isGameDraw = true
                    emit(Finished(board.getSnapshot(), null))
                }
                else -> {
                    val deferredPlayerMove = CompletableDeferred<PlayerMove>()
                    val moveAction: (PlayerMove) -> Unit = { coordinates -> deferredPlayerMove.complete(coordinates) }
                    emit(Move(board.getSnapshot(), playerType, moveAction))
                    val move = deferredPlayerMove.await()
                    if (board.isEmpty(move.row, move.col)) {
                        board.putCellPlayer(move.row, move.col, playerType)
                        playerType = playerType.anouther
                    }
                }
            }
        }
    }

    sealed interface State {
        val boardSnapshot: BoardSnapshot
    }

    data class Move(override val boardSnapshot: BoardSnapshot, val playerType: PlayerType, val moveAction: (PlayerMove) -> Unit) : State
    data class Finished(override val boardSnapshot: BoardSnapshot, val playerType: PlayerType?) : State
}

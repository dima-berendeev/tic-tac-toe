package com.example.tic_tac_toe

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Game(private val board: Board) {
    val stateFlow = MutableStateFlow<State?>(null)
    private var scope = MainScope()

    fun start() {
        launchMainCycle()
    }

    fun stop() {
        scope.cancel()
        scope = MainScope()
        launchMainCycle()
    }

    private fun launchMainCycle() {
        scope.launch {
            var playerType = PlayerType.Cross
            var winner: PlayerType? = null
            var isGameDraw = false
            while (isActive && winner == null && !isGameDraw) {
                when {
                    board.isWin() -> {
                        winner = playerType.other
                        stateFlow.value = State(board.createBoardSnapshot(), Mode.Win(winner))
                    }
                    board.isDraw() -> {
                        isGameDraw = true
                        stateFlow.value = State(board.createBoardSnapshot(), Mode.Draw)
                    }
                    else -> {
                        val deferredMove = CompletableDeferred<Coordinates>()

                        stateFlow.value = State(board.createBoardSnapshot(), Mode.Move(playerType, deferredMove))
                        val move = deferredMove.await()

                        if (board.isEmpty(move.row, move.col)) {
                            board.putCellPlayer(move.row, move.col, playerType)
                            playerType = playerType.other
                        }
                    }
                }
            }
        }
    }

    data class State(
        val boardSnapshot: Board.Snapshot,
        val mode: Mode,
    )

    sealed interface Mode {
        data class Move(val playerType: PlayerType, val deferredMove: CompletableDeferred<Coordinates>) : Mode
        data class Win(val player: PlayerType) : Mode
        object Draw : Mode
    }

    data class Coordinates(val row: Int, val col: Int)
}

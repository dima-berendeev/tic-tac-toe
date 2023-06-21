package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GameViewModel : ViewModel() {
    private var board = Board(3)
    private var game = Game(board)
    val viewState = game.stateFlow.filterNotNull().map { gameState ->
        val mode = gameState.mode
        if (mode is Game.Mode.Move && mode.playerType == PlayerType.Nought) {
            val calculator = OptimalMoveCalculator(Board(gameState.boardSnapshot), mode.playerType)
            val optimalMove = calculator.findOptimalMove()!!
            mode.deferredMove.complete(Game.Coordinates(optimalMove.row, optimalMove.column))
        }
        ViewState(gameState.boardSnapshot, mode)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ViewState())

    init {
        game.start()
    }

    fun onResetClick() {
    }
}

@Immutable
data class ViewState(
    val board: Board.Snapshot? = null,
    val mode: Game.Mode? = null
) {
    fun isFinished(): Boolean {
        return mode !is Game.Mode.Move
    }
}

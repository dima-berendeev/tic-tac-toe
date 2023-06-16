package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class GameViewModel : ViewModel() {
    private val game = Game()
    val viewState = MutableStateFlow(ViewState())

    init {
        updateUi()
    }

    fun onBoardCellClick(r: Int, c: Int) {
        game.makeMove(r, c)
        updateUi()
    }

    private fun updateUi() {
        viewState.value = ViewState(
            board = game.getBoard(),
            turn = game.getTurn()
        )
    }
}

@Immutable
data class ViewState(
    val board: List<List<CellState>>? = null,
    val turn: CellState? = null
)


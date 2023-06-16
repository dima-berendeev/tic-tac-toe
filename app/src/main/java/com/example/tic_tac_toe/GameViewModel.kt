package com.example.tic_tac_toe

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class GameViewModel : ViewModel() {
    private val game = Game()
    val viewState = MutableStateFlow(ViewState())

    init {
        updateUi()
    }

    private fun updateUi() {
        viewState.value = ViewState(game.getBoard())
    }

    fun onBoardCellClick(r: Int, c: Int) {
        game.makeMove(r, c)
        updateUi()
    }
}

data class ViewState(
    val board: List<List<CellState>>? = null
)


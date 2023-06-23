package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel : ViewModel() {
    val gameFactory: GameFactory = GameFactory()
    val viewState = MutableStateFlow(ViewState())
    private val gameSession = MutableStateFlow<UUID>(UUID.randomUUID())

    init {
        gameSession.flatMapLatest {
            gameFactory.get().launch()
        }.onEach { gameState ->
            val mode = gameState.mode
            if (mode is Game.Mode.Move && mode.playerType == PlayerType.Nought) {
                val calculator = OptimalMoveCalculator(Board(gameState.boardSnapshot), mode.playerType)
                val optimalMove = calculator.findOptimalMove()!!
                mode.deferredMove.complete(Game.Coordinates(optimalMove.row, optimalMove.column))
            }
            viewState.value = (ViewState(gameState.boardSnapshot, mode))
        }.launchIn(viewModelScope)
    }

    fun onResetClick() {
        gameSession.tryEmit(UUID.randomUUID())
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

class GameFactory() {
    fun get(): Game {
        return Game(Board(3))
    }
}

package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
                val calculator = OptimalMoveCalculator(gameState.boardSnapshot.createMutableCopy(), mode.playerType)
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
    val boardSnapshot: ImmutableBoard? = null,
    val mode: Game.Mode? = null
) {
    fun isFinished(): Boolean {
        return mode !is Game.Mode.Move
    }
}

class GameFactory() {
    fun get(): Game {
        return Game { MutableBoard(3) }
    }
}

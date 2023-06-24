package com.example.tic_tac_toe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel : ViewModel() {
    val viewState = MutableStateFlow(ViewState())
    private val gameFactory: GameFactory = GameFactory()
    private val gameSession = MutableStateFlow<UUID>(UUID.randomUUID())
    private val autoPlayer = PlayerType.Nought

    init {
        viewModelScope.launch {
            launchMainCycle()
        }
    }

    private suspend fun launchMainCycle() {
        val gameFlow = gameFactory.get().launch()
        var boardSnapshot: BoardSnapshot
        var viewStateMode: ViewState.Mode

        gameFlow.collect { gameState ->
            boardSnapshot = gameState.boardSnapshot
            viewStateMode = when (gameState.mode) {
                is Game.Mode.Move -> {
                    if (gameState.mode.playerType == autoPlayer) {
                        ViewState.AutoPlayerMove(gameState.mode.playerType)
                    } else {
                        val playerMoveAction: (PlayerMove) -> Unit = { playerMove -> gameState.mode.moveAction(playerMove) }
                        ViewState.RealPlayerMove(gameState.mode.playerType, playerMoveAction)
                    }
                }
                Game.Mode.Draw -> ViewState.Draw
                is Game.Mode.Win -> ViewState.Win(gameState.mode.playerType)
            }
            viewState.value = (ViewState(boardSnapshot, viewStateMode))

            if (gameState.mode is Game.Mode.Move && gameState.mode.playerType == autoPlayer) {
                val calculator = OptimalMoveCalculator(gameState.boardSnapshot.createBoard(), gameState.mode.playerType)
                delay(2000)
                val optimalMove = calculator.findOptimalMove()!!
                gameState.mode.moveAction(PlayerMove(optimalMove.row, optimalMove.column))
            }
        }
    }

    fun onResetClick() {
//        gameSession.tryEmit(UUID.randomUUID())
    }
}

@Immutable
data class ViewState(
    val boardSnapshot: BoardSnapshot? = null,
    val mode: Mode? = null
) {
    fun isFinished(): Boolean {
        return mode is Win || mode is Draw
    }

    sealed interface Mode
    data class AutoPlayerMove(val playerType: PlayerType) : Mode
    data class RealPlayerMove(val playerType: PlayerType, val playerMoveAction: (PlayerMove) -> Unit) : Mode
    data class Win(val playerType: PlayerType) : Mode
    object Draw : Mode
}

class GameFactory {
    fun get(): Game {
        return Game { Board(3) }
    }
}
